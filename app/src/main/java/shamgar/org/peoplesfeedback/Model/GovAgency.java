package shamgar.org.peoplesfeedback.Model;

import com.google.gson.annotations.SerializedName;

public class GovAgency {
    private String districtName;
    private String govAgencyName;

    @SerializedName("rating")
    private long rating;

    @SerializedName("votes")
    private long votes;

    public GovAgency() {
    }

    public GovAgency(String districtName, String govAgencyName, long rating, long votes) {
        this.districtName = districtName;
        this.govAgencyName = govAgencyName;
        this.rating = rating;
        this.votes = votes;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getGovAgencyName() {
        return govAgencyName;
    }

    public void setGovAgencyName(String govAgencyName) {
        this.govAgencyName = govAgencyName;
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
