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
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final String RMI_HOST = "localhost";
    private final int RMI_GATEWAY_PORT = 1099;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/searchteste")
    public String searchteste() {
        return "search";
    }

    public List<SearchResult> parseSearchResults(String input) {
        String[] blocks = input.split("URL:");
        List<SearchResult> searchResults = new ArrayList<>();

        for (int i = 1; i < blocks.length; i++) {
            String[] parts = blocks[i].split("Title:");
            if (parts.length < 2) continue;
            String url = parts[0].trim();

            parts = parts[1].split("Description:");
            if (parts.length < 2) continue;
            String title = parts[0].trim();

            parts = parts[1].split("Number of URLs pointing to this URL:");
            if (parts.length < 2) continue;
            String description = parts[0].trim();

            String number = parts[1].trim();

            searchResults.add(new SearchResult(url, title, description, number));
        }

        return searchResults;
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

    public class SearchResult {
        private String url;
        private String title;
        private String description;
        private String number;

        public SearchResult(String url, String title, String description, String number) {
            this.url = url;
            this.title = title;
            this.description = description;
            this.number = number;
        }
    }


}