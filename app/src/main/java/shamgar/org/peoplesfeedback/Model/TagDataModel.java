package shamgar.org.peoplesfeedback.Model;

import java.util.ArrayList;

public class TagDataModel {

    ArrayList<String> tagNames,rating,votes;

    public TagDataModel(ArrayList<String> tagNames, ArrayList<String> rating, ArrayList<String> votes) {
        this.tagNames = tagNames;
        this.rating = rating;
        this.votes = votes;
    }

    public ArrayList<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(ArrayList<String> tagNames) {
        this.tagNames = tagNames;
    }

    public ArrayList<String> getRating() {
        return rating;
    }

    public void setRating(ArrayList<String> rating) {
        this.rating = rating;
    }

    public ArrayList<String> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<String> votes) {
        this.votes = votes;
    }
}
