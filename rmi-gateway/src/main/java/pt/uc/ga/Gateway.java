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

import static pt.uc.ga.FuncLib.getDici;
import static pt.uc.ga.FuncLib.getKeywordsSet;

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
     *
     */
    @Override
    public String addLink(String url) throws RemoteException {
        System.out.println("Received URL from client: " + url);

        // Adiciona o URL à fila
        Socket socket = null;
        PrintWriter out = null;
        try {
            socket = new Socket("localhost", Configuration.PORT_B);
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(url);
        } catch (IOException e) {
            System.out.println("Erro ao enviar URL: " + e.getMessage());
            return "Erro ao enviar URL: " + e.getMessage();
        } finally {
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar o socket: " + e.getMessage());
                }
            }
        }

        return "Received URL: " + url;
    }

    /**
     *
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
     *
     */
    @Override
    public String search(String keywords, int page_number) throws RemoteException {
        long start = System.currentTimeMillis();

        HashSet<String> keywords_set = getKeywordsSet(keywords);

        BarrelInterface b;
        while (true) {
            try {
                b = getRandomBarrel();

                String res = b.search(keywords_set, page_number);
                StringBuilder search = new StringBuilder();


                for (String keyword : keywords_set) {
                    search.append(keyword).append(" ");
                }
                if (searches.containsKey(search.toString())) {
                    searches.put(search.toString(), searches.get(search.toString()) + 1);
                } else {
                    searches.put(search.toString(), 1L);
                }

                long end = System.currentTimeMillis();

                calculateAvg(end - start);
                return res;
            } catch (Exception e) {
                System.out.println("Exception in search: " + e);
            }
        }
    }


    /**
     *
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
     *
     */
    @Override
    public String getAdminPage(boolean wait) throws RemoteException {
        if (!wait) {
            return getAdminInfo();
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
                HashMap<String, String> dici = getDici(received);

                if (dici.containsKey("type") && dici.get("type").equals("adminupdated")) {
                    socket.leaveGroup(groupAddress, networkInterface);
                    socket.close();
                    return getAdminInfo();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    private String getAdminInfo() {
        HashMap<String, Long> copysearches = new HashMap<>(searches);
        //add all searches to searchss
        //and then add to response the top 10
        StringBuilder response = new StringBuilder("\n\nActive Barrels:\n");
        try {
            Registry registry = LocateRegistry.getRegistry(Configuration.RMI_HOST, Configuration.RMI_GATEWAY_PORT);
            //get barrels list. all start with "barrel"
            String[] list = registry.list();
            //only get Strings starting with the word barrel
            for (String s : list) {
                if (s.startsWith("barrel"))
                    response.append(s).append("\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (searches.isEmpty()) {
            return response + "No searches yet";
        }
        response.append("Top 10 Searches: \n");
        for (int i = 0; i < 10 && i < searches.size(); i++) {
            String max = "";
            long maxvalue = 0;
            for (String search : copysearches.keySet()) {
                if (copysearches.get(search) > maxvalue) {
                    max = search;
                    maxvalue = copysearches.get(search);
                }
            }
            response.append(i + 1).append(" - ").append(max).append(" Number of searches: ").append(maxvalue).append("\n");
            copysearches.remove(max);
        }

        return response + "Average search time: " + avgtime / 100 + " décimas de segundo";
    }
}
