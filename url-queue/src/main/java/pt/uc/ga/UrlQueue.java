package pt.uc.ga;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class UrlQueue {
    private Queue<String> queue; // Queue to store URLs to be processed
    private Set<String> visited; // Set to keep track of visited URLs

    public UrlQueue() {
        queue = new LinkedList<>(); // Using LinkedList for the queue implementation
        visited = new HashSet<>(); // Using HashSet for faster membership checking
        String initialUrl = "https://www.uc.pt/";
        queue.add(initialUrl); // Add the initial URL to the queue
        visited.add(initialUrl); // Mark the initial URL as visited
    }

    public void start() {
        // Create and start two QueueThread instances, one for sending and one for
        // receiving
        SendQueueTask queueSend = new SendQueueTask(Configuration.PORT_A, this);
        ReceiveQueueTask queueReceive = new ReceiveQueueTask(Configuration.PORT_B, this);
        new Thread(queueSend).start(); // Start the sending thread
        new Thread(queueReceive).start(); // Start the receiving thread
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
}
