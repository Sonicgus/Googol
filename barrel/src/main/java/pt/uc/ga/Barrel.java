package pt.uc.ga;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Barrel implements BarrelInterface {
    private final int id;
    private final HashMap<String, HashSet<String>> words; // word -> urls
    private final HashMap<String, SiteInfo> urls; // url -> link_info

    public Barrel(int id) {
        this.id = id;
        this.words = new HashMap<>();
        this.urls = new HashMap<>();
    }

    /**
     *
     */
    @Override
    public String search(HashSet<String> keywords, int page_number) throws FileNotFoundException, IOException {
        //measure time of search
        long startTime = System.currentTimeMillis();
        LinkedList<SiteInfo> urls = new LinkedList<>();
        for (String keyword : keywords) {
            if (words.containsKey(keyword)) {
                for (String url : words.get(keyword)) {
                    urls.add(this.urls.get(url));
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;


        if (urls.isEmpty()) {
            return "No results found";
        }

        LinkedList<SiteInfo> urlsToSend10 = new LinkedList<>();

        //for cycle that finds the max and adds to the urlsToSend10
        //does 10 times
        //sort by order the one who has more in getNumUrls() is appended to the urlsToSend10

        for (int i = 0; i < 10 && i < urls.size(); i++) {
            SiteInfo max = urls.get(0);
            for (SiteInfo site : urls) {
                if (site.getNumUrls() > max.getNumUrls()) {
                    max = site;
                }
            }
            urlsToSend10.add(max);
            urls.remove(max);
        }
        String response = "Results for page " + page_number + "/" + +urls.size() / 10 + "Number of results: " + urls.size() + "timeElapsed:" + timeElapsed + "\n\n";

        for (SiteInfo site : urlsToSend10) {
            response += "Title: " + site.getTitle() + "\nDescription: " + site.getDescription() + "\nURL: "
                    + site.getUrl() + "\n\n";
        }

        return response;

    }

    @Override
    public String linkInfo(String url) throws FileNotFoundException, IOException {
        if (urls.containsKey(url)) {
            SiteInfo site = urls.get(url);
            return "Number of URLs pointing to this URL:" + site.getUrls().size() + "\nTitle:"
                    + site.getTitle() + "\nDescription:" + site.getDescription() + "\nURLs Pointing to this URL:"
                    + site.getUrls();
        }

        return "URL not found";
    }

    /**
     * @param info
     */
    private void parser(String info) {
        // chave1 | valor1; chave2 | valor2
        // exemplo:
        // type | words_list; item_count | 2; item_0 | banana; item_1 | fixe;
        // type | url_list; item_count | 2; item_0 | www.uc.pt; item_1 | www.google.com;
        // type | link_info; url | www.uc.pt; title | DEI; description | DEI é fixe;
        String[] parts = info.split(";");

        HashMap<String, String> dici = new HashMap<String, String>();

        for (String part : parts) {
            String[] key_value_pair = part.split("\\|");
            dici.put(key_value_pair[0].trim(), key_value_pair[1].trim());
        }

        if (dici.get("type").equals("url") && dici.containsKey("url")) {
            SiteInfo site = new SiteInfo();

            site.setUrl(dici.get("url"));

            if (dici.containsKey("title"))
                site.setTitle(dici.get("title"));
            if (dici.containsKey("description"))
                site.setDescription(dici.get("description"));

            if (dici.containsKey("referenced_urls")) {
                String[] urlss = dici.get("referenced_urls").split(" ");
                for (String url : urlss) {
                    if (urls.containsKey(url)) {
                        urls.get(url).getUrls().add(site.getUrl());
                    } else {
                        SiteInfo siteaux = new SiteInfo();
                        siteaux.setUrl(url);
                        siteaux.getUrls().add(site.getUrl());
                        urls.put(url, siteaux);
                    }
                }
            }

            if (dici.containsKey("words")) {
                String[] wordss = dici.get("words").split(" ");
                for (String word : wordss) {
                    if (words.containsKey(word)) {
                        words.get(word).add(site.getUrl());
                    } else {
                        HashSet<String> urlsaux = new HashSet<String>();
                        urlsaux.add(site.getUrl());
                        words.put(word, urlsaux);
                    }
                }
            }
            if (urls.containsKey(dici.get("url"))) {
                //if already exists update with title, description
                urls.get(dici.get("url")).setTitle(site.getTitle());
                urls.get(dici.get("url")).setDescription(site.getDescription());
            } else {
                urls.put(site.getUrl(), site);
            }

        }
    }

    public void start() {
        try {
            BarrelInterface stub = (BarrelInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry("localhost", Configuration.RMI_GATEWAY_PORT);
            registry.rebind("barrel" + id, stub);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        byte[] buf = new byte[1024];
        try {
            // Criação do socket de multicast
            MulticastSocket socket = new MulticastSocket(Configuration.MULTICAST_PORT);

            // Junta-se ao grupo de multicast
            InetAddress group = InetAddress.getByName(Configuration.MULTICAST_ADDRESS);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
            InetSocketAddress groupAddress = new InetSocketAddress(group, Configuration.MULTICAST_PORT);
            socket.joinGroup(groupAddress, networkInterface);
            System.out.println("Barrel " + id + " started");
            while (true) {
                // Receber a mensagem de multicast
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // Imprimir a mensagem recebida
                String received = new String(packet.getData(), 0, packet.getLength());
                if (received.equals("exit")) {
                    break;
                }

                // System.out.println("Mensagem de multicast recebida: " + received);
                parser(received);
            }
            // Sair do grupo de multicast e fechar o socket
            socket.leaveGroup(groupAddress, networkInterface);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
