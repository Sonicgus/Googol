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
                sendUrl();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUrl() throws IOException {
        String url;
        url = urlQueue.getUrl(); // Get a URL from the queue

        if (url != null) { // If a URL is obtained
            try (Socket socket = serverSocket.accept(); // Accept a connection from a client
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { // Create a PrintWriter for
                // writing to the client
                out.println(url); // Send the URL to the client
                System.out.println("Sent url: " + url); // Print the sent URL
            }
        }
    }
}
