import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Queue;

// Class representing a URL queue
public class UrlQueue {
    private Queue<String> queue; // Queue to store URLs to be processed
    private Set<String> visited; // Set to keep track of visited URLs

    // Constructor to initialize the queue and visited set
    public UrlQueue() {
        queue = new LinkedList<>(); // Using LinkedList for the queue implementation
        visited = new HashSet<>(); // Using HashSet for faster membership checking
        String initialUrl = "http://127.0.0.1:5500/Tests/Test_Site1.html";
        queue.add(initialUrl); // Add the initial URL to the queue
        visited.add(initialUrl); // Mark the initial URL as visited
    }

    // Method to add a URL to the queue
    public synchronized void addUrl(String url, boolean resend) {
        // If not a resend and the URL is already visited, return without adding
        if (!resend && visited.contains(url))
            return;

        System.out.println("Added url: " + url); // Print the added URL
        queue.add(url); // Add the URL to the queue
        visited.add(url); // Mark the URL as visited
    }

    // Method to get a URL from the queue
    public synchronized String getUrl() {
        return queue.poll(); // Remove and return the first URL in the queue
    }

    // Method to print the contents of the queue (for debugging)
    public void printQueue() {
        for (String url : queue) {
            System.out.println(url);
        }
    }

    // Main method to demonstrate the usage of UrlQueue
    public static void main(String[] args) {
        UrlQueue urlQueue = new UrlQueue(); // Create a new instance of UrlQueue
        try {
            // Create and start two QueueThread instances, one for sending and one for
            // receiving
            QueueThread queueSend = new QueueThread(urlQueue, Configuration.PORT_A);
            QueueThread queueReceive = new QueueThread(urlQueue, Configuration.PORT_B);
            queueSend.start(); // Start the sending thread
            queueReceive.start(); // Start the receiving thread
        } catch (IOException e) { // Catch any IO exceptions that may occur
            e.printStackTrace(); // Print the stack trace if an exception occurs
        }
    }
}

// Thread class for sending and receiving URLs
class QueueThread extends Thread {
    private ServerSocket serverSocket; // Server socket for communication
    private int port; // Port number for the server socket
    private UrlQueue urlQueue; // Reference to the shared UrlQueue instance

    // Constructor to initialize the thread with the UrlQueue and port
    public QueueThread(UrlQueue urlQueue, int port) throws IOException {
        this.port = port; // Set the port number
        this.serverSocket = new ServerSocket(port); // Create a new server socket
        this.urlQueue = urlQueue; // Set the reference to the UrlQueue
    }

    // Method to send a URL
    private void sendUrl() throws IOException {
        String url;
        synchronized (urlQueue) { // Synchronize on the shared UrlQueue instance
            url = urlQueue.getUrl(); // Get a URL from the queue
        }
        if (url != null) { // If a URL is obtained
            try (Socket socket = serverSocket.accept(); // Accept a connection from a client
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { // Create a PrintWriter for
                                                                                         // writing to the client
                out.println(url); // Send the URL to the client
                System.out.println("Sent url: " + url); // Print the sent URL
            }
        }
    }

    // Method to receive a URL
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
                synchronized (urlQueue) { // Synchronize on the shared UrlQueue instance
                    urlQueue.addUrl(url, resend); // Add the URL to the queue
                }
            }
        }
    }

    // Run method of the thread
    public void run() {
        while (true) { // Infinite loop to keep the thread running
            try {
                if (port == Configuration.PORT_A) { // If the thread is for sending URLs
                    sendUrl(); // Call the method to send a URL
                } else if (port == Configuration.PORT_B) { // If the thread is for receiving URLs
                    receiveUrl(); // Call the method to receive a URL
                }
            } catch (IOException e) { // Catch any IO exceptions that may occur
                e.printStackTrace(); // Print the stack trace if an exception occurs
            }
        }
    }
}

