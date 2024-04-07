package pt.uc.ga;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    private IGateway g;
    private final Scanner scanner;
    private boolean admin;
    private final Object lock;

    private final String RMI_HOST;
    private final int RMI_GATEWAY_PORT;


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
                    String response = g.getAdminPage(true);
                    if (admin)
                        System.out.println(response);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    getGateway();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Client(String RMI_HOST, int RMI_GATEWAY_PORT) {

        scanner = new Scanner(System.in);
        admin = false;
        lock = new Object();

        this.RMI_HOST = RMI_HOST;
        this.RMI_GATEWAY_PORT = RMI_GATEWAY_PORT;
        getGateway();

        Thread t = new AdminThread();
        t.start();
    }

    private void getGateway() {
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(this.RMI_HOST, this.RMI_GATEWAY_PORT);
                this.g = (IGateway) registry.lookup("googol");
                return;
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
        String url;
        do {
            System.out.println("Enter the URL to index: ");
            url = scanner.nextLine();
        } while (!isUrl(url));

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
            String keywords = scanner.nextLine();

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

    private boolean isUrl(String url) {
        if (url == null) {
            return false;
        } else
            return url.matches("^(http|https)://.*$");
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
                            System.out.println(g.getAdminPage(false));
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
                getGateway();
            }
        }
    }
}
