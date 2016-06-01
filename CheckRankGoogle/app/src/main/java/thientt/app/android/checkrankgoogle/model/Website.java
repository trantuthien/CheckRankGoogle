package thientt.app.android.checkrankgoogle.model;

/**
 * Created by thientran on 5/17/16.
 */
public class Website {
    private String webName;
    private String webLink;

    public Website(String webName, String webLink) {
        this.webName = webName;
        this.webLink = webLink;
    }

    public Website() {
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    public String toString() {
//        return "Website{" +
//                "webName='" + webName + '\'' +
//                ", webLink='" + webLink + '\'' +
//                '}';
        return webName;
    }
}
