package shamgar.org.peoplesfeedback.Model;

public class Followings {
    public Followings() {
    }

    Politics politics;
    User user;
    Party party;

    public Followings(Politics politics, User user, Party party) {
        this.politics = politics;
        this.user = user;
        this.party = party;
    }

    public Politics getPolitics() {
        return politics;
    }

    public void setPolitics(Politics politics) {
        this.politics = politics;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }
}
