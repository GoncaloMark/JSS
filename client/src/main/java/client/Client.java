package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (
            Socket clientSocket = new Socket("127.0.0.1", 5000);
            PrintWriter outputWriter = new PrintWriter(clientSocket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
        ) {

            Thread sendRequest = new Thread(new Runnable() {
                String msg;
                @Override
                public void run(){
                    while(clientSocket.isConnected()) {
                        if(clientSocket.isClosed()) break;
                        msg = scanner.nextLine();
                        outputWriter.println(msg);
                        outputWriter.flush();
                    }
                }
            });

            Thread getResponse = new Thread(new Runnable() {
                long size;
                @Override
                public void run(){
                    try {
                        while (clientSocket.isConnected()) {
                            if(clientSocket.isClosed()) break;
                            size = inputStream.readLong();
                            if (size > 0) {
                                receiveFile("index.html", size, inputStream);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            sendRequest.start();
            getResponse.start();
            try {
                sendRequest.join();
                getResponse.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveFile(String outputFile, long size, DataInputStream inputStream) throws IOException {
        String outputDir = "C:\\Users\\gmgon\\Desktop\\Desktop\\JSS\\clientFiles";
        int bytes = 0;
        FileOutputStream f_outputStream = new FileOutputStream(outputDir + File.separator + outputFile);
        byte[] buffer = new byte[4 * 1024];

        while(size > 0 && (bytes = inputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
            f_outputStream.write(buffer, 0, bytes);
            size -= bytes;
        }

        System.out.println("Received File!");

        f_outputStream.close();
    }
}
