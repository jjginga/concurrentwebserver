package org.academiadecodigo.ghostbugsters;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer{

    private ServerSocket serverSocket;
    public static final String PATH="www";



    private void init(int port) throws IOException {


        String header;
        String verb;
        String path;




        while(true) {

            serverSocket=connect(port);

            Socket clientSocket = serverSocket.accept();

            Thread thread = new Thread(new Request(clientSocket));
            thread.start();
            System.out.println(thread.getName());

            closeConnections();

        }


    }

    private class Request implements Runnable{


        private Socket clientSocket;

        public Request(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
        String header="";
        String verb;
        String path;

            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                header = receiveHeader(in);
            } catch (IOException e) {
                e.printStackTrace();
            }



        System.out.println();


        if (header.isEmpty()) {
            System.out.println("Empty");//Change
            closeConnections();
            return;
        }


        verb = header.substring(0, header.indexOf(" "));

        if (!verb.equals("GET")) {
            System.out.println("Wrong Verb");;
            closeConnections();
            return;
        }

        path = PATH + header.substring(header.indexOf(" ") + 1, header.indexOf(" ", header.indexOf(" ") + 1));
        File file = new File(path);

        System.out.println(verb);
        System.out.println(path);
        System.out.println(file.length());

            DataOutputStream out = null;
            try {
                out = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!file.exists()) {
            try {
                notFound(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeConnections();
            return;
        }




            try {
                sendHeader(out, file);
                sendFile(out, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private String receiveHeader(BufferedReader in) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        String line="";

        while((line=in.readLine())!=null && !line.isEmpty()){
            stringBuilder.append(line);
        }

        return stringBuilder.toString();

    }
    private String header(File file, String extension){

        String type="";
        String charset=" ";

        switch (extension) {
            case "html":
                type="text";
                charset="; charset=UTF-8";
                break;
            case "png":
            case "jpeg":
            case "jpg":
                type="image";
                break;
            case "mp4":
                type="video";

        }

        return  "HTTP/1.0 200 Document Follows\r\n" +
                "Content-Type: "+type+"/"+extension+charset+"\r\n" +
                "Content-Length: "+file.length()+" \r\n" +
                "\r\n";
    }

    private void sendHeader(DataOutputStream out, File file) throws IOException {
        out.writeBytes(header(file, file.getName().substring(file.getName().indexOf(".")+1)));
        out.flush();
    }

    private void sendFile(DataOutputStream out, File file) throws IOException {


        FileInputStream fStream = new FileInputStream(file);

        byte[] buffer = new byte[1024];

        int num;

        while((num=fStream.read(buffer))!=-1){
            out.write(buffer, 0,num);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fStream.close();

    }

    private ServerSocket connect(int port) throws IOException {


        return serverSocket = new ServerSocket(port);

    }

    private void notFound(DataOutputStream out) throws IOException {
        File file = new File("www/error.html");
        String message ="HTTP/1.0 404 Not Found\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: "+file.length()+" \r\n" +
                "\r\n";
        out.write(message.getBytes());
        sendFile(out, file);
    }

    private void closeConnections()  {

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

           WebServer webServer = new WebServer();
        try {
            webServer.init(8085);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
