package pt.uc.ga;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static pt.uc.ga.FuncLib.getDici;

public class Barrel implements BarrelInterface {
    private final int id;
    private final HashMap<String, HashSet<String>> words;
    private final HashMap<String, SiteInfo> urls;
    private final SiteInfoComparator comparator;


    public Barrel(int id) {
        this.id = id;
        this.words = new HashMap<>();
        this.urls = new HashMap<>();
        this.comparator = new SiteInfoComparator();
    }

    /**
     * Search pages by keywords
     */
    @Override
    public String search(HashSet<String> keywords, int page_number) {
        HashSet<SiteInfo> urlss = new HashSet<>();
        for (String keyword : keywords) {
            if (words.containsKey(keyword)) {

                for (String url : words.get(keyword)) {
                    if (urls.containsKey(url)) {
                        // if not in urlss the add
                        urlss.add(urls.get(url));

                    }
                }
            }
        }

        int numresults = urlss.size();
        int numtoremove = page_number * 10;

        List<SiteInfo> lista = urlss.stream().sorted(comparator).skip(numtoremove).limit(10).toList();


        if (lista.isEmpty()) {
            return "No results found";
        }


        StringBuilder response = new StringBuilder("Search results:\n");


        for (SiteInfo site : lista) {
            response.append("URL:").append(site.getUrl()).append("\nTitle:").append(site.getTitle()).append("\nDescription:").append(site.getDescription()).append("\nNumber of URLs pointing to this URL:").append(site.getNumUrls()).append("\n\n");
        }

        int var = numresults / 10 + 1;
        if (numresults % 10 == 0) {
            var--;
        }

        response.append("Page ").append(page_number + 1).append("/").append(var).append(" Number of results: ").append(numresults);

        return response.toString();

    }

    /**
     * Get link info
     */
    @Override
    public String linkInfo(String url) {
        if (urls.containsKey(url)) {
            SiteInfo site = urls.get(url);
            return "Number of URLs pointing to this URL:" + site.getUrls().size() + "\nTitle:"
                    + site.getTitle() + "\nDescription:" + site.getDescription() + "\nURLs Pointing to this URL:"
                    + site.getUrls();
        }

        return "URL not found";
    }


    /**
     *
     */
    private void parser(String info) {
        // chave1 | valor1; chave2 | valor2
        // exemplo:
        // type | url; url | www.uc.pt; title | DEI; description | DEI é fixe; referenced_urls | www.google.com www.facebook.com; words | DEI UC;
        HashMap<String, String> dici = getDici(info);

        if (dici.containsKey("type")) {
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
                            HashSet<String> urlsaux = new HashSet<>();
                            urlsaux.add(site.getUrl());
                            words.put(word, urlsaux);
                        }
                    }
                }

                if (urls.containsKey(dici.get("url"))) {
                    //if already exists update with title, description
                    if (dici.containsKey("title"))
                        urls.get(dici.get("url")).setTitle(site.getTitle());
                    if (dici.containsKey("description"))
                        urls.get(dici.get("url")).setDescription(site.getDescription());
                } else {
                    urls.put(site.getUrl(), site);
                }

            }
        }
    }


    public void start() {
        try {
            BarrelInterface stub = (BarrelInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(Configuration.RMI_HOST, Configuration.RMI_GATEWAY_PORT);

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
