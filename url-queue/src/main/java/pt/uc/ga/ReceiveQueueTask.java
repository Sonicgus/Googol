package pt.uc.ga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveQueueTask implements Runnable {

    private ServerSocket serverSocket;
    private final UrlQueue urlQueue;
    Socket socket;
    BufferedReader in;

    public ReceiveQueueTask(int port, UrlQueue urlQueue) {
        this.urlQueue = urlQueue;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void run() {
        while (true) {
            try {
                socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String url;
                while (true) {
                    if ((url = in.readLine()) != null) {
                        urlQueue.addUrl(url);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    in.close();
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
