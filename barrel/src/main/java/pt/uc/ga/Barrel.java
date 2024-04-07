package pt.uc.ga;

import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static pt.uc.ga.FuncLib.getDici;

public class Barrel implements IBarrel {
    private final int id;
    private HashMap<String, HashSet<String>> words;
    private HashMap<String, SiteInfo> urls;
    private final SiteInfoComparator comparator;

    public static String MULTICAST_ADDRESS;
    public static int MULTICAST_PORT;
    public static String RMI_HOST;
    public static int RMI_GATEWAY_PORT;


    public Barrel(int id, String MULTICAST_ADDRESS, int MULTICAST_PORT, String RMI_HOST, int RMI_GATEWAY_PORT) {
        this.id = id;
        Barrel.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
        Barrel.MULTICAST_PORT = MULTICAST_PORT;
        Barrel.RMI_HOST = RMI_HOST;
        Barrel.RMI_GATEWAY_PORT = RMI_GATEWAY_PORT;
        this.comparator = new SiteInfoComparator();

        load();
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

    @Override
    public void ping() {
    }


    /**
     *
     */
    private void parser(String info) {
        // chave1 | valor1; chave2 | valor2
        // exemplo:
        // type | url; url | www.uc.pt; title | DEI; description | DEI é fixe; referenced_urls | www.google.com www.facebook.com; words | DEI UC;
        HashMap<String, String> dici = getDici(info);

        if (dici.containsKey("type") && dici.get("type").equals("url") && dici.containsKey("url")) {
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
            //the following code saves the urlsmap and wordsmap to a object file
            save();
        }
    }

    //function to save the urlsmap and wordsmap to a object file
    public void save() {
        //if the file does not exist, create a new urlsmap and wordsmap
        try {
            FileOutputStream fileOut = new FileOutputStream("barrel" + id + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(urls);
            out.writeObject(words);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    //function to load the urlsmap and wordsmap from a object file
    public void load() {

        //if the file does not exist, create a new urlsmap and wordsmap
        try {
            FileInputStream fileIn = new FileInputStream("barrel" + id + ".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            urls = (HashMap<String, SiteInfo>) in.readObject();
            words = (HashMap<String, HashSet<String>>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            urls = new HashMap<>();
            words = new HashMap<>();
        }
    }


    public void start() {
        try {
            IBarrel stub = (IBarrel) UnicastRemoteObject.exportObject(this, 0);
            Registry registry;
            while (true) {
                try {
                    registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
                    registry.list();
                    break;
                } catch (RemoteException e) {
                    System.out.println("Waiting for RMI Gateway to start, retrying in 5 seconds...");
                    Thread.sleep(5000);
                }
            }
            registry.rebind("barrel" + id, stub);
        } catch (RemoteException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        byte[] buf = new byte[1024];
        try {
            // Criação do socket de multicast
            MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);

            // Junta-se ao grupo de multicast
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
            InetSocketAddress groupAddress = new InetSocketAddress(group, MULTICAST_PORT);
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
