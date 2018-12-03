package shamgar.org.peoplesfeedback.Model;

public class Politics {
    public Politics() {
    }

    String politicianId;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Politics(String politicianId) {
        this.politicianId = politicianId;
    }

    public String getPoliticianId() {
        return politicianId;
    }

    public void setPoliticianId(String politicianId) {
        this.politicianId = politicianId;
    }
}
