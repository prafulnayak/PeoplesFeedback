package shamgar.org.peoplesfeedback.Model;

public class News {
    private String postId;
    private String postedBy;
    private String userUrl;
    private String heading;
    private String description;
    private String constituancy;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String address;
    private String mla;
    private String mlaImageUrl;
    private String votePercentage;
    private String tag;
    private int views;
    private int likes;
    private int shares;
    private String postedDate;
    private int status;


    public News() {
    }

    public News(String postId, String postedBy, String userUrl, String heading, String description, String constituancy, String imageUrl, Double latitude, Double longitude, String address, String mla, String mlaImageUrl, String votePercentage, String tag, int views, int likes, int shares, String postedDate, int status) {
        this.postId = postId;
        this.postedBy = postedBy;
        this.userUrl = userUrl;
        this.heading = heading;
        this.description = description;
        this.constituancy = constituancy;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.mla = mla;
        this.mlaImageUrl = mlaImageUrl;
        this.votePercentage = votePercentage;
        this.tag = tag;
        this.views = views;
        this.likes = likes;
        this.shares = shares;
        this.postedDate = postedDate;
        this.status = status;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getConstituancy() {
        return constituancy;
    }

    public void setConstituancy(String constituancy) {
        this.constituancy = constituancy;
    }

    public String getMlaImageUrl() {
        return mlaImageUrl;
    }

    public void setMlaImageUrl(String mlaImageUrl) {
        this.mlaImageUrl = mlaImageUrl;
    }

    public String getVotePercentage() {
        return votePercentage;
    }

    public void setVotePercentage(String votePercentage) {
        this.votePercentage = votePercentage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMla() {
        return mla;
    }

    public void setMla(String mla) {
        this.mla = mla;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAll(News news) {
        this.postId = news.postId;
        this.postedBy = news.postedBy;
        this.userUrl = news.userUrl;
        this.heading = news.heading;
        this.description = news.description;
        this.constituancy = news.constituancy;
        this.imageUrl = news.imageUrl;
        this.latitude = news.latitude;
        this.longitude = news.longitude;
        this.address = news.address;
        this.mla = news.mla;
        this.mlaImageUrl = news.mlaImageUrl;
        this.votePercentage = news.votePercentage;
        this.tag = news.tag;
        this.views = news.views;
        this.likes = news.likes;
        this.shares = news.shares;
        this.postedDate = news.postedDate;
        this.status = news.status;
    }
}
