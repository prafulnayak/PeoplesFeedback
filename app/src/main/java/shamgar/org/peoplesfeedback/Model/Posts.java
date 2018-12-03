package shamgar.org.peoplesfeedback.Model;

public class Posts
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

    public Posts()
    {

    }


        public Posts(String url, String description, String tag, String mla, double lat, double lon, String postedby, String status, String publishedon, String constituency, String mlaid) {
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
