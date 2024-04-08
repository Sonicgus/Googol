package pt.uc.ga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Task to receive urls from the crawler.
 */
public class ReceiveQueueTask implements Runnable {

    private ServerSocket serverSocket;
    private final UrlQueue urlQueue;
    Socket socket;
    BufferedReader in;

    /**
     * Constructor.
     *
     * @param port
     * @param urlQueue
     */
    public ReceiveQueueTask(int port, UrlQueue urlQueue) {
        this.urlQueue = urlQueue;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Run method to receive urls from the crawler.
     */
    @Override
    public void run() {
        while (true) {
            try {
                socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String url;
                while ((url = in.readLine()) != null) {

                    if (url.startsWith("resend")) {
                        urlQueue.addUrl(url.substring(6), true);
                    } else {
                        urlQueue.addUrl(url, false);
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
