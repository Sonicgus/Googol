package pt.uc.ga;

import java.util.HashSet;

public class SiteInfo {
    private String title;
    private String description;
    private HashSet<String> urls;

    public SiteInfo() {
        this.urls = new HashSet<>();
    }

    public SiteInfo(String title, String description, HashSet<String> urls) {
        this.title = title;
        this.description = description;
        this.urls = urls;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<String> getUrls() {
        return urls;
    }

    public void setUrls(HashSet<String> urls) {
        this.urls = urls;
    }
}
