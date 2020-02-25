package org.academiadecodigo.ghostbugsters;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private OutputStream out;

    private void init() throws IOException {

        String header;
        String[] headers;
        String verb;
        String path;
        File file;


        while(true) {

            startStreams();


            header = in.readLine();
            System.out.println(header);
            headers = header.split(" ");
            System.out.println("tale");

            if(header==null || headers.length<2){
                continue;
            }

            verb = headers[0];
            path = "www" + headers[1];
            file = new File(path);

            if (!verb.equals("GET") || !file.exists()) {
                noFound();
                continue;
            }

            sendHeader(file);

            sendFile(file);
            System.out.println("aqui");

            close();

        }



    }

    private String header(File file, String extension){
        switch (extension) {
            case "html":
                return "HTTP/1.0 200 Document Follows\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: "+file.length()+" \r\n" +
                        "\r\n";
            case "jpg":
                return "HTTP/1.0 200 Document Follows\r\n" +
                        "Content-Type: image/"+extension+" \r\n" +
                        "Content-Length: "+file.length()+" \r\n" +
                        "\r\n";
            case "gif":
            case "png":
            case "jpeg":
                return "HTTP/1.0 200 Document Follows\r\n" +
                        "Content-Type: image/"+extension+" \r\n" +
                        "Content-Length: "+file.length()+" \r\n" +
                        "\r\n";

        }

        return null;
    }

    private void sendHeader(File file) throws IOException {
        out.write(header(file, file.getName().substring(file.getName().indexOf(".")+1)).getBytes());
        out.flush();
    }

    private void sendFile(File file) throws IOException {
        byte[] buffer = new byte[1024];
        FileInputStream fStream = new FileInputStream(file);

        int num;

        while((num=fStream.read(buffer))!=-1){
            out.write(buffer, 0,num);
        }

    }

    private void startStreams() throws IOException {
        serverSocket = new ServerSocket(8080);
        clientSocket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = clientSocket.getOutputStream();


    }

    private void cleanBuffer() throws IOException {
        String line;
        while ((line=in.readLine())!=null) {
            System.out.println(line);
        }
        System.out.println("finished");
    }

    private void noFound() throws IOException {
        String message ="HTTP/1.0 404 Not Found\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: <file_byte_size> \r\n" +
                "\r\n";
        out.write(message.getBytes());
        sendFile(new File("www/404.html"));
    }

    private void close() throws IOException {

        serverSocket.close();
        clientSocket.close();
        in.close();
        out.close();
    }

    public static void main(String[] args) {
        WebServer webServer = new WebServer();

        try {
            webServer.init();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                webServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
