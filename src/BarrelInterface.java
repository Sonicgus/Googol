import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BarrelInterface extends Remote {
    String search(String keywords) throws RemoteException;
}