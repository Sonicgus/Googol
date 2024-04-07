package pt.uc.ga;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class UrlQueue {
    private LinkedList<String> queue;
    private Set<String> visited;

    /**
     * Constructor for the UrlQueue class
     */
    public UrlQueue() {

        //read objet file with visited urls and urls queue
        File file = new File("queue.ser");
        if (!file.exists()) {
            queue = new LinkedList<>();
            visited = new HashSet<>();
            String initialUrl = "https://www.uc.pt/";
            addUrl(initialUrl, false);

        } else {
            FileInputStream fileIn = null;
            try {
                fileIn = new FileInputStream("queue.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                queue = (LinkedList<String>) in.readObject();
                visited = (Set<String>) in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException | ClassNotFoundException i) {
                i.printStackTrace();
            }

        }
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
        //save the queue and visited urls to a object file
        try {
            FileOutputStream fileOut = new FileOutputStream("queue.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(queue);
            out.writeObject(visited);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }


    public synchronized String getUrl() {
        return queue.poll();
    }

}
