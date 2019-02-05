package shamgar.org.peoplesfeedback.Model;

public class SpamModel {

    String state,constituency,userNum,tagid;

    public SpamModel(String state, String constituency, String userNum, String tagid) {
        this.state = state;
        this.constituency = constituency;
        this.userNum = userNum;
        this.tagid = tagid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }
}
