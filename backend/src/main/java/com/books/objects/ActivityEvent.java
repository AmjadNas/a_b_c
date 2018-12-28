package com.books.objects;

import com.books.utils.Constants;
import com.books.utils.ToJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class ActivityEvent extends ToJSON implements Comparable<ActivityEvent> {

    private long id;
    private String username;
    private String message;

    public ActivityEvent(long id) {
        this.id = id;
    }

    public ActivityEvent(long id, String username, String message) {
        this.id = id;
        this.message = message;
        this.username = username;

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

    /*public static String toJson(List<ActivityEvent> list){
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (list == null) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
            for (ActivityEvent it : list) {
                if (it != null) {
                    arr.add(it.toJson());
                }
            }
            object.put(Constants.ACTIVITIES, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }*/
    @Override
    public JSONObject toJson() {
        JSONObject cv = new JSONObject();
        cv.put(Constants.ID, id);
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.MESSAGE, message);
        return cv;
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

    @Override
    public int compareTo(ActivityEvent activityEvent) {
        if (id > activityEvent.id)
            return -1;
        else if (id < activityEvent.id)
            return 1;

        return 0;
    }
}
