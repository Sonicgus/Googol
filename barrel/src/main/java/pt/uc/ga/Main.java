package pt.uc.ga;

/**
 * Main class.
 */
public class Main {
    /**
     * Main method.
     *
     * @param args <id> <MULTICAST_ADDRESS> <MULTICAST_PORT> <RMI_HOST> <RMI_GATEWAY_PORT>
     */
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: java -jar barrel.jar <id> <MULTICAST_ADDRESS> <MULTICAST_PORT> <RMI_HOST> <RMI_GATEWAY_PORT>");
            System.exit(1);
        }

        Barrel barrel = new Barrel(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]), args[3], Integer.parseInt(args[4]));
        barrel.start();
    }
}