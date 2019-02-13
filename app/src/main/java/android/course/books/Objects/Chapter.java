package android.course.books.Objects;

import android.content.ContentValues;
import android.course.books.Utils.Constants;
import android.course.books.Utils.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chapter {

    private String storyTitle;
    private String title;
    private List<String> lines;
    private List<Comment> comments;

    public Chapter(String storyTitle, String title) {
        this.storyTitle = storyTitle;
        this.title = title;
        comments = new ArrayList<>();
    }

    public static Chapter fromJson(JSONObject jsonObject) {
        try {
            String storyTitle = jsonObject.getString(Constants.STORY_TITLE);
            String title = jsonObject.getString(Constants.STORY_TITLE);
            String lines = jsonObject.getString(Constants.LINES);
            List<String> ln = Utilities.fromStringToLines(lines);

            Chapter c = new Chapter(storyTitle, title);
            c.setLines(ln);

            return c;

        }catch (Exception e){

        }
        return null;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(Constants.STORY_TITLE, storyTitle);
        cv.put(Constants.TITLE, title);
        cv.put(Constants.LINES, Utilities.combineLines(lines));
        return cv;
    }

    public ContentValues getContentValuesForUpdate(){
        ContentValues cv = new ContentValues();
        cv.put(Constants.LINES, Utilities.combineLines(lines));
        return cv;
    }

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

    public int getCommentcount() {
        return comments == null ? 0 : comments.size();
    }

    public String getLinesString() {
        return Utilities.combineLines(lines);
    }

    public boolean addComment(Comment comment) {
         comments.add(0,comment);
         return true;
    }
}
