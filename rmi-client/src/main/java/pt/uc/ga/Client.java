package pt.uc.ga;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Scanner;

public class Client {
    public void start() {

        GatewayInterface g = null;
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(Configuration.RMI_HOST, Configuration.RMI_GATEWAY_PORT);
                g = (GatewayInterface) registry.lookup("googol");
                break;
            } catch (Exception e) {
                System.out.println("Gateway não disponivel, a tentar em 5 segundos...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        try {
            Scanner scanner = new Scanner(System.in);
            do {
                menu();
                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        System.out.println("Enter the URL to index: ");
                        String url = scanner.nextLine();
                        System.out.println(g.addLink(url));
                        break;
                    case "2":
                        int currentPage = 0;
                        System.out.print("Enter the keyword to search: ");
                        HashSet<String> keywords = new HashSet<String>();
                        String[] keywords_splited = scanner.nextLine().split(" ");

                        for (String keyword : keywords_splited) {
                            keywords.add(keyword);
                        }
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

                            } else if (option2.equals("")) {
                                System.out.println(g.search(keywords, currentPage));
                            } else {
                                System.out.println("Invalid option");
                            }

                        }
                        break;
                    case "3":
                        System.out.println("Enter the link to search: ");
                        String link = scanner.nextLine();
                        System.out.println(g.linkInfo(link));
                        break;
                    case "4":
                        System.out.println(g.admin(false));
                        while (true) {
                            System.out.println(g.admin(true));
                        }
                        //break;
                    case "0":
                        System.out.println("Exiting...");
                        System.exit(0);
                        scanner.close();
                        break;

                    default:
                        System.out.println("Invalid option");
                        break;
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
            while (true) {
                try {
                    Registry registry = LocateRegistry.getRegistry(Configuration.RMI_HOST, Configuration.RMI_GATEWAY_PORT);
                    g = (GatewayInterface) registry.lookup("googol");
                    break;
                } catch (Exception ex) {
                    System.out.println("Gateway não disponivel, a tentar em 5 segundos...");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public static void menu() {
        System.out.println("Choose an option:");
        System.out.println("1. Indexar novo URL introduzido por utilizador ");
        System.out.println("2. Pesquisar páginas que contenham um conjunto de palavras");
        System.out.println("3. Consultar lista de páginas com ligações para uma página específica");
        System.out.println("4. Página de administração atualizada em tempo real");
        System.out.println("0. Sair");
        System.out.print("Escolha:");
    }
}
