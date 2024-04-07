package pt.uc.ga;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * URL queue.
 */
public class UrlQueue {
    private LinkedList<String> queue;
    private Set<String> visited;

    SendQueueTask queueSend;
    ReceiveQueueTask queueReceive;

    String initialUrl;

    /**
     * Constructor.
     *
     * @param PORT_A
     * @param PORT_B
     */
    public UrlQueue(int PORT_A, int PORT_B) {
        queueSend = new SendQueueTask(PORT_A, this);
        queueReceive = new ReceiveQueueTask(PORT_B, this);
    }

    /**
     * Constructor.
     *
     * @param PORT_A
     * @param PORT_B
     * @param initialUrl
     */
    public UrlQueue(int PORT_A, int PORT_B, String initialUrl) {
        queueSend = new SendQueueTask(PORT_A, this);
        queueReceive = new ReceiveQueueTask(PORT_B, this);

        this.initialUrl = initialUrl;
    }

    /**
     * Start the URL queue.
     */
    public void start() {
        load();
        new Thread(queueSend).start();
        new Thread(queueReceive).start();
    }

    /**
     * Add a URL to the queue
     */
    public synchronized void addUrl(String url, boolean resend) {
        if (resend && !queue.contains(url)) {
            queue.add(url);
            save();
            System.out.println("Resent url: " + url);
            return;
        }
        if (queue.contains(url) || visited.contains(url)) {
            return;
        }

        System.out.println("Added url: " + url);
        queue.add(url);
        visited.add(url);
        save();
    }


    /**
     * Load the URL queue and visited from obj files.
     */
    public void load() {
        try {
            FileInputStream fileIn = new FileInputStream("urlqueue.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            queue = (LinkedList<String>) in.readObject();
            visited = (Set<String>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            queue = new LinkedList<>();
            visited = new HashSet<>();
            if (initialUrl != null)
                addUrl(initialUrl, false);

        }
    }

    /**
     * Save the URL queue and visited to obj files.
     */
    public void save() {
        try {
            FileOutputStream fileOut = new FileOutputStream("urlqueue.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(queue);
            out.writeObject(visited);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Get the next URL from the queue.
     *
     * @return
     */
    public synchronized String getUrl() {
        String url = queue.poll();
        save();
        return url;
    }

}
