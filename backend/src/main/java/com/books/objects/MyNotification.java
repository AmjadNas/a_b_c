package com.books.objects;


import com.books.utils.Constants;
import com.books.utils.ToJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.List;

public class MyNotification extends ToJSON {
    private String username;
   // private String text;
    private String type;
    private String sender;
    private long date;

    public MyNotification(String type, String sender, String username, long date) {
      //  this.text = text;
        this.type = type;
        this.sender = sender;
        this.username = username;
        this.date = date;
    }

    @Override
    public JSONObject toJson(){
        JSONObject onj = new JSONObject();
        onj.put(Constants.TYPE, type);
        onj.put(Constants.SENDER, sender);
        onj.put(Constants.USERNAME, username);
       // onj.put(Constants.MESSAGE, text);
        onj.put(Constants.DATE, date);
        return onj;
    }

   /* public static String toJson(List<MyNotification> notifications){
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (notifications == null) {
                return null;
            }
            if (notifications.size() == 0) {
                return null;
            }
            for (MyNotification it : notifications) {
                if (it != null) {
                    arr.add(it.toJson());
                }
            }
            object.put(Constants.NOTIFICATIONS, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }*/
}
