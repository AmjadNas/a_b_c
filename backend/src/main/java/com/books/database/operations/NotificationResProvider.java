package com.books.database.operations;

import com.books.objects.MyNotification;
import com.books.utils.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationResProvider {

    private static final String INSERT_NOTIF = "INSERT INTO " + Constants.NOTIFICATIONS + "(" +
            Constants.TYPE + ", " +
            Constants.SENDER + ", " +
            Constants.USERNAME + ", " +
            Constants.SEEN + ", " +
            Constants.DATE  +
            ") VALUES (?, ?, ?, ?, ?); ";

    private static final String DELETE_NOTIF = "DELETE FROM TABLE " + Constants.NOTIFICATIONS + " WHERE " + Constants.ID + " =?;";
    private static final String GET_NOTIFIC_FOR_USER = "SELECT * FROM "
            + Constants.NOTIFICATIONS
            + " WHERE " + Constants.USERNAME + " =? AND " + Constants.SEEN +" =?;";
    private static final String UPDATE_NOTIFIC = "UPDATE " + Constants.NOTIFICATIONS
            + " SET " + Constants.SEEN + "=? WHERE "
            + Constants.USERNAME + " =? AND " + Constants.SEEN +" =?;";

    public boolean insetNotific(String type, String sender, String username,
                                String sent_you_a_message,
                                long dateFormat, Connection conn){
        boolean result = false;
        PreparedStatement ps2 = null;
        if (!sender.equals(username)) {
            try {
                ps2 = (PreparedStatement) conn.prepareStatement(INSERT_NOTIF);
                ps2.setString(1, type);
                ps2.setString(2, sender);
                ps2.setString(3, username);
                ps2.setBoolean(4, false);
                ps2.setLong(5, dateFormat);
                ps2.execute();
                result = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {

                if (ps2 != null) {
                    try {
                        ps2.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return result;
    }

    public List<MyNotification> getNotificationsForUser(String username, Connection conn) throws SQLException {

        List<MyNotification> messages = new ArrayList<>();
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(GET_NOTIFIC_FOR_USER);
            ps.setString(1, username);
            ps.setBoolean(2, false);
            rs = ps.executeQuery();
            while (rs.next()) {
                String sender = rs.getString(Constants.SENDER);
                String type = rs.getString(Constants.TYPE);
                long date = rs.getLong(Constants.DATE);

                MyNotification notific = new MyNotification(type, sender, username, date);
                messages.add(notific);
            }

            ps2 = conn.prepareStatement(UPDATE_NOTIFIC);
            ps2.setBoolean(1, true);
            ps2.setString(2, username);
            ps2.setBoolean(3, false);
            ps2.execute();

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
            if (ps2 != null) {
                try {
                    ps2.close();
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
        return messages;
    }

    public boolean deleteNotific(){
        return false;
    }
}
