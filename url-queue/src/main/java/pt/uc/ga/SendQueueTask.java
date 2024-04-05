package pt.uc.ga;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SendQueueTask implements Runnable {
    public SendQueueTask(int port, UrlQueue urlQueue) {
        this.port = port;
        this.urlQueue = urlQueue;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerSocket serverSocket;
    private int port;
    private UrlQueue urlQueue;

    @Override
    public void run() {
        while (true) {
            try {
                sendUrl();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUrl() throws IOException {
        String url;
        url = urlQueue.getUrl();

        if (url != null) {
            try (Socket socket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println(url);
                System.out.println("Sent url: " + url);
            }
        }
    }
}
