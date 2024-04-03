package pt.uc.ga;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;

public interface GatewayInterface extends Remote {
    /**
     * @param url
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    String addLink(String url) throws UnknownHostException, IOException;

    /**
     * @param url
     * @return
     * @throws RemoteException
     */
    String linkInfo(String url) throws RemoteException;

    /**
     * @param keywords
     * @return
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    String search(HashSet<String> keywords) throws RemoteException, MalformedURLException, NotBoundException;

    /**
     * @return
     * @throws RemoteException
     */
    String getTop10() throws RemoteException;
}
