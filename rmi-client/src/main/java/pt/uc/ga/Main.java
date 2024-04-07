package pt.uc.ga;

/**
 * Main class that starts the client
 */
public class Main {
    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar rmi-client.jar <RMI_HOST> <RMI_GATEWAY_PORT>");
            System.exit(1);
        }

        String RMI_HOST = args[0];
        int RMI_GATEWAY_PORT = Integer.parseInt(args[1]);

        Client client = new Client(RMI_HOST, RMI_GATEWAY_PORT);
        client.start();
    }
}