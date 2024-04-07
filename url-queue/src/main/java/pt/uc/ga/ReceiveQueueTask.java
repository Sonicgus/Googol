package pt.uc.ga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveQueueTask implements Runnable {

    private ServerSocket serverSocket;
    private final UrlQueue urlQueue;

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
                receiveUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveUrl() throws IOException {
        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String url;
        boolean resend = false;
        while ((url = in.readLine()) != null) {
            if (url.startsWith("[RESEND]")) {
                url = url.substring(8);
                System.out.println("[RE-ADDED]: " + url);
                resend = true;
            }
            urlQueue.addUrl(url, resend);
        }
    }
}
