package pt.uc.ga;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Main class that starts the Gateway server.
 */
public class Main {
    /**
     * Main method that starts the Gateway server.
     *
     * @param args
     */
    public static void main(String[] args) {

        if (args.length != 5) {
            System.out.println("Usage: java -jar rmi-gateway.jar <multicast_adress> <multicast_port> <PORT_B> <rmi-registry-name> <rmi-registry-port>");
            System.exit(1);
        }


        try {
            Gateway g = new Gateway(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3], Integer.parseInt(args[4]));
            IGateway stub = (IGateway) UnicastRemoteObject.exportObject(g, 0);

            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[4]));

            registry.rebind("googol", stub);
            System.out.println("Gateway Server ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in GatewayImpl.main: " + re);
        }
        new Scanner(System.in).nextLine();
        System.exit(0);
    }
}
