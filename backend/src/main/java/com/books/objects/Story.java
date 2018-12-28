package com.books.objects;

import com.books.utils.Constants;
import com.books.utils.Convector;
import com.books.utils.DateUtil;
import com.books.utils.ToJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Story extends ToJSON {
    private String title;
    private String author;
    private String language;
    private String rating;
    private String synopsis;
    private boolean status;
    private List<String> categories;
    private List<String> characters;;
    private List<String> genres;
    private List<String> comments;
    private List<Chapter> chapters;
    private List<String> tags;
    private int likes;
    private int wordCount;
    private int chapterCount;
    private long dateFormat;
    private byte[] Image;

    public Story(String title){
        this.title = title;
    }

    public Story(String title, String author, String synopsis, String rating, String language) {
        this.title = title;
        this.author = author;
        this.synopsis = synopsis;
        this.language = language;
        categories = new ArrayList<>();
        comments = new ArrayList<>();
        chapters = new ArrayList<>();
        characters = new ArrayList<>();
        tags = new ArrayList<>();
        likes = 0;
        status = false;
    }

    public Story(String title, String author, String synopsis, String rating, String language, int likes, boolean status) {
        this.title = title;
        this.author = author;
        this.synopsis = synopsis;
        this.rating = rating;
        this.language = language;
        categories = new ArrayList<>();
        comments = new ArrayList<>();
        chapters = new ArrayList<>();
        tags = new ArrayList<>();
        characters = new ArrayList<>(1);
        this.likes = likes;
        this.status = status;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public byte[] getImage() {
        return Image;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public List<String> getCharacters() {
        return characters;
    }

    public void setCharacters(List<String> characters) {
        this.characters = characters;
    }

    public boolean addCategory(String c){
        if (!categories.contains(c))
            return categories.add(c);
        return false;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getGenreString() {
        return Convector.fromListToString(genres);
    }

    public String getCategoryString(){
        return Convector.fromListToString(categories);
    }

    public boolean AddChapter(Chapter c){
        if (!chapters.contains(c))
            return chapters.add(c);
        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status ? Constants.COMPLETE : Constants.IN_PROGRESS;
    }

    public boolean getStatusBool() {
        return status;
    }

    public long getDateFormat() {
        return dateFormat;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setStatus(int status) {
        if (status == 1)
            this.status = true;
        else if (status == 0)
            this.status = false;

    }
    public int getChapterCount() {
        return chapters.size();
    }

    public int getLength() {
        int i = 0;
        for (Chapter c :chapters)
            i += c.getWordCount();
        return i;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public int getCommentsCount(){
        return comments.size();
    }

    public void increaseLikes(){
        likes++;
    }

    public void decreaseLikes(){
        if (likes > 0)
            likes--;
    }

    public int getLikes() {
        return likes;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();

        object.put(Constants.TITLE, title);
        object.put(Constants.AUTHOR, author);
        object.put(Constants.LANGUAGE, language);
        object.put(Constants.SYNOPSIS, synopsis);
        object.put(Constants.STATUS, status);
        object.put(Constants.LIKES, likes);
        object.put(Constants.DATE, DateUtil.getCurrentDate(dateFormat));
        object.put(Constants.RATING, rating);
        object.put(Constants.TAGS, tags);
        object.put(Constants.CATEGORIES, categories);
        object.put(Constants.GENRES, genres);
        object.put(Constants.CHARACTERS, characters);
        object.put(Constants.WORDCOUNT, wordCount);
        object.put(Constants.CHAPTERCOUNT, chapterCount);

        return object;
    }

    /*public static String toJson(List<Story> list){
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (list == null) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
			for (Story it : list) {
				if (it != null) {
					arr.add(it.toJson());
				}
			}
            object.put(Constants.STORIES, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Story story = (Story) o;
        return Objects.equals(title, story.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    public void setDate(String string) {
        dateFormat = DateUtil.getCurrentDate(string);
    }

    public void setDateFormat(long dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setChapters(List<Chapter> chaptersForStory) {
        chapters = chaptersForStory;
    }

    public boolean removeChapter(String cTitle) {
        if (cTitle != null) {
            Chapter c = new Chapter(title, cTitle);
            return chapters.remove(c);
        }
        return false;
    }
}
