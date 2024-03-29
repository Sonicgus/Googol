import java.rmi.*;
import java.util.Scanner;

public class Client {

    public static void menu() {
        System.out.println("Choose an option:");
        System.out.println("1. Indexar novo URL ");
        System.out.println("2. Pesquisar páginas que contenham um conjunto de termos");
        System.out.println("3. Consultar lista de páginas com ligação para uma página específica");
        System.out.println("4. Página de administração atualizada em tempo real");
        System.out.println("0. Sair");
    }

    public static void main(String args[]) {
        try {
            Scanner scanner = new Scanner(System.in);
            GatewayInterface g = (GatewayInterface) Naming.lookup("rmi://localhost:7000/googol");

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
                        System.out.println("Enter the keyword to search: ");
                        String keyword = scanner.nextLine();
                        System.out.println(g.search(keyword));
                        break;
                    case "3":
                        System.out.println("Enter the link to search: ");
                        String link = scanner.nextLine();
                        System.out.println(g.linkInfo(link));
                        break;
                    case "4":
                        System.out.println(g.top10());
                        break;
                    case "0":
                        System.out.println("Exiting...");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid option");
                        break;
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

    }

}