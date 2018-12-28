package android.course.a_b_c.DatabaseHandkers;

import android.course.a_b_c.Utils.Constants;

public final class DataBaseOperations {

    private DataBaseOperations(){}
    /** table entries **/
    protected static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + Constants.USER + " (" +
                    Constants.USERNAME + " VARCHAR(12) PRIMARY KEY," +
                    Constants.FNAME + " VARCHAR(12)," +
                    Constants.LNAME + " VARCHAR(12), " +
                    Constants.EMAIL +" VARCHAR(128), " +
                    Constants.PASSWORD +" VARCHAR(25)," +
                    Constants.IMAGE +" BLOB " +
                    ")";
    protected static final String SQL_CREATE_STORIES = "CREATE TABLE "
            + Constants.STORY + " (" +
            Constants.TITLE + " VARCHAR(12) PRIMARY KEY," +
            Constants.AUTHOR + " VARCHAR(12)," +
            Constants.LANGUAGE + " VARCHAR(12)," +
            Constants.SYNOPSIS + " VARCHAR(120)," +
            Constants.RATING + " VARCHAR(25)," +
            Constants.STATUS +" BOOLEAN," +
            Constants.LIKES +" INTEGER," +
            Constants.DATE + " DATE, " +
            Constants.IMAGE +" BLOB" +
            ")";
    protected static final String SQL_CREATE_CHAPTERS = "CREATE TABLE "
            + Constants.CHAPTER + " (" +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            Constants.TITLE + " VARCHAR(12), " +
            Constants.LINES + " TEXT, " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "CONSTRAINT PK1 PRIMARY KEY (" + Constants.TITLE + "," + Constants.STORY_TITLE + ")" +
            ")";
    protected static final String SQL_CREATE_STORIES_CATEGORIES = "CREATE TABLE "
            + Constants.CATEGORIES + " (" +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            Constants.CATEGORY + " VARCHAR(45), " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "CONSTRAINT PK2 PRIMARY KEY (" + Constants.CATEGORY + "," + Constants.STORY_TITLE + ")" +
            ")";
    protected static final String SQL_CREATE_STORIES_COMMENTS = "CREATE TABLE "
            + Constants.COMMENTS + " (" +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            Constants.TITLE + " VARCHAR(12), " +
            Constants.USERNAME + " VARCHAR(12), " +
            Constants.COMMENT + " VARCHAR(120), " +
            Constants.DATE + " DATE, " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "FOREIGN KEY (" + Constants.TITLE + ") REFERENCES " + Constants.CHAPTER + " (" +Constants.TITLE + "), "+
            "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER + " (" +Constants.USERNAME + "), "+
            "CONSTRAINT PK3 PRIMARY KEY (" + Constants.TITLE + "," + Constants.COMMENT + "," + Constants.STORY_TITLE + "," + Constants.DATE+ ")" +
            ")";
    protected static final String SQL_CREATE_STORIES_GENRES = "CREATE TABLE "
            + Constants.GENRES + " (" +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            Constants.GENRE + " VARCHAR(45), " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "CONSTRAINT PK4 PRIMARY KEY (" + Constants.GENRE + "," + Constants.STORY_TITLE + ")" +
            ")";
    protected static final String SQL_CREATE_STORIES_TAGS = "CREATE TABLE "
            + Constants.TAGS + " (" +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            Constants.TAG + " VARCHAR(20), " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "CONSTRAINT PK5 PRIMARY KEY (" + Constants.TAG + "," + Constants.STORY_TITLE + ")" +
            ")";
    protected static final String SQL_CREATE_FOLLOWERS = "CREATE TABLE "
            + Constants.FOLLOWERS + " (" +
            Constants.USERNAME + " VARCHAR(12), " +
            Constants.FOLLOWER + " VARCHAR(12), " +
            "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER + " (" +Constants.USERNAME + "), "+
            "FOREIGN KEY (" + Constants.FOLLOWER + ") REFERENCES " + Constants.USER + " (" +Constants.USERNAME + "), "+
            "CONSTRAINT PK6 PRIMARY KEY (" + Constants.USERNAME + "," + Constants.FOLLOWER + ")" +
            ")";
    protected static final String SQL_CREATE_MESSAGE = "CREATE TABLE "
            + Constants.MESSAGES + " (" +
            Constants.SENDER + " VARCHAR(12), "+
            Constants.USERNAME + " VARCHAR(12), " +
            Constants.SUBJECT + " VARCHAR(12), " +
            Constants.MESSAGE + " TEXT, " +
            Constants.DATE + " DATE, " +
            "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER + " (" + Constants.USERNAME + "), "+
            "FOREIGN KEY (" + Constants.SENDER + ") REFERENCES " + Constants.USER + " (" + Constants.USERNAME + "), "+
            "CONSTRAINT PK7 PRIMARY KEY (" + Constants.SENDER + ","
            + Constants.USERNAME + "," + Constants.MESSAGE + "," + Constants.DATE+ ")" +
            ")";
    protected static final String SQL_CREATE_ACTIVITY = "CREATE TABLE "
            + Constants.ACTIVITIES + " (" +
            Constants.ID + " INTEGER PRIMARY KEY, " +
            Constants.USERNAME + " VARCHAR(12), " +
            Constants.MESSAGE + " VARCHAR(255), " +
            "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER + " (" +Constants.USERNAME + ")"+
            ")";
    protected static final String SQL_CREATE_ACTIVITY_REPLIES = "CREATE TABLE "
            + Constants.REPLIES + " (" +
            Constants.ID + " INTEGER, " +
            Constants.USERNAME + " VARCHAR(12), " +
            Constants.MESSAGE + " VARCHAR(255), " +
            "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER + " (" +Constants.USERNAME + "), "+
            "FOREIGN KEY (" + Constants.ID + ") REFERENCES " + Constants.ACTIVITIES + " (" +Constants.ID + "), "+
            "CONSTRAINT PK8 PRIMARY KEY (" + Constants.ID + "," + Constants.USERNAME + "," + Constants.MESSAGE + ")" +
            ")";
    protected static final String SQL_CREATE_CHARACTERS = "CREATE TABLE "
            + Constants.CHARACTERS + " (" +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            Constants.NAME + " VARCHAR(12), " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "CONSTRAINT PK9 PRIMARY KEY (" + Constants.NAME + "," + Constants.STORY_TITLE + ")" +
            ")";
    protected static final String SQL_CREATE_FAVOURITE_STORIES = "CREATE TABLE "
            + Constants.FAVOURITE_STORIES + " (" +
            Constants.USERNAME + " VARCHAR(12), " +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER + " (" +Constants.USERNAME + "), "+
            "CONSTRAINT PK10 PRIMARY KEY (" + Constants.USERNAME + "," + Constants.STORY_TITLE + ")" +
            ")";
    protected static final String SQL_CREATE_CURRENT_READ = "CREATE TABLE "
            + Constants.READING_STORIES + " (" +
            Constants.USERNAME + " VARCHAR(12), " +
            Constants.STORY_TITLE + " VARCHAR(12), " +
            "FOREIGN KEY (" + Constants.STORY_TITLE + ") REFERENCES " + Constants.STORY + " (" +Constants.TITLE + "), "+
            "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER + " (" +Constants.USERNAME + "), "+
            "CONSTRAINT PK11 PRIMARY KEY (" + Constants.USERNAME + "," + Constants.STORY_TITLE + ")" +
            ")";
    /** delete entries **/
    protected static final String SQL_DELETE_STORY_TABLE =
            "DROP TABLE IF EXISTS " + Constants.STORY;

    protected static final String SQL_DELETE_CHAPTER_TABLE =
            "DROP TABLE IF EXISTS " + Constants.CHAPTER;

    protected static final String SQL_DELETE_CATEGORIES_TABLE =
            "DROP TABLE IF EXISTS " + Constants.CATEGORIES;

    protected static final String SQL_DELETE_COMMENTS_TABLE =
            "DROP TABLE IF EXISTS " + Constants.COMMENTS;

    protected static final String SQL_DELETE_GENRES_TABLE =
            "DROP TABLE IF EXISTS " + Constants.GENRES;

    protected static final String SQL_DELETE_TAGS_TABLE =
            "DROP TABLE IF EXISTS " + Constants.TAGS;

    protected static final String SQL_DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS " + Constants.USER;

    protected static final String SQL_DELETE_FOLLOWERS_TABLE =
            "DROP TABLE IF EXISTS " + Constants.FOLLOWERS;

    protected static final String SQL_DELETE_MESSAGES_TABLE =
            "DROP TABLE IF EXISTS " + Constants.MESSAGES;

    protected static final String SQL_DELETE_ACTIVITIES_TABLE =
            "DROP TABLE IF EXISTS " + Constants.ACTIVITIES;

    protected static final String SQL_DELETE_ACTIVITIES_REPLIES_TABLE =
            "DROP TABLE IF EXISTS " + Constants.REPLIES;

    protected static final String SQL_DELETE_CHARACTERS_TABLE =
            "DROP TABLE IF EXISTS " + Constants.CHARACTERS;

    protected static final String SQL_DELETE_FAVOURITE_STORIES_TABLE =
            "DROP TABLE IF EXISTS " + Constants.FAVOURITE_STORIES;

    protected static final String SQL_DELETE_CURRENT_READ_TABLE =
            "DROP TABLE IF EXISTS " + Constants.READING_STORIES;

    protected static final String SQL_DELETE_STORIES =
            "DELETE FROM " + Constants.STORY;

    protected static final String SQL_DELETE_CHAPTERS =
            "DELETE FROM  " + Constants.CHAPTER;

    protected static final String SQL_DELETE_CATEGORIES =
            "DELETE FROM " + Constants.CATEGORIES;

    protected static final String SQL_DELETE_COMMENTS =
            "DELETE FROM " + Constants.COMMENTS;

    protected static final String SQL_DELETE_GENRES =
            "DELETE FROM " + Constants.GENRES;

    protected static final String SQL_DELETE_TAGS =
            "DELETE FROM " + Constants.TAGS;

    protected static final String SQL_DELETE_USERS =
            "DELETE FROM " + Constants.USER;

    protected static final String SQL_DELETE_FOLLOWERS =
            "DELETE FROM " + Constants.FOLLOWERS;

    protected static final String SQL_DELETE_ACTIVITIES =
            "DELETE FROM " + Constants.ACTIVITIES;

    protected static final String SQL_DELETE_ACTIVITIES_REPLIES =
            "DELETE FROM " + Constants.REPLIES;

    protected static final String SQL_DELETE_MESSAGESES =
            "DELETE FROM " + Constants.MESSAGES;

    protected static final String SQL_DELETE_CHARACTERS =
            "DELETE FROM " + Constants.CHARACTERS;

    protected static final String SQL_DELETE_FAVOURITE_STORIES =
            "DELETE FROM " + Constants.FAVOURITE_STORIES;

    protected static final String SQL_DELETE_CURRENT_READ =
            "DELETE FROM " + Constants.READING_STORIES;
}
