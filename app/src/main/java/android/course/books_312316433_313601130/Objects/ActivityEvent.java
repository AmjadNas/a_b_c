package android.course.books_312316433_313601130.Objects;

import android.content.ContentValues;
import android.course.books_312316433_313601130.Utils.Constants;

import org.json.JSONObject;

import java.util.Objects;

public class ActivityEvent {

    private long id;
    private String username;
    private String message;

    public ActivityEvent(long id) {
        this.id = id;
    }

    public ActivityEvent(String username, String message) {
        id = System.currentTimeMillis();
        this.message = message;
        this.username = username;

    }

    public ActivityEvent(long id, String username, String message) {
        this.id = id;
        this.message = message;
        this.username = username;
    }

    public static ActivityEvent fromJson(JSONObject jsonObject) {
        try {
            long id = jsonObject.getLong(Constants.ID);
            String username = jsonObject.getString(Constants.USERNAME);
            String message = jsonObject.getString(Constants.MESSAGE);

            ActivityEvent e = new ActivityEvent(id, username,message);

            return e;
        }catch (Exception e){

        }
        return null;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Constants.ID, id);
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.MESSAGE, message);
        return cv;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityEvent that = (ActivityEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
