package pt.uc.ga.webserver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class HackerNewsService {

    private static final String HACKER_NEWS_URL = "https://hacker-news.firebaseio.com/v0/topstories.json";

    private final RestTemplate restTemplate;

    public HackerNewsService() {
        this.restTemplate = new RestTemplate();
    }

    public List getTopnews(String q) {
        List TopList = restTemplate.getForObject(HACKER_NEWS_URL, List.class);

        List list_that_contains_q = new ArrayList<>();

        for (int i = 0; i < 100 && i < TopList.size(); i++) {
            String url = "https://hacker-news.firebaseio.com/v0/item/" + TopList.get(i) + ".json";
            StoryResponse item = restTemplate.getForObject(url, StoryResponse.class);

            String response = item.getUrl();

            //download the page
            Document doc;
            try {
                doc = Jsoup.connect(response).get();
                String text = doc.text().toLowerCase();

                if (text.contains(q.toLowerCase())) {
                    list_that_contains_q.add(response);
                }
            } catch (Exception e) {
                System.out.println("Failed to download page");
            }
        }

        return list_that_contains_q;
    }

    public List getIdnews(String id) {
        String url = "https://hacker-news.firebaseio.com/v0/user/" + id + ".json";
        UserResponse userResponse = restTemplate.getForObject(url, UserResponse.class);

        List submittedlist = userResponse.getSubmitted();


        List urlList = new ArrayList<>();

        for (int i = 0; i < 100 && i < submittedlist.size(); i++) {
            url = "https://hacker-news.firebaseio.com/v0/item/" + submittedlist.get(i) + ".json";
            StoryResponse item = restTemplate.getForObject(url, StoryResponse.class);

            String response = item.getUrl();
            System.out.println(response);
            if (response != null)
                urlList.add(response);
        }
        System.out.println(urlList);
        return urlList;
    }
}