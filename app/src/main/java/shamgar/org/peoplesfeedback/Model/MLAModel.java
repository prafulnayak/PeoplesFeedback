package shamgar.org.peoplesfeedback.Model;

import com.google.gson.annotations.SerializedName;

public class MLAModel {

    private String constituancyName;

    @SerializedName("mla_image")
    private String mla_image;

    @SerializedName("mla_name")
    private String mla_name;

    @SerializedName("party")
    private String party;

    @SerializedName("rating")
    private long rating;

    @SerializedName("Votes")
    private long votes;

    public MLAModel() {
    }

    public MLAModel(String constituancyName, String mla_image, String mla_name, String party, long rating, long votes) {
        this.constituancyName = constituancyName;
        this.mla_image = mla_image;
        this.mla_name = mla_name;
        this.party = party;
        this.rating = rating;
        this.votes = votes;
    }

    public String getConstituancyName() {
        return constituancyName;
    }

    public void setConstituancyName(String constituancyName) {
        this.constituancyName = constituancyName;
    }

    public String getMla_image() {
        return mla_image;
    }

    public void setMla_image(String mla_image) {
        this.mla_image = mla_image;
    }

    public String getMla_name() {
        return mla_name;
    }

    public void setMla_name(String mla_name) {
        this.mla_name = mla_name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }
}
