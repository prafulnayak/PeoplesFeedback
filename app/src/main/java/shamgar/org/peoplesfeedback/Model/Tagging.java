package shamgar.org.peoplesfeedback.Model;

public class Tagging {
    String postId;

    public Tagging() {
    }

    public Tagging(String postId) {

        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
