import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.Queue;
import java.util.LinkedList;

public class Gateway extends UnicastRemoteObject implements GatewayInterface {
    private static final long serialVersionUID = 1L;

    // Cria uma fila para armazenar URLs
    private Queue<String> urlQueue = new LinkedList<>();

    public Gateway() throws RemoteException {
        super();
    }

    public String addLink(String url) throws RemoteException {
        System.out.println("Received URL from client: " + url);

        // Adiciona o URL à fila
        urlQueue.add(url);

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

    public static void main(String args[]) {

        try {
            Gateway h = new Gateway();
            Naming.rebind("rmi://localhost:7000/googol", h);
            System.out.println("Hello Server ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException in HelloImpl.main: " + e);
        }

    }

}