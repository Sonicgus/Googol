package pt.uc.ga;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;

/**
 * Interface for the Barrel class.
 */
public interface IBarrel extends Remote {

    /**
     * Search for a set of keywords.
     *
     * @param keywords
     * @param page_number
     * @return
     * @throws RemoteException
     */
    String search(HashSet<String> keywords, int page_number) throws RemoteException;

    /**
     * Get the link information.
     *
     * @param url
     * @return
     * @throws RemoteException
     */
    String linkInfo(String url) throws RemoteException;

    /**
     * Ping the server.
     *
     * @throws RemoteException
     */
    void ping() throws RemoteException;
}
