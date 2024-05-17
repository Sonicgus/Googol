package pt.uc.ga.webserver;

import java.util.List;

public class UserResponse {
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public List<Integer> getSubmitted() {
        return submitted;
    }

    public void setSubmitted(List<Integer> submitted) {
        this.submitted = submitted;
    }

    private String about;
    private int created;
    private String id;
    private int karma;
    private List<Integer> submitted;

}
