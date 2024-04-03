package pt.uc.ga;


import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public static void main(String args[]) throws IOException {

        try {
            Gateway g = new Gateway();
            System.setProperty("java.rmi.server.hostname", "localhost");
            GatewayInterface stub = (GatewayInterface) UnicastRemoteObject.exportObject(g, 0);
            Registry registry = LocateRegistry.createRegistry(Configuration.RMI_GATEWAY_PORT);
            registry.rebind("googol", stub);
            System.out.println("Gateway Server ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in GatewayImpl.main: " + re);
        }
        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                System.exit(0);
            }
        }
    }
}
