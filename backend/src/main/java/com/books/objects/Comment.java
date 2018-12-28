package com.books.objects;

import com.books.utils.Constants;
import com.books.utils.Convector;
import com.books.utils.ToJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.List;

public class Comment extends ToJSON {

    private long datFormate;
    private String chapterTitle;
    private String StoryTiele;
    private String username;
    private String comment;

    public Comment(String chapterTitle, String storyTiele, String username, String comment, long datFormate) {
        this.chapterTitle = chapterTitle;
        StoryTiele = storyTiele;
        this.username = username;
        this.comment = comment;
        this.datFormate = datFormate;

    }

    public long getDatFormate() {
        return datFormate;
    }

    public void setDatFormate(long datFormate) {
        this.datFormate = datFormate;
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

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject cv = new JSONObject();
        cv.put(Constants.STORY_TITLE, StoryTiele);
        cv.put(Constants.TITLE, chapterTitle);
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.COMMENT, comment);
        cv.put(Constants.DATE, datFormate);
        return cv;
    }

    /*public static String toJson(List<Comment> list){
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (list == null) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
            for (Comment it : list) {
                if (it != null) {
                    arr.add(it.toJson());
                }
            }
            object.put(Constants.COMMENTS, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }*/
}
