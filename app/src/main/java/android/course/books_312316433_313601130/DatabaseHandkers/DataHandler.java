package android.course.books_312316433_313601130.DatabaseHandkers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.course.books_312316433_313601130.Adapters.ActivitiesAdapter;
import android.course.books_312316433_313601130.Adapters.ChapterAdpater;
import android.course.books_312316433_313601130.Adapters.CommentsAdapter;
import android.course.books_312316433_313601130.Adapters.MessagesAdapter;
import android.course.books_312316433_313601130.Adapters.StoriesAdapter;
import android.course.books_312316433_313601130.Network.NetworkConnector;
import android.course.books_312316433_313601130.Network.NetworkResListener;
import android.course.books_312316433_313601130.Network.ResStatus;
import android.course.books_312316433_313601130.Objects.ActivityEvent;
import android.course.books_312316433_313601130.Objects.Chapter;
import android.course.books_312316433_313601130.Objects.Comment;
import android.course.books_312316433_313601130.Objects.Message;
import android.course.books_312316433_313601130.Objects.Reply;
import android.course.books_312316433_313601130.Objects.Story;
import android.course.books_312316433_313601130.Objects.User;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Check;
import android.course.books_312316433_313601130.Utils.Constants;
import android.course.books_312316433_313601130.Utils.Utilities;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class DataHandler implements NetworkResListener {

    private static DataHandler instance;
    private User user;
    private MyDatabase db;
    private Context context;
    private ArrayList<User> currentUsers;

    private DataHandler(){}

    public static DataHandler getInstance() {
        synchronized (DataHandler.class) {
            if (instance == null)
                instance = new DataHandler();
            return instance;
        }
    }

    /**
     * initializes the SQLite database object
     * @param context
     */
    public void initDataBase(Context context){
        db = new MyDatabase(context.getApplicationContext());
        this.context = context;
        db.open();
    }

    /**
     * method that closes the database
     */
    public void closeDataBase(){
        if (db != null)
            db.close();

        db = null;
    }

    /**
     * init user
     * @return
     */
    public User initUser(){
        SharedPreferences preferences = context.getSharedPreferences(R.string.app_name+"prefs", Context.MODE_PRIVATE);
        String username = preferences.getString(Constants.USERNAME,"--");
        return initUser(username);
    }

    /**
     * save username for automatic login
     * @param username
     */
    private void saveUserPrefs(String username) {
        SharedPreferences preferences = context.getSharedPreferences(R.string.app_name+"prefs", Context.MODE_PRIVATE);
        preferences.edit().putString(Constants.USERNAME, username)
                .apply();
    }

    /**
     *
     * initialize user, to be used in log in activity when created.
     * @param username user's entered username
     * @return user object if user logged in, if not return null, which indicates that user doesn't exist in database
     */
    public User initUser(String username) {
        user = db.getUser(username);  // load user from database
        if(user != null){ // user logged in, load content, else user doesn't exist in database and didn't log in
            saveUserPrefs(username);
            currentUsers = new ArrayList<>();
        }
        return user;
    }

    /**
     * get user that is other than current user in session
     * @param username
     * @return
     */
    public User getUser(String username){
        User u;
        if(user.equals(new User(username)))
            return user;

        if(!currentUsers.contains(new User(username))){
            u = db.getUser(username);
            currentUsers.add(u);
        }else
            u = currentUsers.get(currentUsers.indexOf(new User(username)));

        return u;
    }

    /**
     * method that adds the story to the database and sends requests to the server
     * @param txtTitles
     * @param txtSynopsis
     * @param txtGenre
     * @param txtcateGories
     * @param language
     * @param rating
     * @param image
     * @param tags
     * @param tvCharacters
     * @param isfinished
     * @return if the story was added successfully
     */
    public boolean addStory(String txtTitles, String txtSynopsis, String txtGenre, String txtcateGories,
                            String language, String rating, Bitmap image, String[] tags, String[] tvCharacters,
                            boolean isfinished){
        Story story = new Story(txtTitles, user.getUsername(),  txtSynopsis, language, rating,  0, isfinished);
        String[] cate = txtcateGories.split(", ");
        String[] genres = txtGenre.split(", ");

        // converting tags, genres and categories to ArrayLists

        story.setCategories(new HashSet<>(Arrays.asList(cate)));
        story.setGenres(new HashSet<>(Arrays.asList(genres)));
        story.setTags(new HashSet<>(Arrays.asList(tags)));
        story.setCharacters(new HashSet<>(Arrays.asList(tvCharacters)));

            // if story inserted successfully insert it's relevant items
        if(db.insert(Constants.STORY, story.getContentValues())) {
            if (image != null)
                insertImage(txtTitles, image);
            updateStriesStuff(txtTitles, story.getTags(), story.getGenres(), story.getCategories(), story.getCharacters());
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ITEM_REQ, story, this);
            return true;
        }
        return false;
    }

    /**
     * helper method is used to add stories to the database from the internet
     * @param obj
     * @param currReads
     * @param faves
     */
    private void addStory(Story obj, boolean currReads, boolean faves) {
        if (!db.insert(Constants.STORY, obj.getContentValues()))
            db.update(Constants.STORY, obj.getContentValues(), Constants.TITLE + "=?", obj.getTitle());

        updateStriesStuff(obj.getTitle(), obj.getTags(),
                obj.getGenres(),
                obj.getCategories(), obj.getCharacters());

        if (faves)
            LikeStory(obj.getTitle(), false);
        if (currReads)
            addToCurrentReading(obj.getTitle(), false);

    }

    /**
     * helper method that inserts the image to the database
     * @param title
     * @param image
     */
    private void insertImage(String title, Bitmap image){
        ContentValues cv = new ContentValues();
        byte[] arr = Utilities.getBitmapAsByteArray(image);
        cv.put(Constants.IMAGE, arr);
        db.update(Constants.STORY, cv, Constants.TITLE + "=?", title);
    }

    /**
     * helper method that inserts story related stuff to the database, like tags, categories
     * @param table
     * @param title
     * @param col
     * @param items
     */
    private void insertItems(String table, String title, String col, HashSet<String> items){
        ContentValues cv;
        db.delete(table, Constants.STORY_TITLE +"=?", title);
        for (String s : items){
            cv = new ContentValues();
            cv.put(Constants.STORY_TITLE, title);
            cv.put(col, s);
            db.insert(table, cv);
        }
    }

    /**
     * removes a story from the database
     * @param title
     * @return if the story was removed successfully
     */
    public boolean removeStory(String title){
        if (title != null) {
            db.delete(Constants.READING_STORIES, Constants.STORY_TITLE + "=?", title);
            db.delete(Constants.FAVOURITE_STORIES, Constants.STORY_TITLE + "=?", title);
            db.delete(Constants.TAGS, Constants.STORY_TITLE + "=?", title);
            db.delete(Constants.GENRES, Constants.STORY_TITLE + "=?", title);
            db.delete(Constants.CATEGORIES, Constants.STORY_TITLE + "=?", title);
            db.delete(Constants.COMMENTS, Constants.STORY_TITLE + "=?", title);
            db.delete(Constants.CHAPTER, Constants.STORY_TITLE + "=?", title);
            db.delete(Constants.CHARACTERS, Constants.STORY_TITLE + "=?", title);

            if (db.delete(Constants.STORY, Constants.TITLE + "=?", title)) {
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.DELETE_ITEM_REQ, new Story(title), this);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param title
     * @return story from the database
     */
    public Story getStoryByTitle(String title){
        return db.getStoryByID(title);
    }

    /**
     *
     * @param table
     * @param key
     * @param col
     * @param value
     * @param orderBy
     * @param like if the term like to be used
     * @return list of stories dependant of the filters above
     */
    public ArrayList<Story> getStoriesBy(String table, String key, String col, String value, String orderBy, boolean like){
        if (like) {
            key += " LIKE ?";
            value = "%" + value + "%";
        }
        else
            key += "=?";
        List<String> ids = db.getStringsStuff(table, key, col, orderBy, value);
        ArrayList<Story> list = new ArrayList<>();
        for (String id : ids){
            list.add(db.getStoryByID(id));
        }
        return list;
    }

    /**
     *  overloaded method from getStoriesBy used in StoriesByActivity
     * @param table
     * @param where
     * @param value
     * @param col
     * @return list of stories dependant of the filters above
     */
    public ArrayList<Story> getStoriesBy(String table, String where, String value, String col){
        ArrayList<Story> list = new ArrayList<>();
        for (Story s : db.getAllStories()){
            if (table.equals(Constants.CATEGORIES)) {
                if (s.getCategories().contains(value))
                    list.add(s);
            }else if (table.equals(Constants.GENRES))
                if (s.getGenres().contains(value))
                    list.add(s);

        }
        return list;
    }

    /**
     * sends a message to the user
     * @param reciever
     * @param subject
     * @param message
     * @return if message was stored in database and sent
     */
    public boolean sendMessage(String reciever, String subject, String message) {
        if (!reciever.equals(user.getUsername())) {
            Message msg = new Message(user.getUsername(), reciever, subject, message);
            if (db.insert(Constants.MESSAGES, msg.getContentValue())) {
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_MESSAGE_REQ, msg, this);
                return true;
            }
        }
        return false;
    }

    /**
     * used for adding messages in the database from the internet
     * @param msg
     * @return if the message was added
     */
    public boolean sendMessage(Message msg){
        return db.insert(Constants.MESSAGES, msg.getContentValue());
    }

    /**
     *
     * @param username
     * @return if the following process was successful
     */
    public boolean followUser(String username){
        if (!username.equals(user.getUsername())) {
            ContentValues cv = new ContentValues();
            cv.put(Constants.USERNAME, username);
            cv.put(Constants.FOLLOWER, user.getUsername());
            if (db.insert(Constants.FOLLOWING, cv)) {
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.FOLLOW_USER, username, user.getUsername(), this);
                return true;
            }
        }
        return false;
    }

    /**
     * \
     * @param username
     * @return if the unFollowing process was successful
     */
    public boolean unFollowUser(String username){
        if (!username.equals(user.getUsername())) {
            if (db.delete(Constants.FOLLOWERS, Constants.USERNAME + " =? AND " + Constants.FOLLOWER + "=?", username, user.getUsername())) {
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.UNFOLLOW_USER, username, user.getUsername(), this);
                getUser(username).removeFfollower();
                return true;
            }
        }
        return false;
    }

    /**
     * posts an activity event to the user's feed
     * @param message
     * @return if the activity was stored successfully
     */
    public long postActivity(String message){
        ActivityEvent activityEvent = new ActivityEvent(user.getUsername(), message);
        long id = activityEvent.getId();
        if (db.insert(Constants.ACTIVITIES, activityEvent.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ACTIVITY_REQ, activityEvent, this);
            return id;
        }
        return -1;
    }

    /**
     * inserts the activity events from the internet into the database
     * @param ae
     * @return if the addition was successful
     */
    public boolean postActivity(ActivityEvent ae){
        return db.insert(Constants.ACTIVITIES, ae.getContentValues());
    }

    /**
     *
     * @param id
     * @return if the removal was successful
     */
    public boolean removetActivity(String id){
        if (db.delete(Constants.REPLIES, Constants.ID + "=?", id))
            return db.delete(Constants.ACTIVITIES, Constants.ID + "=?", id);
        return false;
    }

    /**
     *
     * @param str
     * @param txtTitle
     * @param txtContent
     * @return if adding the chapters to the database successfully
     */
    public boolean addChapterToStory(String str, String txtTitle, String txtContent){
        String[] ln = txtContent.split("\n");

        Chapter c = new Chapter(str, txtTitle);
        c.setLines(Arrays.asList(ln));
        Story s = getStoryByTitle(str);

        if (s != null && db.insert(Constants.CHAPTER, c.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_CHAPTER_REQ, c, txtContent, this);
            return true;
        }

        return false;
    }

    /**
     *  add the chapter to the database from the internet
     * @param c
     * @return if adding the chapters to the database successfully
     */
    private boolean addChapterToStory(Chapter c){
        return db.insert(Constants.CHAPTER, c.getContentValues());
    }

    /**
     * updates the chapter
     * @param str
     * @param txtTitle
     * @param txtContent
     * @return if the update was successful
     */
    public boolean updateChapter(String str, String txtTitle, String txtContent){
        String[] ln = txtContent.split("\n");
        Chapter c = new Chapter(str, txtTitle);
        c.setLines(Arrays.asList(ln));

        Story s = getStoryByTitle(str);

        if (s != null && db.update(Constants.CHAPTER, c.getContentValuesForUpdate(),
                Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?", str, txtTitle)){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_CHAPTER_REQ, c, txtContent, this);
            return true;
        }

        return false;
    }

    /**
     *
     * @param str story title
     * @param txtTitle
     * @return if the removal was successful
     */
    public boolean removeChapter(String str, String txtTitle){
        Story s = getStoryByTitle(str);
        if (s == null)
            return false;
         db.delete(Constants.COMMENTS, Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?", str, txtTitle);
        if (db.delete(Constants.CHAPTER, Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?", str, txtTitle)){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.DELETE_CHAPTER_REQ, new Chapter(str, txtTitle), null, this);
            return true;
        }

        return false;
    }

    /**
     * updates the story details in the database
     * @param txtTitles
     * @param txtSynopsis
     * @param txtGenre
     * @param txtcateGories
     * @param txtLanguage
     * @param txtRate
     * @param image
     * @param tags
     * @param chars
     * @param isfinished
     * @return if the update was successful
     */
    public boolean updateStory(String txtTitles, String txtSynopsis, String txtGenre,
                               String txtcateGories, String txtLanguage, String txtRate, Bitmap image, String[] tags, String[] chars, boolean isfinished){

        String[] cate = txtcateGories.split(", ");
        String[] genres = txtGenre.split(", ");
        Story story = new Story(txtTitles, user.getUsername(),  txtSynopsis, txtLanguage, txtRate,  0, isfinished);

        story.setCategories(new HashSet<>(Arrays.asList(cate)));
        story.setGenres(new HashSet<>(Arrays.asList(genres)));
        story.setTags(new HashSet<>(Arrays.asList(tags)));
        story.setCharacters(new HashSet<>(Arrays.asList(chars)));

        if (db.update(Constants.STORY, story.getContentValues(), Constants.TITLE + "=?", txtTitles)){
            if (image != null)
                insertImage(txtTitles, image);
            updateStriesStuff(txtTitles, story.getTags(), story.getGenres(), story.getCategories(), story.getCharacters());
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ITEM_REQ, story, this);
            return true;
        }
        return false;
    }

    public User getUser() {
        return user;
    }

    /**
     * helper method updates the relevant stuff for a story
     * @param txtTitles
     * @param tags
     * @param genres
     * @param cate
     * @param chars
     */
    private void updateStriesStuff(String txtTitles, HashSet<String> tags, HashSet<String> genres, HashSet<String> cate, HashSet<String> chars){

        insertItems(Constants.TAGS, txtTitles, Constants.TAG, tags);
        insertItems(Constants.GENRES, txtTitles, Constants.GENRE, genres);
        insertItems(Constants.CATEGORIES, txtTitles, Constants.CATEGORY, cate);
        insertItems(Constants.CHARACTERS, txtTitles, Constants.NAME, chars);
    }

    /**
     * saves the changes to user's details
     * @param uFname
     * @param uLname
     * @param uEmail
     * @param img
     * @return if the updating the user's details was successful
     */
    public boolean saveProfChanges(String uFname, String uLname, String uEmail, Bitmap img) {
        ContentValues cv;
        final String where = Constants.USERNAME + "=  ?";

        if(Check.isName(uFname) && Check.isName(uLname)) {

            cv = new User(user.getUsername(), uFname, uLname, uEmail, img).getContent();

            if (db.update(Constants.USER, cv, where, user.getUsername())) {
                user.setFirstName(uFname);
                user.setLastName(uLname);
                user.setEmail(uEmail);
                user.setThumb(img);
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_USER_IMAGE_REQ, user, this);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return all stories from database
     */
    public List<Story> getStories() {
        return db.getAllStories();
    }

    /**
     *
     * @param username
     * @return all stories that are in the current reading for a user
     */
    public List<Story> getCurrentReading(String username){
        List<Story> list = new ArrayList<>();
        for (String s : db.getStringsStuff(Constants.READING_STORIES, Constants.USERNAME + "=?",
                Constants.STORY_TITLE, null, username))
            list.add(db.getStoryByID(s));
        return list;
    }

    /**
     *
     * @param username
     * @return if the user in session is following some user
     */
    public boolean isFollowing(String username) {
        if (db.getUserFolloers(username).contains(user.getUsername()))
            return true;
        return false;
    }

    /**
     *
     * @param title
     * @return story's image
     */
    public Bitmap getStoryCover(String title) {
        if (title == null)
            return null;
        else {
            return db.getImage(Constants.STORY, Constants.TITLE, title);
        }
    }

    /**
     *
     * @param title
     * @return story's thumbnail
     */
    public Bitmap getStoryThumbnail(String title) {
        if (title == null)
            return null;
        else {
            return Utilities.cropThumbnail(db.getImage(Constants.STORY, Constants.TITLE, title));
        }
    }

    /**
     *
     * @param storyTitle
     * @param title
     * @return a specific chapter in a story
     */
    public Chapter getChapter(String storyTitle, String title) {
        return db.getChapterInStory(storyTitle, title);
    }

    /**
     *
     * @param storyTitle
     * @param title
     * @return list comments for a chapter
     */
    public List<Comment> getChapterComments(String storyTitle, String title) {
        List<Comment> comments = new ArrayList<>();
        Chapter c = db.getChapterInStory(storyTitle, title);
        if (c != null)
            return c.getComments();
        return comments;
    }

    /**
     *
     * @param username
     * @return list of stories posted by a user
     */
    public List<Story> getUserStories(String username) {
        List<Story> list = new ArrayList<>();
        for(Story sr : db.getAllStories()){
            if (sr.getAuthor().equals(username))
                list.add(sr);
        }
        return list;
    }

    /**
     *
     * @return list of newest stories
     */
    public List<Story> getNewsetStories(){
        TreeSet<Story> newst = new TreeSet<>();
        int i = 0;
        for (Story s : db.getAllStories()) {
            newst.add(s);
            i++;
            if (i == 3)
                break;;
        }
        return new ArrayList<>(newst);
    }

    /**
     *
     * @param sTitle
     * @return chapters list for a story
     */
    public List<Chapter> getChaptersBytStoryTitle(String sTitle) {
        if (sTitle != null){
            return db.getChaptersForStory(sTitle);
        }
        return null;
    }

    /**
     *
     * @param username
     * @return user's image
     */
    public Bitmap getUserImage(String username) {
        return db.getImage(Constants.USER, Constants.USERNAME, username);

    }

    /**
     *
     * @param id
     * @return replies that was posted on an activity event
     */
    public List<Reply> getActivityRepliesByID(long id) {
        return db.getReplies(id);
    }

    /**
     *
     * @param id
     * @return an activity event by it's ID
     */
    public ActivityEvent getActivityByID(long id){
        return db.getActivity(String.valueOf(id));
    }

    /**
     * parses the json for chapters list
     * @param adapter
     * @param list
     * @param res
     */
    public void parseChapters(ChapterAdpater adapter, List<Chapter> list, JSONObject res) {

        try {
            JSONArray arr = res.getJSONArray(Constants.CHAPTERS);
            Chapter obj;
            for (int i = 0; i < arr.length(); i++){
                obj = Chapter.fromJson(arr.getJSONObject(i));

                if (obj != null){
                    list.add(obj);

                    final String ttl = obj.getTitle();
                    if (!addChapterToStory(obj))
                        updateChapter(obj.getStoryTitle(), obj.getTitle(), obj.getLinesString());
                    adapter.notifyItemInserted(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * parses the json for stories list
     * @param adapter
     * @param list
     * @param currReads
     * @param faves
     * @param res
     */
    public void parseStories(final StoriesAdapter adapter, List<Story> list, boolean currReads, boolean faves, JSONObject res) {
        try {
            JSONArray arr = res.getJSONArray(Constants.STORIES);
            Story obj;
            for (int i = 0; i < arr.length(); i++){
                obj = Story.fromJson(arr.getJSONObject(i));

                if (obj != null){
                    list.add(obj);
                    final String ttl = obj.getTitle();
                    addStory(obj,  currReads,  faves);
                    NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_ITEM_IMAGE_REQ, obj, new NetworkResListener() {
                        @Override
                        public void onPreUpdate() {

                        }

                        @Override
                        public void onPostUpdate(JSONObject res, String table, ResStatus status) {

                        }

                        @Override
                        public void onPostUpdate(Bitmap res, ResStatus status) {
                            if (status == ResStatus.SUCCESS){
                                if (res != null){
                                    insertImage(ttl,res);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }
                    });
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * parses the json messages list
     * @param adapter
     * @param list
     * @param res
     */
    public void parseMessages(MessagesAdapter adapter, List<Message> list, JSONObject res) {
        try {
            JSONArray arr = res.getJSONArray(Constants.MESSAGES);
            Message obj;
            for (int i = 0; i < arr.length(); i++){
                obj = Message.fromJson(arr.getJSONObject(i));

                if (obj != null){
                    list.add(obj);
                    sendMessage(obj);
                    adapter.notifyItemInserted(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return list of activity events from database
     */
    public List<ActivityEvent> getActivities() {
        return db.getActivitiesForUser(user.getUsername());
    }

    /**
     * parses the json for activity events
     * @param adapter
     * @param list
     * @param res
     */
    public void parseActivities(ActivitiesAdapter adapter, List<ActivityEvent> list, JSONObject res) {
        try {
            JSONArray arr = res.getJSONArray(Constants.ACTIVITIES);
            ActivityEvent obj;
            for (int i = 0; i < arr.length(); i++){
                obj = ActivityEvent.fromJson(arr.getJSONObject(i));

                if (obj != null){
                    list.add(obj);
                    postActivity(obj);
                    adapter.notifyItemInserted(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * parses the json for a chapters comments
     * @param adapter
     * @param list
     * @param res
     */
    public void parseCommentss(CommentsAdapter adapter, List<Comment> list, JSONObject res) {
        try {
            JSONArray arr = res.getJSONArray(Constants.COMMENTS);
            Comment obj;
            for (int i = 0; i < arr.length(); i++){
                obj = Comment.fromJson(arr.getJSONObject(i));

                if (obj != null){
                    list.add(obj);
                    insertComment(obj);
                    adapter.notifyItemInserted(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * helper method that inserts a comment that is downloaded from the internet
     * @param obj
     */
    private void insertComment(Comment obj) {
        db.insert(Constants.COMMENTS, obj.getContentValues());
    }

    /**
     * parses the json for replies
     * @param adapter
     * @param list
     * @param res
     */
    public void parseReplies(CommentsAdapter adapter, List<Reply> list, JSONObject res) {
        try {
            JSONArray arr = res.getJSONArray(Constants.REPLIES);
            Reply obj;
            for (int i = 0; i < arr.length(); i++){
                obj = Reply.fromJson(arr.getJSONObject(i));

                if (obj != null){
                    list.add(obj);
                    insertReply(obj);
                    adapter.notifyItemInserted(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * inserts a reply to database that is downloaded from the internet
     * @param obj
     */
    private void insertReply(Reply obj) {
        db.insert(Constants.REPLIES, obj.getContentValues());
    }

    /**
     * parses the json for the followeings list
     * @param username
     * @param res
     */
    public void parseFolloweingsist(String username, JSONObject res) {
        try {
            JSONArray arr = res.getJSONArray(Constants.FOLLOWING);
            String obj;
            ContentValues cv = new ContentValues();
            for (int i = 0; i < arr.length(); i++){
                obj = arr.getString(i);

                if (obj != null){
                    cv.put(Constants.USERNAME, obj);
                    cv.put(Constants.FOLLOWER, username);
                    db.insert(Constants.FOLLOWING, cv);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * parses the json for the followers list
     * @param username
     * @param res
     */
    public void parseFollowersList(String username, JSONObject res) {

        try {
            JSONArray arr = res.getJSONArray(Constants.FOLLOWERS);
            String obj;
            ContentValues cv = new ContentValues();
            for (int i = 0; i < arr.length(); i++){
                obj = arr.getString(i);

                if (obj != null){
                    cv.put(Constants.USERNAME, username);
                    cv.put(Constants.FOLLOWER, obj);

                    db.insert(Constants.FOLLOWERS, cv);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return list of sent messages for the user in session
     */
    public List<Message> getSentMessages() {
        return db.getMessages(true, user.getUsername());
    }

    /**
     *
     * @return list of received messages for the user in session
     */
    public List<Message> getInboxMessages() {
        return db.getMessages(false, user.getUsername());
    }

    /**
     * adds a story to a user's favourites list
     * @param storyTitle
     * @param upload
     * @return if the addition was successful
     */
    public boolean LikeStory(String storyTitle, boolean upload) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.USERNAME, user.getUsername());
        cv.put(Constants.STORY_TITLE, storyTitle);
        if(db.insert(Constants.FAVOURITE_STORIES, cv)) {
            db.increaseLikes(storyTitle);
            if (upload)
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ITEM_FAVE_REQ, new Story(storyTitle), this);
            return true;
        }

        return false;
    }

    /**
     * removes story from a user's favourites list
     * @param storyTitle
     * @param upload
     * @return if the removal was successful
     */
    public boolean unLikeStory(String storyTitle, boolean upload) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.USERNAME, user.getUsername());
        cv.put(Constants.STORY_TITLE, storyTitle);
        if(db.delete(Constants.FAVOURITE_STORIES, Constants.STORY_TITLE +"=? AND " + Constants.USERNAME + "=?",
                storyTitle, user.getUsername())) {
            db.decreaseLikes(storyTitle);
            if (upload)
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.REMOVE_FAVE_ITEM, new Story(storyTitle), this);
            return true;
        }

        return false;
    }

    /**
     * adds a story to a user's current reading list
     * @param storyTitle
     * @param upload
     * @return if the addition was successful
     */
    public boolean addToCurrentReading(String storyTitle, boolean upload) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.USERNAME, user.getUsername());
        cv.put(Constants.STORY_TITLE, storyTitle);

        if(db.insert(Constants.READING_STORIES, cv)) {
            if (upload)
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.ADD_TO_CURRENT_READ, new Story(storyTitle), this);
            return true;
        }
        return false;
    }

    /**
     * inserts a comment to the database
     * @param stTtitle
     * @param title
     * @param txtComment
     * @return if the insertion was successful
     */
    public boolean insertComment(String stTtitle, String title, String txtComment) {
        Comment comment = new Comment(title, stTtitle, user.getUsername(), txtComment);
        if (db.insert(Constants.COMMENTS, comment.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_COMMENT, comment, this);
            return getChapter(stTtitle, title).addComment(comment);
        }
        return false;
    }

    /**
     * inserts a reply to the database
     * @param id
     * @param txtComment
     * @return if the insertion was successful
     */
    public boolean insertReply(long id, String txtComment) {
        Reply comment = new Reply(id, user.getUsername(), txtComment);
        if (db.insert(Constants.REPLIES, comment.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_REPLY, comment, this);
            return true;
        }
        return false;
    }

    /**
     *
     * @param username
     * @return list of user's favourite stories
     */
    public List<Story> getFaveStories(String username) {
        List<Story> list = new ArrayList<>();

        for (String s : db.getStringsStuff(Constants.FAVOURITE_STORIES, Constants.USERNAME + "=?",
                Constants.STORY_TITLE, null, username)){
            list.add(db.getStoryByID(s));
        }
        return list;
    }

    /**
     *
     * @param storyTitle
     * @return if the user in session likes a specific story
     */
    public boolean userLikes(String storyTitle) {
        List<Story> stories = getFaveStories(user.getUsername());
        if (stories == null || stories.isEmpty())
            return false;
        if (!stories.contains(new Story(storyTitle)))
            return false;
        return true;
    }

    /**
     * helper method used to refresh a given story list
     * @param stories
     * @param s
     * @return the index of the replaced story
     */
    public int replaceStory(List<Story> stories, String s) {
        try {
            int i = stories.indexOf(new Story(s));
            Story story = db.getStoryByID(stories.remove(i).getTitle());
            stories.add(i, story);

            return i;
        }catch (Exception e){
            return -1;
        }
    }

    /**
     *  deletes the message from database and server
     * @param m
     */
    public void deleteMessage(Message m) {
        if(db.delete(Constants.MESSAGES, Constants.SENDER + "=? AND "+ Constants.USERNAME + "=? AND "
                + Constants.SUBJECT + "=? AND " +  Constants.DATE + "=?",
                m.getSender(), m.getUsername(), m.getSubject(), m.getDateString()))
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.DELETE_MESSAGE_REQ, m, this);

    }

    /**
     *
     * @param title
     * @return comment count for a story
     */
    public int getCommentsCountForStory(String title) {
        return db.getTotalCommentCountForStory(title);
    }

    /**
     *
     * @param username
     * @return list of user's followers
     */
    public List<String> getUserFollowers(String username) {
        return db.getUserFolloers(username);

    }

    /**
     *
     * @param username
     * @return list of user's followings
     */
    public List<String> getUserFollowings(String username) {
        return db.getUserFolloeings(username);
    }

    /**
     *
     * @return if the database is closed
     */
    public boolean dbIsClosed() {
        if (db == null)
            return true;
        else
            return false;
    }

    /**
     * clear every thing for log out process
     */
    public void logOut() {

        db.clearDatabase();
        user = null;
        currentUsers = null;
        saveUserPrefs("--");

    }

    @Override
    public void onPreUpdate() {
    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        // indicate weather the request to server was a success
        if (status == ResStatus.SUCCESS){
            Toast.makeText(context.getApplicationContext(), R.string.uploadSuccess, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context.getApplicationContext(), R.string.uploadFailed, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {
    }
}
