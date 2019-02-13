package android.course.books.Objects;

import android.content.ContentValues;
import android.course.books.Utils.Constants;
import android.course.books.Utils.DateUtil;

import org.json.JSONObject;

public class Message {

    private String sender;
    private String username;
    private String subject;
    private String message;
    private long dateFormat;

    public Message(String sender, String username, String subject, String message) {
        this.sender =sender;
        this.username = username;
        this.subject =subject;
        this.message = message;
        dateFormat = System.currentTimeMillis();
    }

    public static Message fromJson(JSONObject jsonObject) {
        try {
            String sender = jsonObject.getString(Constants.SENDER);
            String username = jsonObject.getString(Constants.USERNAME);
            String subject = jsonObject.getString(Constants.SUBJECT);
            String message = jsonObject.getString(Constants.MESSAGE);
            long dateFormat = jsonObject.getLong(Constants.DATE);

            Message m = new Message(sender,username,subject,message);
            m.setDate(dateFormat);

            return m;
        }catch (Exception e){

        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getDateString(){
        return DateUtil.getCurrentDate(dateFormat);
    }

    public void setDate(long date){
        dateFormat = date;
    }

    public void setDate(String date) {
        dateFormat = DateUtil.getCurrentDate(date);
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public static Message parseJSON( JSONObject jsonObject) {
return null;
       /* try {
            User sender;
            String username = c.getSender().getUsername();
            String str = jsonObject.getString(Constants.SENDER);

            if(str.equals(username))
                sender = c.getSender();
            else
                sender = c.getGuest();
            String desc = jsonObject.getString(Constants.DESCRIPTION);
            long df = jsonObject.getLong(Constants.DATE);

            Message m = new Message(sender, desc);
            m.setDate(df);

            return m;

        } catch (JSONException e) {
            return null;
        }
*/
    }

    public ContentValues getContentValue() {
        ContentValues cv = new ContentValues();
        cv.put(Constants.SENDER, sender);
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.SUBJECT, subject);
        cv.put(Constants.MESSAGE, message);
        cv.put(Constants.DATE, DateUtil.getCurrentDate(dateFormat));
        return cv;
    }
}
