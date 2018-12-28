package com.books.database.operations;

import com.books.objects.ActivityEvent;
import com.books.objects.Reply;
import com.books.utils.Constants;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ActivitiesResProvidor {

    private static final String INSERT_ACTIVITY = "INSERT INTO "
            + Constants.ACTIVITIES + "(" +
            Constants.ID + ", "+
            Constants.USERNAME + ", "+
            Constants.MESSAGE +
            ") VALUES (?, ?, ?);";

    private static final String INSERT_ACTIVITY_REPLY = "INSERT INTO "
            + Constants.REPLIES + "(" +
            Constants.ID + ", "+
            Constants.USERNAME + ", "+
            Constants.MESSAGE +
            ") VALUES (?, ?, ?);";

    private static final String INSERT_FOLLOER = "INSERT INTO "
            + Constants.FOLLOWERS + "(" +
            Constants.USERNAME + ", "+
            Constants.FOLLOWER +
            ") VALUES (?, ?);";

    private static final String GET_ACTIVITY_REPLIES = "SELECT * FROM " + Constants.REPLIES + " WHERE " + Constants.ID + "=?;";

    private static final String GET_ALL_ACTIVITIES = "SELECT * FROM " + Constants.ACTIVITIES
            + " ORDER BY "+ Constants.ID + " DESC";

    private static final String GET_USER_ACTIVITIES = "SELECT * FROM " + Constants.ACTIVITIES +
            " WHERE " + Constants.USERNAME + "=?"
            + " ORDER BY "+ Constants.ID + " DESC";

    private static final String GET_USER_FOLLOWER = "SELECT " + Constants.FOLLOWER
            + " FROM " + Constants.FOLLOWERS + " WHERE " + Constants.USERNAME + "=?;";

    private static final String GET_USER_FOLLOWEG = "SELECT " + Constants.USERNAME
            + " FROM " + Constants.FOLLOWERS + " WHERE " + Constants.FOLLOWER + "=?;";

    private static final String GET_ACTIVITY_BY_ID = " SELECT * FROM " + Constants.ACTIVITIES +
            " WHERE " + Constants.ID + "=?;";

    private static final String DELETE_FOLLOWER = "DELETE FROM " + Constants.FOLLOWERS
            + " WHERE " + Constants.USERNAME + "=? AND " + Constants.FOLLOWER + "=?;";

    public boolean insertReply(Reply r, Connection conn){
        boolean result = false;
        PreparedStatement ps2 = null;

        try {
            ps2 = (PreparedStatement) conn.prepareStatement(INSERT_ACTIVITY_REPLY);
            ps2.setLong(1, r.getActID());
            ps2.setString(2, r.getUsername());
            ps2.setString(3, r.getMessage());
            ps2.execute();
            ActivityEvent act = getActivityById(r.getActID(), conn);
            NotificationResProvider notificationResProvider = new NotificationResProvider();

            notificationResProvider.insetNotific(Constants.REPLIES, r.getUsername(), act.getUsername(),
                    "replied to an activity post", System.currentTimeMillis(), conn);

            result = true;


        }catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if(ps2 != null) {
                try {
                    ps2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public boolean insertFollowUser(String follower, String username, Connection conn){
        boolean result = false;
        PreparedStatement ps2 = null;

        try {
            ps2 = (PreparedStatement) conn.prepareStatement(INSERT_FOLLOER);
            ps2.setString(1, username);
            ps2.setString(2, follower);
            ps2.execute();
            NotificationResProvider notificationResProvider = new NotificationResProvider();
            notificationResProvider.insetNotific(Constants.FOLLOWING, follower, username,
                    "is now following you", System.currentTimeMillis(), conn);
            result = true;
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if(ps2 != null) {
                try {
                    ps2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public boolean unFollowUser(String follower, String username, Connection conn){
        boolean result = false;
        PreparedStatement ps2 = null;

        try {
            ps2 = (PreparedStatement) conn.prepareStatement(DELETE_FOLLOWER);
            ps2.setString(1, username);
            ps2.setString(2, follower);
            ps2.execute();
            result = true;
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if(ps2 != null) {
                try {
                    ps2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public ActivityEvent getActivityById(long id, Connection conn) throws SQLException {
        ActivityEvent activityEvent = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps  = conn.prepareStatement(GET_ACTIVITY_BY_ID);
            rs = ps.executeQuery();
            if (rs.next()){
                activityEvent = new ActivityEvent(id,
                        rs.getString(Constants.USERNAME), rs.getString(Constants.MESSAGE));
            }
        }catch (SQLException e) {
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
        return activityEvent;
    }

    public List<ActivityEvent> getActivitiesForUser(String username, Connection conn) throws SQLException {
        List<ActivityEvent> activityEvents = new ArrayList<>();
        TreeSet<ActivityEvent> set = new TreeSet<>();
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement stt = null;

        try {

            List<String> list = getUserFollowings(username, conn);
            for (String s : list){
                stt = conn.prepareStatement(GET_USER_ACTIVITIES);
                stt.setString(1, s);
                rs1 = stt.executeQuery();

                while (rs1.next()){

                    long id = rs1.getLong(Constants.ID);
                    String message = rs1.getString(Constants.MESSAGE);
                    ActivityEvent item = new ActivityEvent(id, s, message);

                    set.add(item);
                }
            }

            ps = conn.prepareStatement(GET_USER_ACTIVITIES);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()){

                long id = rs.getLong(Constants.ID);
                String message = rs.getString(Constants.MESSAGE);
                ActivityEvent item = new ActivityEvent(id, username, message);

                set.add(item);
            }

            while (!set.isEmpty())
                activityEvents.add(set.pollFirst());

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
        return activityEvents;
    }

    public boolean insertActivity(ActivityEvent act, Connection conn){
        boolean result = false;
        PreparedStatement ps2 = null;

        try {
            ps2 = (PreparedStatement) conn.prepareStatement(INSERT_ACTIVITY);
            ps2.setLong(1, act.getId());
            ps2.setString(2, act.getUsername());
            ps2.setString(3, act.getMessage());
            ps2.execute();
            List<String> followers = getFollowersForUser(act.getUsername(), conn);
            NotificationResProvider notificationResProvider = new NotificationResProvider();
            for(String user : followers){
                notificationResProvider.insetNotific(Constants.ACTIVITIES, act.getUsername(), user,
                        "posted an activity message ", act.getId(), conn);
            }
            result = true;
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if(ps2 != null) {
                try {
                    ps2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public List<String> getUserFollowings(String username, Connection conn) throws SQLException {
        List<String> results = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(GET_USER_FOLLOWEG);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString(Constants.USERNAME);
                results.add(id);
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

        return results;
    }

    public List<String> getFollowersForUser(String username, Connection conn) throws SQLException {
        List<String> results = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement(GET_USER_FOLLOWER);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString(Constants.FOLLOWER);
                results.add(id);

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

        return results;
    }

    public List<Reply> getActvityReplies(long aLong, Connection conn) throws SQLException {
        List<Reply> results = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement(GET_ACTIVITY_REPLIES);
            ps.setLong(1, aLong);
            rs = ps.executeQuery();

            while (rs.next()) {

                String username = rs.getString(Constants.USERNAME);
                String message = rs.getString(Constants.MESSAGE);

                Reply r = new Reply(aLong, username, message);

                results.add(r);

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

        return results;
    }
}
