import java.io.IOException;
import java.rmi.*;

public interface GatewayInterface extends Remote {
    String addLink(String link) throws UnknownHostException, IOException;

    String linkInfo(String link) throws RemoteException;

    String search(String keyword) throws RemoteException;

    String top10() throws RemoteException;
}