package pt.uc.ga;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar downloader.jar <id>");
            System.exit(1);
        }

        int id = Integer.parseInt(args[0]);
        Downloader downloader = new Downloader(id); // Create a new instance of the Downloader class
        new Thread(downloader).start(); // Start the downloader thread

    }
}