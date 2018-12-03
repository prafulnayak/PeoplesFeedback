package shamgar.org.peoplesfeedback.Model;

import android.content.Context;

public class HomeModel
{
    String url;
    String description;
    String tag;
    String mla;
    double lat;
    double lon;
    String postedby;
    String status;
    String publishedon;
    String constituency;
    String mlaid;
    String userImage;
    String mlaImage;
    String username;
    String rating;
    Context context;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public HomeModel(Context context,String url, String description, String tag, String mla, double lat, double lon, String postedby, String publishedon, String constituency, String userImage, String mlaImage, String username) {
        this.url = url;
        this.description = description;
        this.tag = tag;
        this.mla = mla;
        this.lat = lat;
        this.lon = lon;
        this.postedby = postedby;
        this.publishedon = publishedon;
        this.constituency = constituency;
        this.userImage = userImage;
        this.mlaImage = mlaImage;
        this.username = username;
        this.context=context;
    }

    public HomeModel(String url, String description, String tag, String mla, double lat, double lon, String postedby, String status, String publishedon, String constituency, String mlaid, String userImage, String mlaImage, String username, String rating) {

        this.url = url;
        this.description = description;
        this.tag = tag;
        this.mla = mla;
        this.lat = lat;
        this.lon = lon;
        this.postedby = postedby;
        this.status = status;
        this.publishedon = publishedon;
        this.constituency = constituency;
        this.mlaid = mlaid;
        this.userImage = userImage;
        this.mlaImage = mlaImage;
        this.username = username;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {

        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getMlaImage() {
        return mlaImage;
    }

    public void setMlaImage(String mlaImage) {
        this.mlaImage = mlaImage;
    }

    public HomeModel()
    {

    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMla() {
        return mla;
    }

    public void setMla(String mla) {
        this.mla = mla;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublishedon() {
        return publishedon;
    }

    public void setPublishedon(String publishedon) {
        this.publishedon = publishedon;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getMlaid() {
        return mlaid;
    }

    public void setMlaid(String mlaid) {
        this.mlaid = mlaid;
    }
}
