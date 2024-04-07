package pt.uc.ga;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar barrel.jar <id>");
            System.exit(1);
        }

        int id = Integer.parseInt(args[0]);
        Barrel barrel = new Barrel(id);
        barrel.start();
    }
}