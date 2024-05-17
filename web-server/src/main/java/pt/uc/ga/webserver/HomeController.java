package pt.uc.ga.webserver;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import pt.uc.ga.IGateway;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

@Controller
public class HomeController {
    private final SimpMessagingTemplate template;

    private final String RMI_HOST = "localhost";
    private final int RMI_GATEWAY_PORT = 1099;

    private final WeatherService weatherService;
    private final HackerNewsService hackerNewsService;


    public HomeController(SimpMessagingTemplate template, WeatherService weatherService, HackerNewsService hackerNewsService) {
        this.weatherService = weatherService;
        this.hackerNewsService = hackerNewsService;
        this.template = template;
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(HttpServletRequest request, Exception e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("exception", e);
        return mav;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            WeatherResponse weatherInfo = weatherService.getWeather("Coimbra");
            String weatherIcon = weatherService.getWeatherIcon(weatherInfo.getWeather().get(0).getMain());
            model.addAttribute("weatherIcon", weatherIcon);
            model.addAttribute("weather", weatherInfo);

            model.addAttribute("Temp", weatherInfo.getMain().getTemp());
            model.addAttribute("minTemp", weatherInfo.getMain().getTemp_min());
            model.addAttribute("maxTemp", weatherInfo.getMain().getTemp_max());

            model.addAttribute("humidity", weatherInfo.getMain().getHumidity());
            model.addAttribute("windSpeed", weatherInfo.getWind().getSpeed());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return "index";
    }


    // List<SearchResult> or PaginatedSearchResult
    @GetMapping("/search")
    public String search(@RequestParam String q, @RequestParam(required = false) Integer page, Model model) {
        try {
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
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            // Handle the exception here. For example, you can log the error and return an error page.
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }
    }

    @GetMapping("/linkinfo")
    public String linkInfo(@RequestParam(required = false) String q, Model model) {
        if (q == null) {
            model.addAttribute("results", "");
            return "linkInfo";
        }

        try {
            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
            IGateway gateway = (IGateway) registry.lookup("googol");

            String results = gateway.linkInfo(q);

            model.addAttribute("results", results);
        } catch (RemoteException | NotBoundException e) {
            // Handle the exception here. For example, you can log the error and return an error page.
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }

        return "linkInfo";
    }

    @GetMapping("/addlink")
    public String addLink(@RequestParam(required = false) String q, Model model) {
        if (q == null) {
            model.addAttribute("results", "");
            return "addLink";
        }

        try {
            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
            IGateway gateway = (IGateway) registry.lookup("googol");

            String results = gateway.addLink(q);

            model.addAttribute("results", results);
        } catch (RemoteException | NotBoundException e) {
            // Handle the exception here. For example, you can log the error and return an error page.
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }

        return "addLink";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        try {
            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
            IGateway gateway = (IGateway) registry.lookup("googol");

            String results = gateway.getAdminPage(false);

            model.addAttribute("results", results);

            return "admin";
        } catch (RemoteException | NotBoundException e) {
            // Handle the exception here. For example, you can log the error and return an error page.
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while processing your request.");
            return "error";
        }
    }

    @GetMapping("/indexhacker")
    public String indexhacker(@RequestParam(required = false) String q, Model model) {
        if (q != null) {
            try {
                List response = hackerNewsService.getTopnews(q);
                model.addAttribute("response", response);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "An error occurred while fetching the top stories.");
            }
        }

        return "indexhacker";
    }

    @GetMapping("/weather")
    public String weather(@RequestParam(required = false) String city,
                          @RequestParam(required = false) Double lat,
                          @RequestParam(required = false) Double lon,
                          Model model) {
        try {
            WeatherResponse weatherInfo;
            if (city != null) {
                weatherInfo = weatherService.getWeather(city);
            } else if (lat != null && lon != null) {
                weatherInfo = weatherService.getWeatherByCoordinates(lat, lon);
            } else {
                return "weather";
            }

            String weatherIcon = weatherService.getWeatherIcon(weatherInfo.getWeather().get(0).getMain());
            model.addAttribute("weatherIcon", weatherIcon);
            model.addAttribute("weather", weatherInfo);

            model.addAttribute("Temp", weatherInfo.getMain().getTemp());
            model.addAttribute("minTemp", weatherInfo.getMain().getTemp_min());
            model.addAttribute("maxTemp", weatherInfo.getMain().getTemp_max());

            model.addAttribute("humidity", weatherInfo.getMain().getHumidity());
            model.addAttribute("windSpeed", weatherInfo.getWind().getSpeed());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return "weather";
    }

    public void sendThread() throws InterruptedException {
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
                IGateway gateway = (IGateway) registry.lookup("googol");
                String reply = gateway.getAdminPage(true);

                // Enviar mensagem para os clientes WebSocket
                this.template.convertAndSend("/topic/messages", new Message(reply));

                System.out.println("Sent message: " + reply);
            } catch (RemoteException | NotBoundException e) {
                Thread.sleep(5000);
            }
        }
    }


}
