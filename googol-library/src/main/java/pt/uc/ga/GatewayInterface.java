package pt.uc.ga;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;

public interface GatewayInterface extends Remote {
    /**
     *
     */
    String addLink(String url) throws RemoteException;

    /**
     *
     */
    String linkInfo(String url) throws RemoteException;

    /**
     *
     */
    String search(HashSet<String> keywords, int page_number) throws RemoteException, MalformedURLException, NotBoundException;

    /**
     *
     */
    String getAdminPage(boolean wait) throws RemoteException;
}
