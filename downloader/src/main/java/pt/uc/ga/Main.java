package pt.uc.ga;

public class Main {
    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Usage: java -jar downloader.jar <PORT_A> <PORT_B> <MULTICAST_ADDRESS> <MULTICAST_PORT> <MAXIMUM_REFERENCE_LINKS> <HOST_ADRESS>");
            System.exit(1);
        }

        Downloader downloader = new Downloader(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
        downloader.start();

    }
}