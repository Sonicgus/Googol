import java.rmi.*;
import java.rmi.server.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.net.UnknownHostException;

public class Gateway extends UnicastRemoteObject implements GatewayInterface {
    private static final long serialVersionUID = 1L;

    public Gateway() throws RemoteException {
        super();
    }

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

    public String linkInfo(String url) throws RemoteException {
        System.out.println("Received URL from client: " + url);

        return "Received URL: " + url;
    }

    public String search(String keyword) throws RemoteException {
        System.out.println("Received keyword from client: " + keyword);

        return "I dont have barrels to search: " + keyword;
    }

    public String top10() throws RemoteException {
        System.out.println("Bruh the client wants to see the top10: ");

        return "I dont have barrels to give you the top10";
    }

    public static void main(String args[]) throws MalformedURLException {

        try {
            for (int i = 0; i < Configuration.NUM_BARRELS; i++) {
                Barrel barrel = new Barrel(i);
                barrel.start();
            }

            for (int i = 0; i < Configuration.NUM_DOWNLOADERS; i++) {
                Downloader downloader = new Downloader(i);
                downloader.start();
            }
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

        try {
            Gateway h = new Gateway();
            Naming.rebind("rmi://localhost:7000/googol", h);
            System.out.println("Hello Server ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }

    }
}