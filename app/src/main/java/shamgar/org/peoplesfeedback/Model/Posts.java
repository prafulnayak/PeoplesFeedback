package shamgar.org.peoplesfeedback.Model;

public class Posts {

    String user;
    String latitude;
    String longitude;
    String address;
    String imageUrl;
    String heading;
    String description;
    String postedOn;
    String tagId;
    String currentUserId;
    int like;
    int share;
    int view;



    public Posts() {
    }



    public Posts(String user, String latitude, String longitude, String address, String imageUrl, String heading, String description, String postedOn, String tagId, String currentUserId) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.imageUrl = imageUrl;
        this.heading = heading;
        this.description = description;
        this.postedOn = postedOn;
        this.tagId = tagId;
        this.currentUserId=currentUserId;

    }
    public Posts(String user, String latitude, String longitude, String address, String imageUrl, String heading, String description, String postedOn, String tagId, int like, int share, int view) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.imageUrl = imageUrl;
        this.heading = heading;
        this.description = description;
        this.postedOn = postedOn;
        this.tagId = tagId;
        this.like = like;
        this.share = share;
        this.view = view;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }
    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId)
    {
        this.currentUserId = currentUserId;
    }
}
