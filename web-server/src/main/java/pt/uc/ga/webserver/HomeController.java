package pt.uc.ga.webserver;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.uc.ga.IGateway;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Controller
public class HomeController {

    private final String RMI_HOST = "localhost";
    private final int RMI_GATEWAY_PORT = 1099;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // List<SearchResult> or PaginatedSearchResult
    @GetMapping("/search")
    public String search(@RequestParam String q, @RequestParam(required = false) Integer page, Model model) throws RemoteException, NotBoundException, MalformedURLException {
        Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
        IGateway gateway = (IGateway) registry.lookup("googol");

        // If page is null, assign it a default value
        if (page == null || page < 1) {
            page = 1; // or any other default value
        }

        int totalpages = 10;

        String results = gateway.search(q, page - 1);
        model.addAttribute("results", results);
        model.addAttribute("q", q);
        model.addAttribute("page", page);
        model.addAttribute("totalpages", totalpages);

        return "search";
    }

    @GetMapping("/linkinfo")
    public String linkInfo(@RequestParam(required = false) String q, Model model) throws RemoteException, NotBoundException, MalformedURLException {

        if (q == null) {
            model.addAttribute("results", "");
            return "linkInfo";
        }

        Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
        IGateway gateway = (IGateway) registry.lookup("googol");

        String results = gateway.linkInfo(q);

        model.addAttribute("results", results);

        return "linkInfo";
    }

    @GetMapping("/addlink")
    public String addLink(@RequestParam(required = false) String q, Model model) throws RemoteException, NotBoundException, MalformedURLException {

        if (q == null) {
            model.addAttribute("results", "");
            return "addLink";
        }

        Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
        IGateway gateway = (IGateway) registry.lookup("googol");

        String results = gateway.addLink(q);

        model.addAttribute("results", results);

        return "addLink";
    }

    @GetMapping("/admin")
    public String admin(Model model) throws RemoteException, NotBoundException, MalformedURLException {

        Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
        IGateway gateway = (IGateway) registry.lookup("googol");

        String results = gateway.getAdminPage(false);

        model.addAttribute("results", results);

        return "admin";

    }
}