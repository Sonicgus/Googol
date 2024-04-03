package pt.uc.ga;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.HashSet;

public class Gateway implements GatewayInterface {
    private final HashMap<String, Long> searches;

    public Gateway() {
        this.searches = new HashMap<>();
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
            Registry registry = LocateRegistry.getRegistry("localhost", Configuration.RMI_GATEWAY_PORT);
            b = (BarrelInterface) registry.lookup("barrel" + 0);

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

    /**
     * @param keywords
     * @return
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public String search(HashSet<String> keywords, int page_number) throws RemoteException {
        BarrelInterface b;
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Configuration.RMI_GATEWAY_PORT);
            b = (BarrelInterface) registry.lookup("barrel" + 0);

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

            return b.search(keywords, page_number);
        } catch (Exception e) {
            System.out.println("Exception in search: " + e);
            e.printStackTrace();
            return "Falha ao comunicar com barrels function searchBarrel";
        }

    }

    /**
     * @return
     * @throws RemoteException
     */
    @Override
    public String getTop10() throws RemoteException {
        HashMap<String, Long> copysearches = new HashMap<>(searches);
        String response = "Top 10 searches\n";
        //add all searches to searchss
        //and than add to response the top 10
        for (int i = 0; i < 10 && i < searches.size(); i++) {
            String max = "";
            long maxvalue = 0;
            for (String search : copysearches.keySet()) {
                if (copysearches.get(search) > maxvalue) {
                    max = search;
                    maxvalue = copysearches.get(search);
                }
            }
            response += "Search: " + max + " Number of searches: " + maxvalue + "\n";
            copysearches.remove(max);
        }
        if (response.equals("Top 10 searches\n")) {
            return "No searches made";
        }
        return response;
    }
}
