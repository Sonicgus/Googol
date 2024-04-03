package pt.uc.ga;

import java.util.HashSet;

public class SiteInfo implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private int searchCount;

    private String title;
    private String description;
    private HashSet<String> urls;
    private String url;
    private int numUrls;


    public SiteInfo() {
        this.url = "";
        this.title = "";
        this.description = "";
        this.urls = new HashSet<>();
        this.numUrls = 0;
        this.searchCount = 0;
    }


    public String getTitle() {
        return title;
    }

    public int getNumUrls() {
        return numUrls;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
    public int getSearchCount() {
        return searchCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<String> getUrls() {
        return urls;
    }

    public void setUrls(HashSet<String> urls) {
        this.urls = urls;
        this.numUrls = urls.size();
    }
    public void addSearchCount() {
        this.searchCount++;
    }
}
