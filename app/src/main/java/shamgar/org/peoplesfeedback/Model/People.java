package shamgar.org.peoplesfeedback.Model;

import android.nfc.Tag;

public class People {
    public People() {
    }
    String type;
    String name;
    String email;
    String phoneno;
    String state;
    String dist;
    String constituancy;
    String gender;
    String createdon;
    String shortheading;
    String desc;

    public People(String type, String name, String email, String phoneno, String state, String dist, String constituancy, String gender, String createdon, String shortheading, String desc) {
        this.type = type;
        this.name = name;
        this.email = email;
        this.phoneno = phoneno;
        this.state = state;
        this.dist = dist;
        this.constituancy = constituancy;
        this.gender = gender;
        this.createdon = createdon;
        this.shortheading = shortheading;
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getConstituancy() {
        return constituancy;
    }

    public void setConstituancy(String constituancy) {
        this.constituancy = constituancy;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getShortheading() {
        return shortheading;
    }

    public void setShortheading(String shortheading) {
        this.shortheading = shortheading;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
