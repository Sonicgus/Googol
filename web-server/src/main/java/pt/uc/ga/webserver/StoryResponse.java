package pt.uc.ga.webserver;

public class StoryResponse {
    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public int getDescendants() {
        return descendants;
    }

    public void setDescendants(int descendants) {
        this.descendants = descendants;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getKids() {
        return kids;
    }

    public void setKids(int[] kids) {
        this.kids = kids;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * {
     * "by": "feross",
     * "descendants": 53,
     * "id": 40380975,
     * "kids": [
     * 40382272,
     * 40382601,
     * 40383650,
     * 40383354,
     * 40382856,
     * 40382821,
     * 40383710,
     * 40382662,
     * 40383725,
     * 40382658,
     * 40383050,
     * 40383443,
     * 40383437,
     * 40382752,
     * 40382591,
     * 40382917,
     * 40382647,
     * 40382525,
     * 40383512,
     * 40382957,
     * 40383155,
     * 40382885,
     * 40382845,
     * 40382570
     * ],
     * "score": 76,
     * "time": 1715880741,
     * "title": "Crypto brothers front-ran the front-runners",
     * "type": "story",
     * "url": "https://www.bloomberg.com/opinion/articles/2024-05-16/crypto-brothers-front-ran-the-front-runners"
     * }
     */
    private String by;
    private int descendants;
    private int id;
    private int[] kids;
    private int score;
    private int time;
    private String title;
    private String type;
    private String url;

}
