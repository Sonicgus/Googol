package pt.uc.ga;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the Gateway class.
 */
public interface IGateway extends Remote {
    /**
     * Add a link to the index.
     *
     * @param url
     * @return
     * @throws RemoteException
     */
    String addLink(String url) throws RemoteException;

    /**
     * Get the information of a link.
     *
     * @param url
     * @return
     * @throws RemoteException
     */
    String linkInfo(String url) throws RemoteException;

    /**
     * Search for a keyword.
     *
     * @param keywords
     * @param page_number
     * @return
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    String search(String keywords, int page_number) throws RemoteException, MalformedURLException, NotBoundException;

    /**
     * Get the admin page.
     *
     * @param wait
     * @return
     * @throws RemoteException
     */
    String getAdminPage(boolean wait) throws RemoteException;
}
