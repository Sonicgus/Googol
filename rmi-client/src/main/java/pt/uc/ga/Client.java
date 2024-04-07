package pt.uc.ga;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Client {
    private GatewayInterface g;
    private final Scanner scanner;
    private boolean admin;
    private final Object lock;

    class AdminThread extends Thread {
        public void run() {
            while (true) {
                synchronized (lock) {

                    while (!admin) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                try {
                    String response = g.admin(true);
                    if (admin)
                        System.out.println(response);
                } catch (Exception e) {
                    System.out.println("Exception in admin: " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    public Client() {
        g = getGateway();
        scanner = new Scanner(System.in);
        admin = false;
        lock = new Object();

        Thread t = new AdminThread();
        t.start();
    }

    private GatewayInterface getGateway() {
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(Configuration.RMI_HOST, Configuration.RMI_GATEWAY_PORT);
                return (GatewayInterface) registry.lookup("googol");
            } catch (Exception e) {
                System.out.println("Gateway não disponivel, a tentar em 5 segundos...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void printMenu() {
        System.out.println("Choose an option:");
        System.out.println("1. Indexar novo URL introduzido por utilizador ");
        System.out.println("2. Pesquisar páginas que contenham um conjunto de palavras");
        System.out.println("3. Consultar lista de páginas com ligações para uma página específica");
        System.out.println("4. Página de administração atualizada em tempo real");
        System.out.println("0. Sair");
        System.out.print("Escolha:");
    }


    private void indexUrl() {
        System.out.println("Enter the URL to index: ");
        String url = scanner.nextLine();
        try {
            System.out.println(g.addLink(url));
        } catch (Exception e) {
            System.out.println("Exception in indexUrl: " + e);
            e.printStackTrace();
        }

    }

    private void search() {
        try {
            int currentPage = 0;
            System.out.print("Enter the keyword to search: ");
            String[] keywords_splited = scanner.nextLine().split(" ");

            HashSet<String> keywords = new HashSet<>(Arrays.asList(keywords_splited));

            System.out.println(g.search(keywords, currentPage));
            while (true) {
                System.out.println("Previous page: 1, Next page: 2, Exit: 0, Refresh Page: Enter");
                String option2 = scanner.nextLine();
                if (option2.equals("0")) {
                    break;
                } else if (option2.equals("1")) {
                    if (currentPage > 0) {
                        currentPage--;
                        System.out.println(g.search(keywords, currentPage));
                    } else {
                        System.out.println("No previous pages");
                    }

                } else if (option2.equals("2")) {
                    currentPage++;
                    String result = g.search(keywords, currentPage);
                    System.out.println(result);
                    if (result.equals("No results found") && currentPage > 0) {
                        currentPage--;
                    }

                } else if (option2.isEmpty()) {
                    System.out.println(g.search(keywords, currentPage));
                } else {
                    System.out.println("Invalid option");
                }

            }
        } catch (Exception e) {
            System.out.println("Exception in search: " + e);
            e.printStackTrace();
        }


    }

    private void getLinkInfo() {
        System.out.println("Enter the link to search: ");
        String link = scanner.nextLine();
        try {
            System.out.println(g.linkInfo(link));
        } catch (Exception e) {
            System.out.println("Exception in getLinkInfo: " + e);
            e.printStackTrace();
        }

    }

    public void start() {

        while (true) {
            try {
                printMenu();
                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        indexUrl();
                        break;
                    case "2":
                        search();
                        break;
                    case "3":
                        getLinkInfo();
                        break;
                    case "4":
                        try {
                            System.out.println(g.admin(false));
                        } catch (Exception e) {
                            System.out.println("Exception in admin: " + e);
                            e.printStackTrace();
                        }

                        admin = true;
                        synchronized (lock) {
                            lock.notify();
                        }
                        System.out.println("Press Enter to exit admin page");
                        scanner.nextLine();
                        admin = false;

                        break;
                    case "0":
                        System.out.println("Exiting...");
                        scanner.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid option");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
                g = getGateway();
            }
        }
    }
}
