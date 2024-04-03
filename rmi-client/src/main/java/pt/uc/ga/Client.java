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
                Registry registry = LocateRegistry.getRegistry("localhost", Configuration.RMI_GATEWAY_PORT);
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
                            System.out.println("Press 0 to exit");
                            System.out.println("For next page press 2 and for previous page press 1");
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
                                if (currentPage < 100) {
                                    currentPage++;
                                    System.out.println(g.search(keywords, currentPage));
                                } else {
                                    System.out.println("No more pages");
                                }
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
                        System.out.println(g.getTop10());
                        break;
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
                    Registry registry = LocateRegistry.getRegistry("localhost", Configuration.RMI_GATEWAY_PORT);
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
        System.out.println("1. Indexar novo URL ");
        System.out.println("2. Pesquisar páginas que contenham um conjunto de termos");
        System.out.println("3. Consultar lista de páginas com ligação para uma página específica");
        System.out.println("4. Página de administração atualizada em tempo real");
        System.out.println("0. Sair");
        System.out.print("Escolha:");
    }
}
