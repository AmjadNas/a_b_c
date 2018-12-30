package android.course.books_312316433_313601130.Objects;

import android.content.ContentValues;
import android.course.books_312316433_313601130.Utils.Constants;
import android.course.books_312316433_313601130.Utils.DateUtil;

import org.json.JSONObject;

public class Comment {

    private long dateFormat;
    private String chapterTitle;
    private String StoryTiele;
    private String username;
    private String comment;

    public Comment(String chapterTitle, String storyTiele, String username, String comment) {
        this.chapterTitle = chapterTitle;
        StoryTiele = storyTiele;
        this.username = username;
        this.comment = comment;
        dateFormat = System.currentTimeMillis();
    }

    public static Comment fromJson(JSONObject jsonObject) {
        try{
            String chapterTitle = jsonObject.getString(Constants.TITLE);
            String storyTiele = jsonObject.getString(Constants.STORY_TITLE);
            String username = jsonObject.getString(Constants.USERNAME);
            String comment = jsonObject.getString(Constants.COMMENT);
            long dateFormat = jsonObject.getLong(Constants.DATE);

            Comment c = new Comment(chapterTitle, storyTiele, username, comment);
            c.setDateFormat(dateFormat);

            return c;
        }catch (Exception e){}

        return null;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getStoryTiele() {
        return StoryTiele;
    }

    public void setStoryTiele(String storyTiele) {
        StoryTiele = storyTiele;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDateFormat() {
        return dateFormat;
    }

    public String getDateString(){
        return DateUtil.formatDateTime(dateFormat);
    }

    public void setDateFormat(long dateFormat) {
        this.dateFormat = dateFormat;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(Constants.STORY_TITLE, StoryTiele);
        cv.put(Constants.TITLE, chapterTitle);
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.COMMENT, comment);
        cv.put(Constants.DATE, DateUtil.formatDateTime(dateFormat));

        return cv;
    }

    public void setDate(String string) {
        dateFormat = DateUtil.getCurrentDate(string);
    }
}
