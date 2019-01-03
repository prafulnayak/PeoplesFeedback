package shamgar.org.peoplesfeedback.Model;

public class PartyStateMla {
    private String heading;
    private String name;
    private String imageUrl;
    private String rating;

    public PartyStateMla() {
    }

    public PartyStateMla(String heading, String name, String imageUrl, String rating) {
        this.heading = heading;
        this.name = name;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
