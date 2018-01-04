package abbasi.com.nixor.misc;

/**
 * Created by anjum on 7/22/2016.
 */
public class addToBuddy {
    String username;
    String photourl;

    public String getPhotourl() {
        return photourl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public addToBuddy() {
    }

    public addToBuddy(String photourl, String username) {
        this.photourl = photourl;
        this.username = username;
    }
}

