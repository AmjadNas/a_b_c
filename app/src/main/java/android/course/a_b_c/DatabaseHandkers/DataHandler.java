package android.course.a_b_c.DatabaseHandkers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.course.a_b_c.Adapters.ActivitiesAdapter;
import android.course.a_b_c.Adapters.ChapterAdpater;
import android.course.a_b_c.Adapters.CommentsAdapter;
import android.course.a_b_c.Adapters.FollowersAdapter;
import android.course.a_b_c.Adapters.MessagesAdpater;
import android.course.a_b_c.Adapters.StoriesAdapter;
import android.course.a_b_c.Network.NetworkConnector;
import android.course.a_b_c.Network.NetworkResListener;
import android.course.a_b_c.Network.ResStatus;
import android.course.a_b_c.Objects.ActivityEvent;
import android.course.a_b_c.Objects.Chapter;
import android.course.a_b_c.Objects.Comment;
import android.course.a_b_c.Objects.Message;
import android.course.a_b_c.Objects.Reply;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.Objects.User;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Check;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.Converter;
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
        if (instance == null)
            instance = new DataHandler();
        return instance;
    }

    public void initDataBase(Context context){
        db = new MyDatabase(context.getApplicationContext());
        this.context = context;
        db.open();
    }

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

    private void addStory(Story obj, boolean currReads, boolean faves) {
        db.insert(Constants.STORY, obj.getContentValues());

        updateStriesStuff(obj.getTitle(), obj.getTags(),
                obj.getGenres(),
                obj.getCategories(), obj.getCharacters());

        if (faves)
            LikeStory(obj.getTitle(), false);
        if (currReads)
            addToCurrentReading(obj.getTitle(), false);

    }

    private void insertImage(String title, Bitmap image){
        ContentValues cv = new ContentValues();
        byte[] arr = Converter.getBitmapAsByteArray(image);
        cv.put(Constants.IMAGE, arr);
        db.update(Constants.STORY, cv, Constants.TITLE + "=?", title);
    }

    private void insertItems(String table, String title, String col, HashSet<String> items){
        ContentValues cv;
        for (String s : items){
            cv = new ContentValues();
            cv.put(Constants.STORY_TITLE, title);
            cv.put(col, s);
            db.insert(table, cv);
        }
    }

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

    public Story getStoryByTitle(String title){
        return db.getStoryByID(title);
    }

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

    public boolean removeChapterFromStory(String sTitle, String title){
        Story s = getStoryByTitle(sTitle);
        if (s != null && db.delete(Constants.CHAPTER, Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?", sTitle, title)){
            return s.removeChapter(title);
        }
        return false;
    }

    public boolean sendMessage(String reciever, String subject, String message){

        Message msg = new Message(user.getUsername(), reciever, subject, message);
        if (db.insert(Constants.MESSAGES, msg.getContentValue())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_MESSAGE_REQ, msg, this);
            return true;
        }
        return false;
    }

    public boolean sendMessage(Message msg){
        return db.insert(Constants.MESSAGES, msg.getContentValue());
    }

    public boolean followUser(String username){
        ContentValues cv = new ContentValues();
        cv.put(Constants.USERNAME, username);
        cv.put(Constants.FOLLOWER, user.getUsername());
        if (db.insert(Constants.FOLLOWERS, cv)){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.FOLLOW_USER, username, user.getUsername(), this);
            getUser(username).addfollower();
            return user.addFollowing();
        }
        return false;
    }

    public boolean unFollowUser(String username){
        if (db.delete(Constants.FOLLOWERS, Constants.USERNAME + " =? AND " + Constants.FOLLOWER + "=?", username, user.getUsername())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.UNFOLLOW_USER, username, user.getUsername(), this);
            getUser(username).removeFfollower();
            user.removeFollowing();
            return true;
        }
        return false;
    }

    public long postActivity(String message){
        ActivityEvent activityEvent = new ActivityEvent(user.getUsername(), message);
        long id = activityEvent.getId();
        if (db.insert(Constants.ACTIVITIES, activityEvent.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ACTIVITY_REQ, activityEvent, this);
            return id;
        }
        return -1;
    }

    public boolean postActivity(ActivityEvent ae){
        return db.insert(Constants.ACTIVITIES, ae.getContentValues());
    }

    public boolean removetActivity(String id){
        if (db.delete(Constants.REPLIES, Constants.ID + "=?", id))
            return db.delete(Constants.ACTIVITIES, Constants.ID + "=?", id);
        return false;
    }

    public boolean addChapterToStory(String str, String txtTitle, String txtContent){
        String[] ln = txtContent.split("\n");

        Chapter c = new Chapter(str, txtTitle);
        c.setLines(Arrays.asList(ln));
        Story s = getStoryByTitle(str);

        if (s != null && db.insert(Constants.CHAPTER, c.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_CHAPTER_REQ, c, txtContent, this);
            return s.AddChapter(c);
        }

        return false;
    }

    private void addChapterToStory(Chapter c){
        db.insert(Constants.CHAPTER, c.getContentValues());
    }

    public boolean updateChapter(String str, String txtTitle, String txtContent){
        String[] ln = txtContent.split("\n");
        Chapter c = new Chapter(str, txtTitle);
        c.setLines(Arrays.asList(ln));

        Story s = getStoryByTitle(str);

        if (s != null && db.update(Constants.CHAPTER, c.getContentValuesForUpdate(),
                Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?", str, txtTitle)){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_CHAPTER_REQ, c, txtContent, this);
            return s.updateChapter(c);
        }

        return false;
    }

    public boolean removeChapter(String str, String txtTitle){
        Story s = getStoryByTitle(str);
        if (s == null)
            return false;
         db.delete(Constants.COMMENTS, Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?", str, txtTitle);
        if (db.delete(Constants.CHAPTER, Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?", str, txtTitle)){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.DELETE_CHAPTER_REQ, new Chapter(str, txtTitle), null, this);
            return s.removeChapter(txtTitle);
        }

        return false;
    }

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

    private void updateStriesStuff(String txtTitles, HashSet<String> tags, HashSet<String> genres, HashSet<String> cate, HashSet<String> chars){
        insertItems(Constants.TAGS, txtTitles, Constants.TAG, tags);
        insertItems(Constants.GENRES, txtTitles, Constants.GENRE, genres);
        insertItems(Constants.CATEGORIES, txtTitles, Constants.CATEGORY, cate);
        insertItems(Constants.CHARACTERS, txtTitles, Constants.NAME, chars);
    }

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

    public List<Story> getStories() {
       // stories =
        return db.getAllStories();
    }

    public List<Story> getCurrentReading(String username){
        List<Story> list = new ArrayList<>();
        for (String s : db.getStringsStuff(Constants.READING_STORIES, Constants.USERNAME + "=?",
                Constants.STORY_TITLE, null, username))
            list.add(db.getStoryByID(s));
        return list;
    }

    public boolean isFollowing(String username) {
        return db.isFollowing(user.getUsername(), username);
    }

    public Bitmap getStoryCover(String title) {
        if (title == null)
            return null;
        else {
            return db.getImage(Constants.STORY, Constants.TITLE, title);
        }
    }

    public Chapter getChapter(String storyTitle, String title) {
        return db.getChapterInStory(storyTitle, title);
    }

    public List<Comment> getChapterComments(String storyTitle, String title) {
        List<Comment> comments = new ArrayList<>();
        Chapter c = db.getChapterInStory(storyTitle, title);
        if (c != null)
            return c.getComments();
        return comments;
    }


    public List<Story> getUserStories(String username) {
        List<Story> list = new ArrayList<>();
        for(Story sr : db.getAllStories()){
            if (sr.getAuthor().equals(username))
                list.add(sr);
        }
        return list;
    }

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

    public List<Chapter> getChaptersBytStoryTitle(String sTitle) {
        if (sTitle != null){
            return db.getStoryByID(sTitle).getChapters();
        }
        return null;
    }

    public Bitmap getUserImage(String username) {
        return db.getImage(Constants.USER, Constants.USERNAME, username);

    }

    public List<Reply> getActivityRepliesByID(long id) {
        return db.getReplies(id);
    }

    public ActivityEvent getActivityByID(long id){
        return db.getActivity(String.valueOf(id));
    }

    public void parseChapters(ChapterAdpater adapter, List<Chapter> list, JSONObject res) {

        try {
            JSONArray arr = res.getJSONArray(Constants.CHAPTERS);
            Chapter obj;
            for (int i = 0; i < arr.length(); i++){
                obj = Chapter.fromJson(arr.getJSONObject(i));

                if (obj != null){
                    list.add(obj);

                    final String ttl = obj.getTitle();
                    addChapterToStory(obj);
                    adapter.notifyItemInserted(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

    public void parseMessages(MessagesAdpater adapter, List<Message> list, JSONObject res) {
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

    public List<ActivityEvent> getActivities() {
        return db.getActivitiesForUser(user.getUsername());
    }

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

    private void insertComment(Comment obj) {
        db.insert(Constants.COMMENTS, obj.getContentValues());
    }

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

    private void insertReply(Reply obj) {
        db.insert(Constants.REPLIES, obj.getContentValues());
    }

    public void parseStringList(FollowersAdapter adapter, List<String> list, String tag, JSONObject res) {
        try {
            JSONArray arr = res.getJSONArray(tag);
            String obj;
            ContentValues cv = new ContentValues();
            for (int i = 0; i < arr.length(); i++){
                obj = arr.getString(i);

                if (obj != null){
                    list.add(obj);
                    if (tag.equals(Constants.FOLLOWING)) {
                        cv.put(Constants.USERNAME, obj);
                        cv.put(Constants.FOLLOWER, user.getUsername());
                    }else{
                        cv.put(Constants.USERNAME, user.getUsername());
                        cv.put(Constants.FOLLOWER, obj);
                    }
                    db.insert(Constants.FOLLOWERS, cv);
                    adapter.notifyItemInserted(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> parseStringList (JSONObject res, String followers) {
        List<String> list = new ArrayList<>();
        try {
            JSONArray arr = res.getJSONArray(Constants.FOLLOWERS);
            String obj;
            ContentValues cv = new ContentValues();
            for (int i = 0; i < arr.length(); i++){
                obj = arr.getString(i);

                if (obj != null){
                    list.add(obj);
                    cv.put(Constants.USERNAME, user.getUsername());
                    cv.put(Constants.FOLLOWER, obj);

                    db.insert(Constants.FOLLOWERS, cv);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Message> getSentMessages() {
        return db.getMessages(true, user.getUsername());
    }

    public List<Message> getInboxMessages() {
        return db.getMessages(false, user.getUsername());
    }

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

    public boolean insertComment(String stTtitle, String title, String txtComment) {
        Comment comment = new Comment(stTtitle, title, user.getUsername(), txtComment);
        if (db.insert(Constants.COMMENTS, comment.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_COMMENT, comment, this);
            return getChapter(stTtitle, title).addComment(comment);
        }
        return false;
    }

    public boolean insertReply(long id, String txtComment) {
        Reply comment = new Reply(id, user.getUsername(), txtComment);
        if (db.insert(Constants.REPLIES, comment.getContentValues())){
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_REPLY, comment, this);
            return true;
        }
        return false;
    }

    public List<Story> getFaveStories(String username) {
        List<Story> list = new ArrayList<>();

        for (String s : db.getStringsStuff(Constants.FAVOURITE_STORIES, Constants.USERNAME + "=?",
                Constants.STORY_TITLE, null, username)){
            list.add(db.getStoryByID(s));
        }
        return list;
    }

    public boolean userLikes(String storyTitle) {
        List<Story> stories = getFaveStories(user.getUsername());
        if (stories == null || stories.isEmpty())
            return false;
        if (!stories.contains(new Story(storyTitle)))
            return false;
        return true;
    }

    public int replaceStory(List<Story> stories, String s) {
        int i = stories.indexOf(new Story(s));
        Story story = stories.remove(i);
        stories.add(i, story);

        return i;
    }

    public void deleteMessage(Message m) {
        if(db.delete(Constants.MESSAGES, Constants.SENDER + "=? AND "+ Constants.USERNAME + "=? AND "
                + Constants.SUBJECT + "=? AND " +  Constants.DATE + "=?",
                m.getSender(), m.getUsername(), m.getSubject(), m.getDateString()))
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.DELETE_MESSAGE_REQ, m, this);

    }

    public List<String> getUserFollowers(String username) {
        return db.getStringsStuff(Constants.FOLLOWERS, Constants.USERNAME + "=?", Constants.FOLLOWER, null, username);

    }

    public List<String> getUserFollowings(String username) {
        return db.getStringsStuff(Constants.FOLLOWERS, Constants.FOLLOWER + "=?", Constants.USERNAME, null, username);
    }

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
