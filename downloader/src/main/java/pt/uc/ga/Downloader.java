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

    public Downloader(int PORT_A, int PORT_B, String MULTICAST_ADDRESS, int MULTICAST_PORT, int MAXIMUM_REFERENCE_LINKS) {

        this.links = new HashSet<>();
        this.wordsmap = new HashSet<>();
        this.PORT_A = PORT_A;
        this.PORT_B = PORT_B;
        this.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
        this.MULTICAST_PORT = MULTICAST_PORT;
        this.MAXIMUM_REFERENCE_LINKS = MAXIMUM_REFERENCE_LINKS;
    }

    /**
     *
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
                handleConnectionFailure();
                continue;
            } catch (Exception e) {
                handleDownloadFailure();
                continue;
            }

            clear();

            try {
                filter();

                sendWords();
                sendUrl();
                sendLinkToQueue(false);

            } catch (Exception e) {
                handleDownloaderFailure();
            }
        }
    }

    private void sendUrl() {
        try {
            InetAddress group = InetAddress.getByName(this.MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket(this.MULTICAST_PORT);

            String urlString = "type | url; item_count | 1; url | " + this.url + "; title | " + this.title
                    + "; description | " + this.description;

            byte[] buffer = urlString.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, this.MULTICAST_PORT);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            handleSendUrlError();
        }
    }

    private void handleSendUrlError() {
        System.err.println("Failed to send URL to admin for Downloader");
    }

    private void handleDownloadFailure() {
        System.err.println("Failed to download URL: " + this.url);
    }

    private void handleDownloaderFailure() {
        try {
            this.links.clear();
            this.links.add(this.url);
            sendLinkToQueue(true);
        } catch (Exception e1) {
            System.err.println("Failed to send URL to queue");
        }

    }


    private void sendWords() throws IOException {
        // Constroi a string de dados no formato do protocolo
        String info = getInfo();


        // Obtém o endereço IP do grupo multicast ao qual os dados serão enviados.
        InetAddress group = InetAddress.getByName(this.MULTICAST_ADDRESS);

        // Cria um socket de multicast que será usado para enviar os dados para o grupo
        MulticastSocket socket = new MulticastSocket(this.MULTICAST_PORT);

        // Converte a string this.data em um array de bytes.
        byte[] buffer = info.getBytes();

        // Verifica o tamanho dos dados antes de enviar
        if (buffer.length > 65534) {
            System.err.println("Downloader [Page too long] " + "failed to send url to queue");
            socket.close();
            return; // Retorna se a página for muito longa
        }

        // Envia os dados para o grupo multicast
        // DatagramPacket encapsula os dados a serem enviados, o endereço do grupo
        // multicast e a porta do multicast.
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, this.MULTICAST_PORT);
        socket.send(packet); // Envie o DatagramPacket através do socket multicast.
        socket.close();// fecha o socket
    }

    private String getInfo() {
        StringBuilder info = new StringBuilder("type | url; url | " + this.url);

        if (this.title != null)
            info.append("; title | ").append(this.title);

        // Adiciona os links de referencia á string de dados
        int linkCount = 0;
        if (!this.links.isEmpty())
            info.append("; referenced_urls |");

        for (String link : this.links) {
            if (linkCount++ == this.MAXIMUM_REFERENCE_LINKS) {
                break;
            }

            if (link != this.links.toArray()[this.links.size() - 1])
                info.append(" ").append(link); // Adiciona espaço para separar os links
        }

        if (!this.wordsmap.isEmpty())
            info.append("; words |");

        for (String word : wordsmap) {
            info.append(" ").append(word);
        }
        return info.toString();
    }

    private void clear() {
        this.links.clear();
        this.wordsmap.clear();
        this.title = "";
        this.description = "";
    }

    private void sendLinkToQueue(boolean resend) throws InterruptedException {
        int numberTries = 0;
        while (true) {
            try {
                Socket socket = new Socket("localhost", this.PORT_B);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                for (String link : links) {
                    if (resend) {
                        link = "[RESEND]" + link;
                    }
                    out.println(link);
                }

                socket.close();
                break; // Break the loop if sending succeeds
            } catch (IOException e) {
                numberTries++;
                System.err.println(
                        "Attempts: " + numberTries + " - Failed to send URL to queue, trying again in 3 seconds");
                Thread.sleep(3000);
                // Se esta exceção for lançada, isso indica que houve um problema de comunicação
                // com o servidor de fila de URLs.
                // E o método tentará enviar os URLs novamente após uma espera de 3 segundos.
            }
        }
    }


    private void filter() {

        String title; // título da página é extraído usando o método title() do objeto doc.
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

    private String removeIlegalCharacters(String str) {
        return str.replace("|", "").replace(";", "").replace("\n", "");
    }


    private void handleConnectionFailure() {
        System.out.println("Connection failed to URL: " + this.url);
        try {
            this.links.clear();
            this.links.add(this.url);
            sendLinkToQueue(true);
        } catch (Exception e1) {
            System.err.println("Failed to send URL to queue");
        }
    }


    private String getUrl() throws InterruptedException {
        while (true) {
            try {
                Socket socket = new Socket("localhost", this.PORT_A);
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
