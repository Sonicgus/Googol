import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GatewayInterface extends Remote {
    String addLink(String link) throws RemoteException;

    String linkInfo(String link) throws RemoteException;

    String search(String keyword) throws RemoteException;

    String top10() throws RemoteException;
}