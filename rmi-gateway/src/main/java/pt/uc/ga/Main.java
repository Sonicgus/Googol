package pt.uc.ga;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try {
            Gateway g = new Gateway();
            System.setProperty("java.rmi.server.hostname", "localhost");
            IGateway stub = (IGateway) UnicastRemoteObject.exportObject(g, 0);
            Registry registry = LocateRegistry.createRegistry(Configuration.RMI_GATEWAY_PORT);
            registry.rebind("googol", stub);
            System.out.println("Gateway Server ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in GatewayImpl.main: " + re);
        }
        new Scanner(System.in).nextLine();
        System.exit(0);
    }
}
