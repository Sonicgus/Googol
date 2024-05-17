package pt.uc.ga.webserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pt.uc.ga.IGateway;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class Main {
    @Autowired
    private SimpMessagingTemplate template;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        Main main = context.getBean(Main.class);
        main.run();
    }

    public void run() {
        String RMI_HOST = "localhost";
        int RMI_GATEWAY_PORT = 1099;
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
                IGateway gateway = (IGateway) registry.lookup("googol");
                String reply = gateway.getAdminPage(true);

                template.convertAndSend("/topic/messages", new Message(reply));
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}