package pt.uc.ga;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) {
            System.out.println("Usage: java -jar url-queue.jar <port-a> <port-b>");
            System.out.println("or: java -jar url-queue.jar <port-a> <port-b> <initial_index_url>");
            System.exit(1);
        }

        int PORT_A = Integer.parseInt(args[0]);
        int PORT_B = Integer.parseInt(args[1]);

        if (args.length == 3) {
            String initialUrl = args[2];
            UrlQueue urlQueue = new UrlQueue(PORT_A, PORT_B, initialUrl);
            urlQueue.start();
        } else {
            UrlQueue urlQueue = new UrlQueue(PORT_A, PORT_B);
            urlQueue.start();
        }


    }
}