package org.academiadecodigo.ghostbugsters;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer implements Runnable{

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private OutputStream out;

    private void init() throws IOException {

        String header;
        String verb;
        String path;
        File file;




        while(true) {

            connect();

            header = in.readLine();
            System.out.println(header);

            if (!header.contains("GET")) {
                notFound();
                closeConnections();
                continue;
            }


            verb = header.substring(0, header.indexOf(" "));
            path = "/Users/codecadet/joel/workspace/homework/web-server/web-server/www" + header.substring(header.indexOf(" ") + 1, header.indexOf(" ", header.indexOf(" ") + 1));
            file = new File(path);

            if (!file.exists()) {
                notFound();
                closeConnections();
                continue;
            }

            //sendHeader(file);

            sendFile(file);

            closeConnections();


        }


    }

    private String header(File file, String extension){

        String type="";

        switch (extension) {
            case "html":
                type="text";
                break;
            case "png":
            case "jpeg":
            case "jpg":
                type="image";
                break;
            case "mp4":
                type="video";

        }

        System.out.println(file.length());
        System.out.println(extension);
        System.out.println(type);

        return  "HTTP/1.0 200 Document Follows\r\n" +
                "Content-Type: "+type+"/"+extension+" \r\n" +
                "Content-Length: "+file.length()+" \r\n" +
                "\r\n";
    }

    private void sendHeader(File file) throws IOException {
        out.write(header(file, file.getName().substring(file.getName().indexOf(".")+1)).getBytes());
        out.flush();
    }

    private void sendFile(File file) throws IOException {


        FileInputStream fStream = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        fStream.read(buffer);

        int num;
        int i=0;

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        out.write(header(file, file.getName().substring(file.getName().indexOf(".")+1)).getBytes());
        out.write(buffer);
        /*while((num=fStream.read(buffer))!=-1){
            System.out.println(i+=num);
            System.out.println("buffer length: "+num);
            out.write(buffer, 0,num);
        }*/

        fStream.close();

    }

    private void connect() throws IOException {


        serverSocket = new ServerSocket(8080);
        clientSocket = serverSocket.accept();

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new DataOutputStream(clientSocket.getOutputStream());
    }





    private void notFound() throws IOException {
        File file = new File("/Users/codecadet/joel/workspace/homework/web-server/web-server/www/error.html");
        String message ="HTTP/1.0 404 Not Found\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: "+file.length()+" \r\n" +
                "\r\n";
        out.write(message.getBytes());
        sendFile(file);
    }

    private void closeConnections() throws IOException {

        serverSocket.close();
        clientSocket.close();

        in.close();
        out.close();
    }

    public static void main(String[] args) {

            Thread thread = new Thread(new WebServer());
            thread.start();


    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            closeConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
