package pt.uc.ga;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashSet;

import static pt.uc.ga.FuncLib.getKeywordsSet;

/**
 * Class that downloads a webpage and sends the information to barrels
 */
public class Downloader {
    private String url;
    private Document doc;
    private final HashSet<String> links;
    private HashSet<String> wordsmap;
    private String title;
    private String description;

    private final int PORT_A;
    private final int PORT_B;
    private final String MULTICAST_ADDRESS;
    private final int MULTICAST_PORT;
    private final int MAXIMUM_REFERENCE_LINKS;

    private final String HOST_ADDRESS;

    /**
     * Constructor
     *
     * @param PORT_A
     * @param PORT_B
     * @param MULTICAST_ADDRESS
     * @param MULTICAST_PORT
     * @param MAXIMUM_REFERENCE_LINKS
     * @param HOST_ADDRESS
     */
    public Downloader(int PORT_A, int PORT_B, String MULTICAST_ADDRESS, int MULTICAST_PORT, int MAXIMUM_REFERENCE_LINKS, String HOST_ADDRESS) {

        this.links = new HashSet<>();
        this.wordsmap = new HashSet<>();
        this.PORT_A = PORT_A;
        this.PORT_B = PORT_B;
        this.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
        this.MULTICAST_PORT = MULTICAST_PORT;
        this.MAXIMUM_REFERENCE_LINKS = MAXIMUM_REFERENCE_LINKS;
        this.HOST_ADDRESS = HOST_ADDRESS;
    }

    /**
     * Method that starts the downloader
     */
    public void start() {
        System.out.println("Downloader started");

        while (true) {
            try {
                this.url = getUrl();
            } catch (InterruptedException e) {
                System.err.println("Failed to get URL from queue");
                continue;
            }

            if (this.url == null) {
                System.out.println("No more URLs to download");
                continue;
            }

            try {
                this.doc = Jsoup.connect(this.url).get();
            } catch (ConnectException e) {
                System.out.println("Connection failed to URL: " + this.url);
                try {
                    this.links.clear();
                    this.links.add(this.url);
                    sendLinkToQueue(true);
                } catch (Exception e1) {
                    System.err.println("Failed to send URL to queue");
                }
                continue;
            } catch (Exception e) {
                System.err.println("Failed to download URL: " + this.url);
                continue;
            }

            clear();

            try {
                filter();

                sendUrlInfo();
                sendLinkToQueue(false);

            } catch (Exception e) {
                try {
                    this.links.clear();
                    this.links.add(this.url);
                    sendLinkToQueue(true);
                } catch (Exception e1) {
                    System.err.println("Failed to send URL to queue");
                }
            }
        }
    }


    /**
     * Method that sends the words to the barrels
     *
     * @throws IOException
     */
    private void sendUrlInfo() throws IOException {
        String info = getUrlInfo();


        InetAddress group = InetAddress.getByName(this.MULTICAST_ADDRESS);

        MulticastSocket socket = new MulticastSocket(this.MULTICAST_PORT);

        byte[] buffer = info.getBytes();

        if (buffer.length > 65534) {
            System.err.println("Downloader [Page too long] " + "failed to send url to queue");
            socket.close();
            return;
        }

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, this.MULTICAST_PORT);
        socket.send(packet);
        socket.close();
    }

    /**
     * Method that gets the information of the page
     *
     * @return
     */
    private String getUrlInfo() {
        StringBuilder info = new StringBuilder("type | url; url | " + this.url);

        if (this.description != null)
            info.append("; description | ").append(this.description);

        if (this.title != null)
            info.append("; title | ").append(this.title);

        int linkCount = 0;
        if (!this.links.isEmpty())
            info.append("; referenced_urls |");

        for (String link : this.links) {
            if (linkCount++ == this.MAXIMUM_REFERENCE_LINKS) {
                break;
            }

            if (link != this.links.toArray()[this.links.size() - 1])
                info.append(" ").append(link);
        }

        if (!this.wordsmap.isEmpty())
            info.append("; words |");

        for (String word : wordsmap) {
            info.append(" ").append(word);
        }
        return info.toString();
    }

    /**
     * Method that clears the variables
     */
    private void clear() {
        this.links.clear();
        this.wordsmap.clear();
        this.title = "";
        this.description = "";
    }

    /**
     * Method that sends the link to the queue
     *
     * @param resend
     * @throws InterruptedException
     */
    private void sendLinkToQueue(boolean resend) throws InterruptedException {
        while (true) {
            try {
                Socket socket = new Socket(HOST_ADDRESS, this.PORT_B);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                for (String link : links) {
                    if (resend)
                        out.println("resend" + link);
                    else
                        out.println(link);
                }

                socket.close();
                break;
            } catch (IOException e) {
                System.err.println("Failed to send URL to queue, trying again in 3 seconds");
                Thread.sleep(3000);
            }
        }
    }

    /**
     * Method that filters the page
     */
    private void filter() {

        String title;
        try {
            title = doc.title();
        } catch (NullPointerException e) {
            return;
        }
        this.title = removeIlegalCharacters(title);
        this.description = doc.select("meta[name=description]").attr("content");


        String text;
        try {
            text = doc.text();
        } catch (NullPointerException e) {
            return;
        }

        if (this.description == null || this.description.isEmpty()) {
            this.description = text.substring(0, Math.min(0, 100));
        }

        this.wordsmap = getKeywordsSet(text);

        Elements links;
        try {
            links = doc.select("a[href]");
        } catch (NullPointerException e) {
            return;
        }

        for (Element link : links) {
            String url = link.attr("abs:href");
            url = removeIlegalCharacters(url);
            this.links.add(url);
        }
    }

    /**
     * Method that removes illegal characters
     *
     * @param str
     * @return
     */
    private String removeIlegalCharacters(String str) {
        return str.replace("|", "").replace(";", "").replace("\n", "");
    }

    /**
     * Method that gets the URL
     *
     * @return
     * @throws InterruptedException
     */
    private String getUrl() throws InterruptedException {
        while (true) {
            try {
                Socket socket = new Socket(HOST_ADDRESS, this.PORT_A);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String url = in.readLine();
                socket.close();
                return url;
            } catch (IOException e) {
                System.err.println("Failed to get URL from queue, trying again in 3 seconds");
                Thread.sleep(3000);
            }
        }
    }
}
