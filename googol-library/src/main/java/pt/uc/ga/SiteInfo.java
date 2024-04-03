package pt.uc.ga;

import java.util.HashSet;

public class SiteInfo implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

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
    }


    public String getTitle() {
        return title;
    }

    public int getNumUrls() {
        return urls.size();
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

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<String> getUrls() {
        return urls;
    }

}
