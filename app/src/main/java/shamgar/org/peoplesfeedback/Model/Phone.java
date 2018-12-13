package shamgar.org.peoplesfeedback.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Phone implements Serializable {
    @SerializedName("name")
    public int name;

    @SerializedName("phone")
    public int phone;

    @SerializedName("Array")
    public List array;

}
