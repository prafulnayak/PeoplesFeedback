package shamgar.org.peoplesfeedback.Model;

import com.google.gson.annotations.SerializedName;

public class StateCm {

    private String stateName;
    @SerializedName("CM")
    private String CM;

    @SerializedName("Votes")
    private Long Votes;

    @SerializedName("Rating")
    private Long Rating;

    @SerializedName("state_url")
    private String state_url;

    @SerializedName("cm_url")
    private String cm_url;

    public StateCm() {
    }

    public StateCm(String CM, Long votes, Long rating, String state_url, String cm_url) {
        this.CM = CM;
        Votes = votes;
        Rating = rating;
        this.state_url = state_url;
        this.cm_url = cm_url;
    }

    public StateCm(String stateName, String CM, Long votes, Long rating, String state_url, String cm_url) {
        this.stateName = stateName;
        this.CM = CM;
        Votes = votes;
        Rating = rating;
        this.state_url = state_url;
        this.cm_url = cm_url;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCM() {
        return CM;
    }

    public void setCM(String CM) {
        this.CM = CM;
    }

    public Long getVotes() {
        return Votes;
    }

    public void setVotes(Long votes) {
        Votes = votes;
    }

    public Long getRating() {
        return Rating;
    }

    public void setRating(Long rating) {
        Rating = rating;
    }

    public String getState_url() {
        return state_url;
    }

    public void setState_url(String state_url) {
        this.state_url = state_url;
    }

    public String getCm_url() {
        return cm_url;
    }

    public void setCm_url(String cm_url) {
        this.cm_url = cm_url;
    }
}
