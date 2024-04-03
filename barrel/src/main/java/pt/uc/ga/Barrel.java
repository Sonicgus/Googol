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
        String urls = "Results for page " + page_number + ":\n";

        for (String keyword : keywords) {
            if (words.containsKey(keyword)) {
                for (String url : words.get(keyword)) {
                    urls += url + "\n";
                }
            }
        }

        return urls;
    }

    @Override
    public String linkInfo(String url) throws FileNotFoundException, IOException {
        if (urls.containsKey(url)) {
            SiteInfo site = urls.get(url);
            return "Title: " + site.getTitle() + "\nDescription: " + site.getDescription() + "\nReferenced URLs: "
                    + site.getUrls();
        }
        return null;
    }

    /**
     * @param url
     */
    private void parser(String url) {
        // chave1 | valor1; chave2 | valor2
        // exemplo:
        // type | words_list; item_count | 2; item_0 | banana; item_1 | fixe;
        // type | url_list; item_count | 2; item_0 | www.uc.pt; item_1 | www.google.com;
        // type | link_info; url | www.uc.pt; title | DEI; description | DEI é fixe;
        String[] parts = url.split(";");

        HashMap<String, String> dici = new HashMap<String, String>();

        for (String part : parts) {
            String[] key_value_pair = part.split("\\|");
            dici.put(key_value_pair[0].trim(), key_value_pair[1].trim());
        }

        if (dici.get("type").equals("url")) {
            SiteInfo site = new SiteInfo();

            site.setTitle(dici.get("title"));
            site.setDescription(dici.get("description"));

            String[] urlss = dici.get("referenced_urls").split(" ");
            for (String u : urlss) {
                site.getUrls().add(u);
            }

            String[] wordss = dici.get("words").split(" ");
            for (String word : wordss) {
                System.out.println(word);
                if (words.containsKey(word)) {
                    words.get(word).add(dici.get("url"));
                } else {
                    HashSet<String> urlsaux = new HashSet<String>();
                    urlsaux.add(dici.get("url"));
                    words.put(word, urlsaux);
                }
            }
            urls.put(dici.get("url"), site);
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
