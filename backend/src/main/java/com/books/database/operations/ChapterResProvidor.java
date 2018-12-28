package com.books.database.operations;

import com.books.objects.Chapter;
import com.books.objects.Comment;
import com.books.objects.Story;
import com.books.utils.Constants;
import com.books.utils.Convector;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChapterResProvidor {

    private static final String INSERT_COMMENT =  "INSERT INTO " + Constants.COMMENTS + " (" +
            Constants.STORY_TITLE + ", " +
            Constants.TITLE + ", " +
            Constants.USERNAME + ", " +
            Constants.COMMENT + ", " +
            Constants.DATE +
            ")" +
            " VALUES (?, ?, ?, ?, ?);";

    private static final String INSERT_CHAPTER = "INSERT INTO chapter (`storyTitle`, `title`, `lines`) VALUES (?, ?, ?);";

    private static final String UPDATE_CHAPTER = "UPDATE " + Constants.CHAPTER + " SET " +
            Constants.LINES + "=? WHERE " + Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?;" ;

    private static final String DELETE_CHAPTER = "DELETE FROM  " + Constants.CHAPTER +
            " WHERE " + Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?;";

    private static final String DELETE_COMMENTS = "DELETE FROM  " + Constants.COMMENTS +
            " WHERE " + Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?;";

    private static final String GET_CHAPTER_COMMENTS = "SELECT * FROM  " + Constants.COMMENTS +
            " WHERE " + Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?;";

    private static final String GET_STORIES_CHAPTERS = "SELECT * FROM  " + Constants.CHAPTER +
            " WHERE " + Constants.STORY_TITLE + "=?;";

    private static final String GET_STORY_CHAPTER = "SELECT * FROM  " + Constants.CHAPTER +
            " WHERE " + Constants.STORY_TITLE + "=? AND " + Constants.TITLE + "=?;";

    public List<Chapter> getStoryChapters(String sTitle, Connection conn) throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement(GET_STORIES_CHAPTERS);
            ps.setString(1, sTitle);
            rs = ps.executeQuery();

            while (rs.next()) {
                String title = rs.getString(Constants.TITLE);
                String lines = rs.getString(Constants.LINES);

                Chapter item = new Chapter(sTitle,title);
                item.setLines(Convector.fromStringToLines(lines));
                chapters.add(item);
            }

        } catch (SQLException e) {
            throw e;

        } catch (Throwable e) {
            e.printStackTrace();

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return chapters;
    }

    public int getWordCount(String sTitle, Connection conn) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        int i = 0;
        try {

            ps = conn.prepareStatement(GET_STORIES_CHAPTERS);
            ps.setString(1, sTitle);
            rs = ps.executeQuery();

            while (rs.next()) {
                String username = rs.getString(Constants.LINES);
                for (String line : username.split("\n"))
                    for (String s : line.split(" "))
                        i++;
            }

        } catch (SQLException e) {
            throw e;

        } catch (Throwable e) {
            e.printStackTrace();

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return i;
    }

    public List<Comment> getCommentsForChapter(String sTitle, String title, Connection conn) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement(GET_CHAPTER_COMMENTS);
            ps.setString(1, sTitle);
            ps.setString(2, title);
            rs = ps.executeQuery();

            while (rs.next()) {
                String username = rs.getString(Constants.USERNAME);
                String comment = rs.getString(Constants.COMMENT);
                long date = rs.getLong(Constants.DATE);

                Comment item = new Comment(title, sTitle,username, comment, date);
                comments.add(item);

            }

        } catch (SQLException e) {
            throw e;

        } catch (Throwable e) {
            e.printStackTrace();

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return comments;
    }

    public boolean removeChapter(String sTitle, String title, Connection conn){
        return removeStuff(sTitle, title, DELETE_CHAPTER, conn);
    }

    public boolean removeComments(String sTitle, String title, Connection conn){
        return removeStuff(sTitle, title, DELETE_COMMENTS, conn);
    }

    public boolean insertComment(Comment obj, Connection conn){
        boolean result = false;
        PreparedStatement ps = null;

        try {

            String title = obj.getChapterTitle();
            String sTitle = obj.getStoryTiele();
            String username = obj.getUsername();
            String comment = obj.getComment();
            long date = obj.getDatFormate();

            // its execute insert
            ps = (PreparedStatement) conn.prepareStatement(INSERT_COMMENT);

            ps.setString(1, sTitle);
            ps.setString(2, title);
            ps.setString(3, username);
            ps.setString(4, comment);
            ps.setLong(5, date);
            ps.execute();
            ItemsResProvider resProvider = new ItemsResProvider();
            Story s = resProvider.getStoryByTitle(sTitle, conn);
            NotificationResProvider notificationResProvider = new NotificationResProvider();
            notificationResProvider.insetNotific(Constants.COMMENT, username, s.getAuthor(),
                    "commented on chapter  "+title + " in "+ sTitle, date, conn);
            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }

    public boolean insertChapter(Chapter obj, Connection conn){
        boolean result = false;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement stt = null;

        try {

            String title = obj.getTitle();
            String sTitle = obj.getStoryTitle();
            String lines = Convector.combineLines(obj.getLines());

            stt = (PreparedStatement) conn.prepareStatement(GET_STORY_CHAPTER);
            stt.setString(1, sTitle);
            stt.setString(2, title);

            if (stt.execute()) {
                rs1 = stt.getResultSet();
                if (rs1.next()) {
                    // its execute update
                    ps = (PreparedStatement) conn.prepareStatement(UPDATE_CHAPTER);

                    ps.setString(1, lines);
                    // where
                    ps.setString(2, sTitle);
                    ps.setString(3, title);
                    ps.execute();

                    result = true;

                } else {

                    // its execute insert
                    ps = (PreparedStatement) conn.prepareStatement(INSERT_CHAPTER);

                    ps.setString(1, sTitle);
                    ps.setString(2, title);
                    ps.setString(3, lines);

                    ps.execute();

                    result = true;

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stt != null) {
                try {
                    stt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private boolean removeStuff(String sTitle, String title, String op,Connection conn){
        boolean result = false;

        PreparedStatement ps = null;

        try {
            // its execute insert
            ps = (PreparedStatement) conn.prepareStatement(op);
            ps.setString(1, sTitle);
            ps.setString(2, title);
            ps.execute();

            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public boolean deleteItem(Chapter obj, Connection conn) throws SQLException {

        boolean result = false;
        PreparedStatement ps = null;

        try {
            if (obj != null) {

                ps = (PreparedStatement) conn.prepareStatement(DELETE_CHAPTER);
                ps.setString(1, obj.getStoryTitle());
                ps.setString(2, obj.getTitle());
                ps.execute();

                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
