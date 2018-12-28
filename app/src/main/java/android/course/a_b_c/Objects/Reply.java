package android.course.a_b_c.Objects;

import android.content.ContentValues;
import android.course.a_b_c.Utils.Constants;

import org.json.JSONObject;

public class Reply {

    private long actID;
    private String username;
    private String message;

    public Reply(long actID, String username, String message) {
        this.actID = actID;
        this.username = username;
        this.message = message;
    }

    public static Reply fromJson(JSONObject jsonObject) {
        try{
            long id = jsonObject.getLong(Constants.ID);
            String username = jsonObject.getString(Constants.USERNAME);
            String message = jsonObject.getString(Constants.MESSAGE);

            Reply r = new Reply(id, username, message);
            return r;
        }catch (Exception e){
        }
        return null;
    }

    public long getActID() {
        return actID;
    }

    public void setActID(long actID) {
        this.actID = actID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(Constants.ID, actID);
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.MESSAGE, message);

        return cv;
    }
}
