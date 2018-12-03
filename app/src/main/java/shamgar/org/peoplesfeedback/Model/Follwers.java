package shamgar.org.peoplesfeedback.Model;

public class Follwers {
    public Follwers() {
    }

    public Follwers(User user, Politics politics) {

        this.user = user;
        this.politics = politics;
    }

    User user;
    Politics politics;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Politics getPolitics() {
        return politics;
    }

    public void setPolitics(Politics politics) {
        this.politics = politics;
    }
}
