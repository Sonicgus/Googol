package pt.uc.ga;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Task to send urls to the crawler.
 */
public class SendQueueTask implements Runnable {
    private ServerSocket serverSocket;

    private final UrlQueue urlQueue;

    public SendQueueTask(int port, UrlQueue urlQueue) {
        this.urlQueue = urlQueue;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Run method to send urls to the crawler.
     */
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

    /**
     * Send url to the crawler.
     *
     * @throws IOException
     */
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
