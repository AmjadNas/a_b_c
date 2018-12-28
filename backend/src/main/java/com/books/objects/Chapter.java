package com.books.objects;

import com.books.utils.Constants;
import com.books.utils.Convector;
import com.books.utils.ToJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class Chapter extends ToJSON {
    private String storyTitle;
    private String title;
    private List<String> lines;

    public Chapter(String storyTitle, String title) {
        this.storyTitle = storyTitle;
        this.title = title;
    }

    public String getId(){
        return storyTitle + " " + title;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public int getWordCount(){
        int i = 0;

        if (lines != null)
            for (String line : lines)
                for (String word : line.split(" "))
                    i++;

        return i;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject cv = new JSONObject();
        cv.put(Constants.STORY_TITLE, storyTitle);
        cv.put(Constants.TITLE, title);
        cv.put(Constants.LINES, Convector.combineLines(lines));
        return cv;
    }

    /*public static String toJson(List<Chapter> list){
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (list == null) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
            for (Chapter it : list) {
                if (it != null) {
                    arr.add(it.toJson());
                }
            }
            object.put(Constants.CHAPTERS, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(storyTitle, chapter.storyTitle) &&
                Objects.equals(title, chapter.title);
    }

    @Override
    public int hashCode() {

        return Objects.hash(storyTitle, title);
    }
}
