package com.books.objects;

import com.books.utils.Constants;
import com.books.utils.DateUtil;
import com.books.utils.ToJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.List;


public class Message extends ToJSON {

    private String sender;
    private String username;
    private String subject;
    private String message;
    private long dateFormat;

    public Message(String sender, String username,String subject, String message, long dateFormat) {
        this.sender = sender;
        this.username = username;
        this.subject = subject;
        this.message = message;
        this.dateFormat = dateFormat;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDateFormat(long dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDateFormat() {
        return dateFormat;
    }

    public void setDate(String date) {
        dateFormat = DateUtil.getCurrentDate(date);
    }

    public void setDate(Date date) {
        dateFormat = DateUtil.getCurrentDate(date);
    }

    public void setDate(long date) {
        dateFormat = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (dateFormat != message1.dateFormat) return false;
        return message.equals(message1.message);

    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + (int) (dateFormat ^ (dateFormat >>> 32));
        return result;
    }

    @Override
    public JSONObject toJson(){
        JSONObject object;
        try {
            object = new JSONObject();
            object.put(Constants.SENDER, sender);
            object.put(Constants.USERNAME, username);
            object.put(Constants.SUBJECT, subject);
            object.put(Constants.MESSAGE, message);
            object.put(Constants.DATE, dateFormat);

        } catch (JSONException e) {
            return null;
        }
        return object;
    }

    /*public static String toJson(List<Message> itemsList) {
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (itemsList == null) {
                return null;
            }
            if (itemsList.size() == 0) {
                return null;
            }
            for (Message it : itemsList) {
                if (it != null) {
                    arr.add(it.toJson());
                }
            }
            object.put(Constants.MESSAGES, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }*/

}
