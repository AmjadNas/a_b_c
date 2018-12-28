package android.course.a_b_c.Objects;

import android.content.ContentValues;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.Converter;
import android.course.a_b_c.Utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Story implements Comparable<Story> {

    private String title;
    private String author;
    private String language;
    private String synopsis;
    private String rated;
    private boolean status;
    private HashSet<String> categories;
    private HashSet<String> characters;;
    private HashSet<String> genres;
    private HashSet<String> tags;
    private int likes;
    private int wordCount;
    private int chapterCount;
    private long dateFormat;

    public Story(String title){
        this.title = title;
    }

    public Story(String title, String author, String synopsis, String language, String rated) {
        this.title = title;
        this.author = author;
        this.synopsis = synopsis;
        this.language = language;
        this.rated = rated;
        categories = new HashSet<>();
        characters = new HashSet<>(1);
        tags = new HashSet<>();
        likes = 0;
        status = false;
        dateFormat = System.currentTimeMillis();
    }

    public Story(String title, String author, String synopsis, String language, String rated, int likes, boolean status) {
        this.title = title;
        this.author = author;
        this.synopsis = synopsis;
        this.language = language;
        this.rated = rated;
        categories = new HashSet<>();
        tags = new HashSet<>();
        characters = new HashSet<>(1);
        this.likes = likes;
        this.status = status;
        dateFormat = System.currentTimeMillis();
    }

    public static Story fromJson(JSONObject jsonObject) {
        try{
            JSONArray arr;

            Story s = new Story(jsonObject.getString(Constants.TITLE),
                    jsonObject.getString(Constants.AUTHOR), jsonObject.getString(Constants.SYNOPSIS),
                    jsonObject.getString(Constants.LANGUAGE),
                    jsonObject.getString(Constants.RATING),
                    jsonObject.getInt(Constants.LIKES),
                    jsonObject.getBoolean(Constants.STATUS));

            s.setDate(jsonObject.getString(Constants.DATE));

            HashSet<String> list = new HashSet<>();
            arr = jsonObject.getJSONArray(Constants.TAGS);
            for (int i = 0; i < arr.length(); i++){
                list.add(arr.getString(i));
            }
            s.setTags(list);

            HashSet<String> cates = new HashSet<>();
            arr = jsonObject.getJSONArray(Constants.CATEGORIES);
            for (int i = 0; i < arr.length(); i++){
                cates.add(arr.getString(i));
            }
            s.setCategories(cates);

            HashSet<String> genres = new HashSet<>();
            arr = jsonObject.getJSONArray(Constants.GENRES);
            for (int i = 0; i < arr.length(); i++){
                genres.add(arr.getString(i));
            }
            s.setGenres(genres);
            HashSet<String> chars = new HashSet<>();
            arr = jsonObject.getJSONArray(Constants.CHARACTERS);
            for (int i = 0; i < arr.length(); i++){
                chars.add(arr.getString(i));
            }
            s.setCharacters(chars);

            s.setWordCount(jsonObject.getInt(Constants.WORDCOUNT));
            s.setChapterCount(jsonObject.getInt(Constants.CHAPTERCOUNT));
            return s;


        }catch (Exception e){
            return null;
        }
    }

    public void setChapterCount(int anInt) {
        chapterCount = anInt;
    }

    public void setWordCount(int anInt) {
        wordCount = anInt;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public HashSet<String> getCharacters() {
        return characters;
    }

    public String getCharactersString() {
        return Converter.fromListToString(characters);
    }

    public void setCharacters(HashSet<String> characters) {
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

    public HashSet<String> getTags() {
        return tags;
    }

    public String getTagsString() {
        return Converter.fromListToString(tags);
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public HashSet<String> getCategories() {
        return categories;
    }

    public void setCategories(HashSet<String> categories) {
        this.categories = categories;
    }

    public void setGenres(HashSet<String> genres) {
        this.genres = genres;
    }

    public HashSet<String> getGenres() {
        return genres;
    }

    public String getGenreString() {
        return Converter.fromListToString(genres);
    }

    public String getCategoryString(){
        return Converter.fromListToString(categories);
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

    public String getStatusString() {
        return status ? Constants.COMPLETE : Constants.IN_PROGRESS;
    }

    public boolean isStatus() {
        return status;
    }

    public String getCompletedStatues() {
        return status ? "Yes" : "No";
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

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE, title);
        cv.put(Constants.AUTHOR, author);
        cv.put(Constants.LANGUAGE, language);
        cv.put(Constants.SYNOPSIS, synopsis);
        cv.put(Constants.RATING, rated);
        cv.put(Constants.STATUS, status);
        cv.put(Constants.LIKES, likes);
        cv.put(Constants.DATE, DateUtil.getCurrentDate(dateFormat));
        return cv;
    }

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

    public long getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(long dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDate() {
        return DateUtil.getCurrentDate(dateFormat);
    }

    public String getRated() {
        return rated;
    }


    public void setRated(String txtRate) {
        rated = txtRate;
    }

    public void copy(Story story) {
        setSynopsis(story.synopsis);
        setLanguage(story.language);
        setRated(story.rated);
        setCategories(story.categories);
        setGenres(story.genres);
        setTags(story.tags);
        setCharacters(story.characters);
        setStatus(story.status);
    }

    @Override
    public int compareTo(Story o) {
        if (dateFormat > o.dateFormat)
            return -1;
        else if (dateFormat < o.dateFormat)
            return 1;
        else
            return 0;
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public void addCharacter(String c) {
        characters.add(c);
    }

    public void addTag(String c) {
        tags.add(c);
    }

    public int getWrodCount() {
        return wordCount;
    }

    public void setLikes(int i) {
        likes = i;
    }
}
