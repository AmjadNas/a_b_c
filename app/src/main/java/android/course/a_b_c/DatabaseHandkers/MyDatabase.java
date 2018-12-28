package android.course.a_b_c.DatabaseHandkers;

import android.content.ContentValues;
import android.content.Context;
import android.course.a_b_c.Objects.ActivityEvent;
import android.course.a_b_c.Objects.Chapter;
import android.course.a_b_c.Objects.Comment;
import android.course.a_b_c.Objects.Message;
import android.course.a_b_c.Objects.Reply;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.Objects.User;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.Converter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MyDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = R.string.app_name+".db";
    private static final int DATABASE_VERSION = 1;

    protected MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private SQLiteDatabase db = null;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseOperations.SQL_CREATE_USER_TABLE);

        ContentValues cv = new ContentValues();
        cv.put(Constants.USERNAME,"aNas");
        cv.put(Constants.FNAME,"Amjad");
        cv.put(Constants.LNAME,"Nassar");
        cv.put(Constants.EMAIL,"asdas@dsad.com");
        cv.put(Constants.PASSWORD,"123456");
        db.insert(Constants.USER,null,cv);

        cv.put(Constants.USERNAME,"aYoun");
        cv.put(Constants.FNAME,"Aiman");
        cv.put(Constants.LNAME,"Younis");
        cv.put(Constants.EMAIL,"asdas@dsad.com");
        cv.put(Constants.PASSWORD,"123456");
        db.insert(Constants.USER,null,cv);

        cv.put(Constants.USERNAME,"jSmith");
        cv.put(Constants.FNAME,"John");
        cv.put(Constants.LNAME,"Smith");
        cv.put(Constants.EMAIL,"asdas@dsad.com");
        cv.put(Constants.PASSWORD,"123456");
        db.insert(Constants.USER,null,cv);

        db.execSQL(DataBaseOperations.SQL_CREATE_FOLLOWERS);
        db.execSQL(DataBaseOperations.SQL_CREATE_MESSAGE);
        db.execSQL(DataBaseOperations.SQL_CREATE_ACTIVITY);
        db.execSQL(DataBaseOperations.SQL_CREATE_ACTIVITY_REPLIES);
        db.execSQL(DataBaseOperations.SQL_CREATE_STORIES);
        db.execSQL(DataBaseOperations.SQL_CREATE_CHARACTERS);
        db.execSQL(DataBaseOperations.SQL_CREATE_CHAPTERS);
        db.execSQL(DataBaseOperations.SQL_CREATE_STORIES_CATEGORIES);
        db.execSQL(DataBaseOperations.SQL_CREATE_STORIES_COMMENTS);
        db.execSQL(DataBaseOperations.SQL_CREATE_STORIES_GENRES);
        db.execSQL(DataBaseOperations.SQL_CREATE_STORIES_TAGS);
        db.execSQL(DataBaseOperations.SQL_CREATE_FAVOURITE_STORIES);
        db.execSQL(DataBaseOperations.SQL_CREATE_CURRENT_READ);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DataBaseOperations.SQL_DELETE_FOLLOWERS_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_MESSAGES_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_ACTIVITIES_REPLIES_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_ACTIVITIES_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_FAVOURITE_STORIES_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_CURRENT_READ_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_USERS_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_TAGS_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_GENRES_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_COMMENTS_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_CATEGORIES_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_CHAPTER_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_CHARACTERS_TABLE);
        db.execSQL(DataBaseOperations.SQL_DELETE_STORY_TABLE);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insert(String table, ContentValues cv){
        try {
            long l = db.insert(table,null,cv);
            return l >= 0;

        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public boolean update(String table, ContentValues cv, String where, String...cols){
        try {
            long l = db.update(table,cv ,where, cols);
            return l > 0;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public boolean delete(String table, String where, String...cols){
        try {
            long l = db.delete(table ,where, cols);
            return l > 0;

        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param wArgs what column values
     * @return user from database
     */
    public User getUser(String ... wArgs) {
        try {
            String str;
            String[] args = new String[]{Constants.USERNAME,
                    Constants.FNAME,
                    Constants.LNAME,
                    Constants.EMAIL,
                    Constants.IMAGE,
                    Constants.PASSWORD

            };
            byte[] bt;
            Bitmap bitmp = null;
            User user = null;
            int i;

            str = Constants.USERNAME + " =? ";
            Cursor c = db.query(Constants.USER, args, str, wArgs, null, null, null, null);
            // fill args if found
            if (c.moveToNext()) {
                user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), bitmp);
                if (!c.isNull(4)) {
                    // load image
                    bt = c.getBlob(4);
                    bitmp = Converter.decodeImage(bt);
                    user.setThumb(bitmp);
                }
            }
            if (user == null)
                Log.e("USER", " no user found");

            c.close();

            return user;
        }catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public List<Story> getAllStories(){
        List<Story> stories = new ArrayList<>();
        try {
            Story s;
            String[] args = {
                    Constants.TITLE,
                    Constants.AUTHOR,
                    Constants.SYNOPSIS,
                    Constants.LANGUAGE,
                    Constants.RATING,
                    Constants.LIKES,
                    Constants.STATUS,
                    Constants.DATE
            };
            Cursor c = db.query(Constants.STORY, args, null, null, null, null, null);

            while (c.moveToNext()){
                s = new Story(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3),c.getString(4), c.getInt(5), false);
                s.setStatus(c.getInt(6));
                s.setDate(c.getString(7));

                List<String> tags = getStringsStuff(Constants.TAGS,
                        Constants.STORY_TITLE + "=?", Constants.TAG, null, s.getTitle());
                s.setTags(new HashSet<>(tags));
                tags = getStringsStuff(Constants.CATEGORIES,
                        Constants.STORY_TITLE + "=?", Constants.CATEGORY, null, s.getTitle());
                s.setCategories(new HashSet<>(tags));
                tags = getStringsStuff(Constants.CHARACTERS,
                        Constants.STORY_TITLE + "=?", Constants.NAME, null, s.getTitle());
                s.setCharacters(new HashSet<>(tags));
                tags = getStringsStuff(Constants.GENRES,
                        Constants.STORY_TITLE + "=?", Constants.GENRE, null, s.getTitle());
                s.setGenres(new HashSet<>(tags));

                s.setChapterCount(getChapterCount(s.getTitle()));
                s.setWordCount(getStoryWordCount(s.getTitle()));
                stories.add(s);
            }

            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return stories;
    }

    public int getChapterCount(String ...title){
        int i = 0;
        try {
            String[] args = {
                    Constants.TITLE,
            };
            Cursor c = db.query(Constants.CHAPTER, args, Constants.STORY_TITLE + "=? " , title, null, null, null);

            while (c.moveToNext()){
                i++;
            }

            c.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return i;
    }

    public int getStoryWordCount(String ...title){
        int i = 0;
        try {
            String[] args = {
                    Constants.LINES,
            };
            Cursor c = db.query(Constants.CHAPTER, args, Constants.STORY_TITLE + "=? " , title, null, null, null);

            while (c.moveToNext()){
                for (String s : c.getString(0).split(" "))
                    i++;
            }

            c.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return i;
    }

    public int getTotalCommentCountForStory(String ...title){
        int i = 0;
        try {
            Chapter sc = null;
            String[] args = {
                    Constants.STORY_TITLE
            };
            Cursor c = db.query(Constants.COMMENTS, args, Constants.STORY_TITLE + "=? " , title, null, null, null);

            while (c.moveToNext()){
                i++;
            }

            c.close();

        } catch (Throwable t) {
            t.printStackTrace();

        }
        return i;
    }

    public Chapter getChapterInStory(String ...title){
        try {
            Chapter sc = null;
            String[] args = {
                    Constants.STORY_TITLE,
                    Constants.TITLE,
                    Constants.LINES,
            };
            Cursor c = db.query(Constants.CHAPTER, args, Constants.STORY_TITLE + "=? AND "+
                    Constants.TITLE + "=?" , title, null, null, null);
            String sTitle, titlle;
            if (c.moveToNext()){
                String str = c.getString(2);
                sTitle = c.getString(0);
                titlle = c.getString(1);
                sc = new Chapter(c.getString(0), c.getString(1));
                sc.setLines(Arrays.asList(str.split("\n")));
                List<Comment> comments = getCommentsForChapter(sTitle, titlle);
                sc.setComments(comments);
            }

            c.close();
            return sc;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public List<Comment> getCommentsForChapter(String ...title) {
        List<Comment> comments = new ArrayList<>();
        try {
            Comment sc;
            String[] args = {
                    Constants.STORY_TITLE,
                    Constants.TITLE,
                    Constants.USERNAME,
                    Constants.COMMENT,
                    Constants.DATE
            };
            Cursor c = db.query(Constants.COMMENTS, args, Constants.STORY_TITLE + "=? AND "+
                    Constants.TITLE + "=?", title, null, null, null);

            while (c.moveToNext()){

                sc = new Comment(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3));
                sc.setDate(c.getString(4));
                comments.add(sc);
            }

            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return comments;
    }

    public List<Chapter> getChaptersForStory(String ...title){
        List<Chapter> chapters = new ArrayList<>();
        try {
            Chapter sc;
            String[] args = {
                    Constants.STORY_TITLE,
                    Constants.TITLE,
                    Constants.LINES,
            };
            String sTitle;
            String titlle;
            Cursor c = db.query(Constants.CHAPTER, args, Constants.STORY_TITLE + "=?", title, null, null, null);

            while (c.moveToNext()){
                String str = c.getString(2);
                sTitle = c.getString(0);
                titlle = c.getString(1);
                sc = new Chapter(c.getString(0), c.getString(1));
                sc.setLines(Arrays.asList(str.split("\n")));
                List<Comment> comments = getCommentsForChapter(sTitle, titlle);
                sc.setComments(comments);
                chapters.add(sc);
            }

            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return chapters;
    }

    public List<String> getStringsStuff(String table, String where, String column, String orderBy, String...title){
        List<String> stuff = new ArrayList<>();
        try {

            String[] args = {
                    column,
            };

            Cursor c = db.query(table, args, where, title, null, null, orderBy);
            while (c.moveToNext()){
                stuff.add(c.getString(0));
            }

            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return stuff;
    }

    public boolean isFollowing(String follower, String username) {
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + Constants.FOLLOWERS + " WHERE " + Constants.USERNAME + "=?"
                     + " AND "+ Constants.FOLLOWER + "=?" , new String[]{username, follower});

            if (c.moveToNext()){
                c.close();
                return true;
            }

            c.close();
            return false;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public Bitmap getImage(String table, String key, String...title) {
        try {
            Bitmap img = null;
            byte[] arr = null;
            String[] args = {
                    Constants.IMAGE
            };
            Cursor c = db.query(table, args, key + "=?", title, null, null, null);

            while (c.moveToNext()){
                arr = c.getBlob(0);
                img = Converter.decodeImage(arr);
            }

            c.close();
            return img;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public List<Reply> getReplies(long id) {
        List<Reply> replies = new ArrayList<>();
        try {
            String[] args = {
                    Constants.USERNAME,
                    Constants.MESSAGE
            };
            String[] col = {
                    String.valueOf(id)
            };
            Cursor c = db.query(Constants.REPLIES, args, Constants.ID + "=?", col, null, null, null);

            while (c.moveToNext()){
                replies.add(0, new Reply(id, c.getString(0), c.getString(1)));
            }

            c.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return replies;
    }

    public ActivityEvent getActivity(String...id) {
        try {
            ActivityEvent sc = null;
            String[] args = {
                    Constants.ID,
                    Constants.USERNAME,
                    Constants.MESSAGE
            };

            Cursor c = db.query(Constants.ACTIVITIES, args, Constants.ID + "=?", id, null, null, null);

            if (c.moveToNext()){
                sc = new ActivityEvent(c.getLong(0), c.getString(1), c.getString(2));
            }

            c.close();
            return sc;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public Story getStoryByID(String... s) {
        try {
            Story sc = null;
            String[] args = {
                    Constants.TITLE,
                    Constants.AUTHOR,
                    Constants.SYNOPSIS,
                    Constants.LANGUAGE,
                    Constants.RATING,
                    Constants.LIKES,
                    Constants.STATUS,
                    Constants.DATE
            };

            Cursor c = db.query(Constants.STORY, args, Constants.TITLE + "=?", s, null, null, null);

            if (c.moveToNext()) {
                sc = new Story(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3), c.getString(4));

                sc.setLikes(c.getInt(5));
                sc.setStatus(c.getInt(6));
                sc.setDate(c.getString(7));

                List<String> tags = getStringsStuff(Constants.TAGS,
                        Constants.STORY_TITLE + "=?", Constants.TAG, null, sc.getTitle());
                sc.setTags(new HashSet<>(tags));
                tags = getStringsStuff(Constants.CATEGORIES,
                        Constants.STORY_TITLE + "=?", Constants.CATEGORY, null, sc.getTitle());
                sc.setCategories(new HashSet<>(tags));
                tags = getStringsStuff(Constants.CHARACTERS,
                        Constants.STORY_TITLE + "=?", Constants.NAME, null, sc.getTitle());
                sc.setCharacters(new HashSet<>(tags));
                tags = getStringsStuff(Constants.GENRES,
                        Constants.STORY_TITLE + "=?", Constants.GENRE, null, sc.getTitle());
                sc.setGenres(new HashSet<>(tags));

                sc.setChapterCount(getChapterCount(sc.getTitle()));
                sc.setWordCount(getStoryWordCount(sc.getTitle()));
            }

            c.close();
            return sc;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessages(boolean isSent, String...username) {
        List<Message> messages = new ArrayList<>();
        try{
            Message m;
            String[] args = {
                    Constants.SENDER,
                    Constants.USERNAME,
                    Constants.SUBJECT,
                    Constants.MESSAGE,
                    Constants.DATE
            };
            String where;
            if (isSent)
                where = Constants.SENDER + "=?";
            else
                where = Constants.USERNAME + "=?";
            Cursor c = db.query(Constants.MESSAGES, args, where, username, null, null, null);

            while (c.moveToNext()) {
                m = new Message(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3));
                m.setDate(c.getString(4));
                messages.add(m);
            }

            c.close();
        }catch (Throwable t){
            t.printStackTrace();
        }
        return messages;
    }


    public List<ActivityEvent> getActivitiesForUser(String...username) {
        List<ActivityEvent> activityEvents = new ArrayList<>();
        try{
            ActivityEvent m;
            String[] args = {
                    Constants.ID,
                    Constants.USERNAME,
                    Constants.MESSAGE,
            };

            Cursor c = db.query(Constants.ACTIVITIES, args, Constants.USERNAME + "=?", username, null, null, null);

            while (c.moveToNext()) {
                m = new ActivityEvent(c.getLong(0), c.getString(1),
                        c.getString(2));

                activityEvents.add(m);
            }
            c.close();

            List<String> followers = getStringsStuff(Constants.FOLLOWERS, Constants.FOLLOWER + "=?", Constants.USERNAME, null, username);
            for (String f : followers){
                Cursor b = db.query(Constants.ACTIVITIES, args, Constants.USERNAME + "=?", new String[]{f}, null, null, null);

                while (b.moveToNext()) {
                    m = new ActivityEvent(b.getLong(0), b.getString(1),
                            b.getString(2));

                    activityEvents.add(m);
                }
                b.close();
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
        return activityEvents;
    }

    public void increaseLikes(String storyTitle) {
        try{
            Story story = getStoryByID(storyTitle);
            story.setLikes(story.getLikes()+1);
            ContentValues cv = new ContentValues();
            int i =  story.getLikes();
            cv.put(Constants.LIKES, ++i);
            update(Constants.STORY, cv, Constants.TITLE + "=?", storyTitle);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public void decreaseLikes(String storyTitle) {
        try{
            Story story = getStoryByID(storyTitle);
            story.setLikes(story.getLikes()-1);
            ContentValues cv = new ContentValues();
            int i =  story.getLikes();
            cv.put(Constants.LIKES, --i);
            update(Constants.STORY, cv, Constants.TITLE + "=?", storyTitle);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public void clearDatabase(){
        try {
            db.execSQL(DataBaseOperations.SQL_DELETE_FOLLOWERS);
            db.execSQL(DataBaseOperations.SQL_DELETE_MESSAGESES);
            db.execSQL(DataBaseOperations.SQL_DELETE_ACTIVITIES_REPLIES);
            db.execSQL(DataBaseOperations.SQL_DELETE_ACTIVITIES);
            db.execSQL(DataBaseOperations.SQL_DELETE_CHARACTERS);
            //db.execSQL(DataBaseOperations.SQL_DELETE_USERS);
            db.execSQL(DataBaseOperations.SQL_DELETE_CURRENT_READ);
            db.execSQL(DataBaseOperations.SQL_DELETE_FAVOURITE_STORIES);
            db.execSQL(DataBaseOperations.SQL_DELETE_TAGS);
            db.execSQL(DataBaseOperations.SQL_DELETE_GENRES);
            db.execSQL(DataBaseOperations.SQL_DELETE_COMMENTS);
            db.execSQL(DataBaseOperations.SQL_DELETE_CATEGORIES);
            db.execSQL(DataBaseOperations.SQL_DELETE_CHAPTERS);
            db.execSQL(DataBaseOperations.SQL_DELETE_STORIES);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void open() {
        try {
            db = getWritableDatabase();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void close() {
        try {
            db.close();
            super.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    /* public LIST<String> getStoriesTags(String...title){
        LIST<String> tags = new ArrayList<>();
        try {
            Chapter sc;
            String[] args = {
                    Constants.TAG,
            };

            Cursor c = db.query(Constants.TAGS, args, Constants.STORY_TITLE + "=?", title, null, null, null);

            while (c.moveToNext()){
                tags.add(c.getString(0));
            }

            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return tags;
    }

    public LIST<String> getStoriesComments(String...title){
        LIST<String> comments = new ArrayList<>();
        try {
            Chapter sc;
            String[] args = {
                    Constants.COMMENT,
            };

            Cursor c = db.query(Constants.COMMENTS, args, Constants.STORY_TITLE + "=?", title, null, null, null);

            while (c.moveToNext()){
                comments.add(c.getString(0));
            }

            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return comments;
    }*/

}
