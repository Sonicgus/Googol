package pt.uc.ga;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.HashSet;

public class Gateway implements GatewayInterface {
    private final HashMap<String, Long> searches;
    private long avgtime;
    private long num_searches;

    public Gateway() {
        this.searches = new HashMap<>();
        this.avgtime = 0;
        this.num_searches = 0;
    }


    /**
     * @param url
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    @Override
    public String addLink(String url) throws UnknownHostException, IOException {
        System.out.println("Received URL from client: " + url);

        // Adiciona o URL à fila
        Socket socket = new Socket("localhost", Configuration.PORT_B);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println(url);

        out.close();
        socket.close();

        return "Received URL: " + url;
    }

    /**
     * @param url
     * @return
     * @throws RemoteException
     */
    @Override
    public String linkInfo(String url) throws RemoteException {
        BarrelInterface b;
        try {
            b = getRandomBarrel();

        } catch (Exception e) {
            System.out.println("Exception in linkInfo: " + e);
            e.printStackTrace();
            return "Falha ao comunicar com barrels";
        }

        try {
            return b.linkInfo(url);
        } catch (Exception e) {
            System.out.println("Exception in linkInfo: " + e);
            e.printStackTrace();
            return "Falha ao comunicar com barrels function linkInfo";
        }
    }

    private synchronized void calculateAvg(long time) {
        avgtime = (avgtime * num_searches + time) / (num_searches + 1);
        num_searches++;
        //send avg to multicast
        try {
            // Criação do socket de multicast
            MulticastSocket socket = new MulticastSocket(Configuration.MULTICAST_PORT);

            // Junta-se ao grupo de multicast
            InetAddress group = InetAddress.getByName(Configuration.MULTICAST_ADDRESS);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
            InetSocketAddress groupAddress = new InetSocketAddress(group, Configuration.MULTICAST_PORT);
            socket.joinGroup(groupAddress, networkInterface);
            //send avg and top 10 to multicast
            String message = "type | adminupdated";
            byte[] buf = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Configuration.MULTICAST_PORT);
            socket.send(packet);
            socket.leaveGroup(groupAddress, networkInterface);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param keywords
     * @param page_number
     * @return
     * @throws RemoteException
     */
    @Override
    public String search(HashSet<String> keywords, int page_number) throws RemoteException {

        BarrelInterface b;
        try {
            b = getRandomBarrel();

        } catch (Exception e) {
            System.out.println("Exception in search: " + e);
            e.printStackTrace();
            return "Falha ao comunicar com barrels";
        }

        try {
            String search = "";
            for (String keyword : keywords) {
                search += keyword + " ";
            }
            if (searches.containsKey(search)) {
                searches.put(search, searches.get(search) + 1);
            } else {
                searches.put(search, 1L);
            }

            //start time
            long start = System.currentTimeMillis();
            String res = b.search(keywords, page_number);
            //end time
            long end = System.currentTimeMillis();

            calculateAvg(end - start);
            return res;
        } catch (Exception e) {
            System.out.println("Exception in search: " + e);
            e.printStackTrace();
            return "Falha ao comunicar com barrels function searchBarrel";
        }
    }

    /**
     * @return
     */
    public BarrelInterface getRandomBarrel() {
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(Configuration.RMI_HOST, Configuration.RMI_GATEWAY_PORT);
                //get barrels list. all start with "barrel"
                String[] list = registry.list();
                //only get Strings starting with the word barrel
                HashSet<String> barrelsList = new HashSet<>();
                for (String s : list) {
                    if (s.startsWith("barrel")) {
                        barrelsList.add(s);
                    }
                }
                //get a random barrel
                String randomBarrel = (String) barrelsList.toArray()[(int) (Math.random() * barrelsList.size())];
                return (BarrelInterface) registry.lookup(randomBarrel);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @return
     * @throws RemoteException
     */
    @Override
    public String admin(boolean wait) throws RemoteException {
        if (!wait) {
            return admininfo();
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
            while (true) {
                // Receber a mensagem de multicast
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // Imprimir a mensagem recebida
                String received = new String(packet.getData(), 0, packet.getLength());
                //check if the message is for admin
                HashMap<String, String> dici = new HashMap<>();
                String[] messages = received.split(";");
                for (String message : messages) {
                    String[] parts = message.split("\\|");
                    dici.put(parts[0].trim(), parts[1].trim());
                }
                if (dici.containsKey("type") && dici.get("type").equals("adminupdated")) {
                    socket.leaveGroup(groupAddress, networkInterface);
                    socket.close();
                    return admininfo();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    private String admininfo() {
        HashMap<String, Long> copysearches = new HashMap<>(searches);
        //add all searches to searchss
        //and than add to response the top 10
        String response = "\n\nActive Barrels:\n";
        try {
            Registry registry = LocateRegistry.getRegistry(Configuration.RMI_HOST, Configuration.RMI_GATEWAY_PORT);
            //get barrels list. all start with "barrel"
            String[] list = registry.list();
            //only get Strings starting with the word barrel
            HashSet<String> barrelsList = new HashSet<>();
            for (String s : list) {
                if (s.startsWith("barrel")) {
                    barrelsList.add(s);
                }
            }
            for (String barrel : barrelsList) {
                response += barrel + "\n";
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (searches.isEmpty()) {
            return response + "No searches yet";
        }
        response += "Top 10 Searches: \n";
        for (int i = 0; i < 10 && i < searches.size(); i++) {
            String max = "";
            long maxvalue = 0;
            for (String search : copysearches.keySet()) {
                if (copysearches.get(search) > maxvalue) {
                    max = search;
                    maxvalue = copysearches.get(search);
                }
            }
            response += i + 1 + " - " + max + " Number of searches: " + maxvalue + "\n";
            copysearches.remove(max);
        }

        return response;
    }
}
