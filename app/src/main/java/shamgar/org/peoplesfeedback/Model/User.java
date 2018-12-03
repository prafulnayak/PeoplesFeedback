package shamgar.org.peoplesfeedback.Model;

class User {
    String peopleId;

    public User() {
    }

    public User(String peopleId) {

        this.peopleId = peopleId;
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }
}
