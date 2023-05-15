package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try(
            final ServerSocket serverSocket = new ServerSocket(5000);
        ){
            while(true){
                Socket clientSocket = serverSocket.accept();
                PrintWriter outputWriter = new PrintWriter(clientSocket.getOutputStream());
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                Thread request = new Thread(new Runnable() {
                    @Override
                    public void run(){
                        System.out.println("Listening for Request");
                        try{
                            while(clientSocket.isConnected()){
                                String msg = inputReader.readLine();
                                if(clientSocket.isClosed()) break;
                                System.out.println("Sending File...");
                                sendFile(String.format("C:\\Users\\gmgon\\Desktop\\Desktop\\JSS\\serverFiles\\%s", msg), outputStream);
                                
                                //TODO: Handle P2P chat request
                            }
                        } catch(IOException e){
                            e.printStackTrace();
                        } finally {
                                try {
                                    outputWriter.close();
                                    outputStream.close();
                                    inputReader.close();
                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                            }
                        }
                    }
                });
                request.start();

                try {
                    request.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(String path, DataOutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int bytes = 0;

        File file = new File(path);
        if(file.exists() && !file.isDirectory()) {
            FileInputStream file_stream = new FileInputStream(file);

            outputStream.writeLong(file.length());

            while((bytes = file_stream.read(buffer)) != -1){
                outputStream.write(buffer, 0, bytes);
            }
            file_stream.close();
        } else throw new IOException();
        
    }
}
