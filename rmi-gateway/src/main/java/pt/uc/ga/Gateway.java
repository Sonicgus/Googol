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
import java.util.HashSet;

public class Gateway implements GatewayInterface {

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
            System.out.println("Exception in search: " + e);
            e.printStackTrace();
            return "Falha ao comunicar com barrels";
        }

        try {
            return b.linkInfo(url);
        } catch (Exception e) {
            System.out.println("Exception in search: " + e);
            e.printStackTrace();
            return "Falha ao comunicar com barrels function searchBarrel";
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
    public String search(HashSet<String> keywords) throws RemoteException {
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
            HashSet<String> urls = b.search(keywords);
            String result = "Search results: ";
            for (String url : urls) {
                result += url + " ";
            }
            return result;
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
    public String top10() throws RemoteException {
        System.out.println("Bruh the client wants to see the top10: ");

        return "I dont have barrels to give you the top10";
    }
}
