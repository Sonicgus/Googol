package pt.uc.ga;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar url-queue.jar <port-a> <port-b>");
            System.exit(1);
        }


        int PORT_A = Integer.parseInt(args[0]);
        int PORT_B = Integer.parseInt(args[1]);

        UrlQueue urlQueue = new UrlQueue(PORT_A, PORT_B);
        urlQueue.start();
    }
}