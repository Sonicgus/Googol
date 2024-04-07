package pt.uc.ga;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class UrlQueue {
    private final LinkedList<String> queue;
    private final Set<String> visited;

    /**
     * Constructor for the UrlQueue class
     */
    public UrlQueue() {
        queue = new LinkedList<>();
        visited = new HashSet<>();
        String initialUrl = "https://www.uc.pt/";
        addUrl(initialUrl, false);
    }

    /**
     * Start the URL queue
     */
    public void start() {
        SendQueueTask queueSend = new SendQueueTask(Configuration.PORT_A, this);
        ReceiveQueueTask queueReceive = new ReceiveQueueTask(Configuration.PORT_B, this);
        new Thread(queueSend).start();
        new Thread(queueReceive).start();
    }

    /**
     * Add a URL to the queue
     */
    public synchronized void addUrl(String url, boolean resend) {
        if (!resend && visited.contains(url))
            return;

        System.out.println("Added url: " + url);
        queue.add(url);
        visited.add(url);
    }


    public synchronized String getUrl() {
        return queue.poll();
    }

}
