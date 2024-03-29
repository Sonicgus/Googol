
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.net.*;
import java.io.*;

public class Downloader extends Thread {
    private String url;
    private Document doc;
    private HashSet<String> links;
    private String words;
    private String title;
    private String data;

    private int ID;

    public Downloader(int ID) {
        this.ID = ID;
        this.links = new HashSet<String>();
        this.words = "";
    }

    public void run() {
        try {
            sendStatus("Waiting");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            clear();

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

            sendStatus("Active");

            try {
                this.doc = Jsoup.connect(this.url).get();
            } catch (ConnectException e) {
                handleConnectionFailure();
                continue;
            } catch (Exception e) {
                handleDownloadFailure();
                continue;
            }

            try {
                // Configuration.AUTOFAIL_DOWNLOADERS = true; - caso queira testar a robustez do
                // código;
                simulateCrashIfNeeded();

                download();

                if (this.title == null || this.title.equals("")) {
                    System.err.println("Failed to download URL: " + this.url);
                    continue;
                }

                sendWords();
                sendLinkToQueue(false);

            } catch (Exception e) {
                handleDownloaderFailure();
                continue;
            }
        }
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

    private void handleDownloadFailure() {
        System.err.println("Failed to download URL: " + this.url);
    }

    private void simulateCrashIfNeeded() throws Exception {
        if (Configuration.AUTOFAIL_DOWNLOADERS && this.ID == getRandomNumber()) {
            System.out.println("Simulated a crash for Downloader[" + this.ID + "]");
            throw new Exception();
        }
    }

    private int getRandomNumber() {
        return (int) (Math.random() * Configuration.NUM_DOWNLOADERS) + 1;
    }

    private void handleDownloaderFailure() {
        System.err.println("Downloader[" + this.ID + "] stopped working!");

        try {
            this.links.clear();
            this.links.add(this.url);
            sendLinkToQueue(true);
        } catch (Exception e1) {
            System.err.println("Failed to send URL to queue");
        }

        try {
            sendStatus("Offline");
        } catch (Exception e1) {
            System.err.println("Failed to send Downloader[" + this.ID + "] status");
        }
    }

    private void download() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get(); // contem todo o conteúdo da página
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String title = doc.title(); // título da página é extraído usando o método title() do objeto doc.
        try {
            title = doc.title();
        } catch (NullPointerException e) {
            return;
        }
        this.title = title;
        this.title = this.title.replace("|", "");
        this.title = this.title.replace(";", "");
        this.title = this.title.replace("\n", "");

        String[] words = doc.text().split(" ");
        // A função text() retorna todo o texto contido na página HTML,excluindo as tags
        // HTML.
        // Este texto é então dividido em palavras usando o método split(" ").

        for (String word : words) {
            if (word.contains("|") || word.contains(";") || word.contains("\n"))
                continue;

            this.words += word + ";";
        }

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            url = url.replace("|", "");
            url = url.replace(";", "");
            url = url.replace("\n", "");
            this.links.add(url);
        }
    }

    private String getUrl() throws InterruptedException {
        while (true) {
            try {
                Socket socket = new Socket("localhost", Configuration.PORT_A);
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

    private void sendLinkToQueue(boolean resend) throws InterruptedException {
        int numberTries = 0;
        while (true) {
            try {
                Socket socket = new Socket("localhost", Configuration.PORT_B);
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

    private void sendStatus(String status) {
        try {
            InetAddress group = InetAddress.getByName(Configuration.MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket(Configuration.MULTICAST_PORT);

            String statusString = buildStatusString(status);

            byte[] buffer = statusString.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Configuration.MULTICAST_PORT);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            handleSendStatusError();
        }
    }

    private String buildStatusString(String status) {
        return "type | Downloader; index | " + this.ID + "; status | " + status + "; url | " + this.url;
    }

    private void handleSendStatusError() {
        System.err.println("Failed to send status to admin for Downloader[" + this.ID + "]");
    }

    private void sendWords() throws IOException {
        // Constroi a string de dados no formato do protocolo
        String referencedUrls = "type | url; item_count | " + this.links.size() + "; url | " + this.url
                + "; referenced_urls | ";

        // Adiciona os links de referencia á string de dados
        int linkCount = 0;
        for (String link : this.links) {
            if (linkCount++ == Configuration.MAXIMUM_REFERENCE_LINKS) {
                referencedUrls += "; "; // Adiciona ponto e vírgula para separar links quando chega ao maximo de links
                break;
            }

            if (link == this.links.toArray()[this.links.size() - 1])
                referencedUrls += link + "; ";
            // se link é igual ao último elemento da lista this.links
            else
                referencedUrls += link + " "; // Adiciona espaço para separar os links
        }

        // Tratar o caso em que não há links referenciados
        if (this.links.isEmpty())
            referencedUrls += "None; ";

        // Substituir os pontos e vírgulas na string de palavras
        if (this.words == null)
            this.words = "None";
        this.words = this.words.replace(";", " ");

        // repara-se a string de dados completa, incluindo título e palavras
        referencedUrls += "title | " + this.title + "; " + "words | " + this.words;
        this.data = referencedUrls;

        // Obtém o endereço IP do grupo multicast ao qual os dados serão enviados.
        InetAddress group = InetAddress.getByName(Configuration.MULTICAST_ADDRESS);

        // Cria um socket de multicast que será usado para enviar os dados para o grupo
        MulticastSocket socket = new MulticastSocket(Configuration.MULTICAST_PORT);

        // Converte a string this.data em um array de bytes.
        byte[] buffer = this.data.getBytes();

        // Verifica o tamanho dos dados antes de enviar
        if (buffer.length > 65534) {
            System.err.println("Downloader[" + this.ID + "] [Page too long] " + "failed to send url to queue");
            socket.close();
            return; // Retorna se a página for muito longa
        }

        // Envia os dados para o grupo multicast
        // DatagramPacket encapsula os dados a serem enviados, o endereço do grupo
        // multicast e a porta do multicast.
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Configuration.MULTICAST_PORT);
        socket.send(packet); // Envie o DatagramPacket através do socket multicast.
        socket.close();// fecha o socket
    }

    private void clear() {
        this.links.clear();
        this.words = "";
        this.data = "";
    }
}