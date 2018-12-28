package android.course.a_b_c.Objects;

import android.content.ContentValues;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.Converter;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Bitmap thumb;
    private boolean hasOnlineImage;
    private int followingCount;
    private int followersCount;

    public User(String username, String firstName, String lastName, String email, Bitmap thumb) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.thumb = thumb;
        followingCount = 0;
        followersCount = 0;

    }

    public User(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        followingCount = 0;
        followersCount = 0;
    }

    public User(String username) {
        this.username = username;
    }

    public Bitmap getThumbnail() {
        if (thumb != null) {
            Bitmap t = ThumbnailUtils.extractThumbnail(thumb, 50, 50);
            return t;
        }
        return null;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return username.equals(user.username);

    }

    public ContentValues getContent(){

        ContentValues cv = new ContentValues();

        cv.put(Constants.USERNAME, username);
        cv.put(Constants.FNAME, firstName);
        cv.put(Constants.LNAME, lastName);
        cv.put(Constants.EMAIL, email);

        if(thumb != null)
            cv.put(Constants.IMAGE, Converter.getBitmapAsByteArray(thumb));

        return cv;

    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public static User parseJSON(JSONObject object){
        User user;
        try {
            String username = object.getString(Constants.USERNAME);
            String fname = object.getString(Constants.FNAME);
            String lname = object.getString(Constants.LNAME);
            String email =  object.getString(Constants.EMAIL);
            boolean has =  object.getBoolean(Constants.HAS_IMAGE);

            user = new User(username, fname, lname, email);
            user.setHasOnlineImage(has);
            return user;



        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setHasOnlineImage(boolean hasOnlineImage) {
        this.hasOnlineImage = hasOnlineImage;
    }

    public boolean isHasOnlineImage() {
        return hasOnlineImage;
    }

    public void removeFollowing() {
        followingCount--;
    }

    public boolean addFollowing() {
        followingCount++;

        return true;
    }

    public void addfollower(){
        followersCount++;
    }

    public void removeFfollower() {
        followersCount--;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }
}
