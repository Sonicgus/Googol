package pt.uc.ga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveQueueTask implements Runnable {
    public ReceiveQueueTask(int port, UrlQueue urlQueue) {
        this.port = port;
        this.urlQueue = urlQueue;
        try {
            serverSocket = new ServerSocket(port); // Create a server socket for the specified port
        } catch (IOException e) { // Catch any IO exceptions that may occur
            e.printStackTrace(); // Print the stack trace if an exception occurs
        }
    }

    private ServerSocket serverSocket; // Server socket for communication
    private int port; // Port number for the server socket
    private UrlQueue urlQueue; // Reference to the shared UrlQueue instance

    @Override
    public void run() {
        while (true) {
            try {
                receiveUrl();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveUrl() throws IOException {
        try (Socket socket = serverSocket.accept(); // Accept a connection from a client
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) { // Create a
            // BufferedReader
            // for reading
            // from the
            // client
            String url; // Variable to store the received URL
            boolean resend = false; // Flag to indicate if the URL needs to be resent
            while ((url = in.readLine()) != null) { // Read URLs from the client until no more data is available
                if (url.startsWith("[RESEND]")) { // If the received URL is marked for resend
                    url = url.substring(8); // Remove the resend marker
                    System.out.println("[RE-ADDED]: " + url); // Print that the URL is being re-added
                    resend = true; // Set the resend flag to true
                }
                urlQueue.addUrl(url, resend); // Add the URL to the queue

            }
        }
    }
}
