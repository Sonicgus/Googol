package pt.uc.ga;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;

public interface IBarrel extends Remote {

    String search(HashSet<String> keywords, int page_number) throws RemoteException;

    String linkInfo(String url) throws RemoteException;

}
