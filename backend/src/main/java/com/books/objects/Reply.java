package com.books.objects;


import com.books.utils.Constants;
import com.books.utils.ToJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.List;

public class Reply extends ToJSON {

    private long actID;
    private String username;
    private String message;

    public Reply(long actID, String username, String message) {
        this.actID = actID;
        this.username = username;
        this.message = message;
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

    @Override
    public JSONObject toJson() {
        JSONObject cv = new JSONObject();
        cv.put(Constants.ID, actID);
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.MESSAGE, message);
        return cv;
    }

    /*public static String toJson(List<Reply> list){
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (list == null) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
            for (Reply it : list) {
                if (it != null) {
                    arr.add(it.toJson());
                }
            }
            object.put(Constants.REPLIES, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }*/
}
