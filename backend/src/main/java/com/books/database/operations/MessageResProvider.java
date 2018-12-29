package com.books.database.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.books.objects.Message;
import com.books.utils.Constants;

public class MessageResProvider {

	private static final String GET_SENT_MESSAGES = "SELECT * FROM " + Constants.MESSAGES + " WHERE "+ Constants.SENDER +" =?;" ;
    private static final String GET_USER_MESSAGES = "SELECT * FROM " + Constants.MESSAGES + " WHERE "+ Constants.USERNAME +" =?;" ;

	private static final String INSERT_MESSAGE = "INSERT INTO " + Constants.MESSAGES + "(" +
			Constants.SENDER + ", " +
			Constants.USERNAME + ", " +
            Constants.SUBJECT + ", " +
			Constants.MESSAGE + ", " +
			Constants.DATE +
			") VALUES (?,?,?,?,?);";
    private static final String DELETE_MEESSAGE = "DELETE FROM " + Constants.MESSAGES +
            " WHERE " + Constants.SENDER + " =? AND " +
            Constants.USERNAME + " =? AND " +
            Constants.DATE +"=?;";


    public boolean deleteMessage(Message obj, Connection conn){
        boolean result = false;
        PreparedStatement ps2 = null;

        try {

            String sender = obj.getSender();
            String guest = obj.getUsername();

            ps2 = (PreparedStatement) conn.prepareStatement(DELETE_MEESSAGE);
            ps2.setString(1, sender);
            ps2.setString(2, guest);
            ps2.setLong(3, obj.getDateFormat());;
            result = ps2.execute();


        } catch (SQLException e) {
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

    public boolean insertMessage(Message obj, Connection conn) {

        boolean result = false;
        PreparedStatement ps2 = null;

        try {

            String sender = obj.getSender();
            String guest = obj.getUsername();
            String text = obj.getMessage();

            ps2 = (PreparedStatement) conn.prepareStatement(INSERT_MESSAGE);
            ps2.setString(1, sender);
            ps2.setString(2, guest);
            ps2.setString(3, obj.getSubject());
            ps2.setString(4, text);
            ps2.setLong(5, obj.getDateFormat());
            ps2.execute();

            NotificationResProvider resProvider = new NotificationResProvider();
            resProvider.insetNotific(Constants.MESSAGE, obj.getSender(), obj.getUsername(), "Sent you a message", obj.getDateFormat(), conn);
            result = true;


        } catch (SQLException e) {
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

    public List<Message> getRecivedMessages(String username, Connection conn) throws SQLException {
        List<Message> messages = new ArrayList<>();
        PreparedStatement ps2 =  null, ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(GET_USER_MESSAGES);
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {

                String sender = rs.getString(Constants.SENDER);
                String subject = rs.getString(Constants.SUBJECT);
                String text = rs.getString(Constants.MESSAGE);
                long date = rs.getLong(Constants.DATE);

                Message message = new Message(sender, username, subject, text, date);
                messages.add(message);
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
        return messages;
    }

    public List<Message> getSentMessages(String sender, Connection conn) throws SQLException {
        List<Message> messages = new ArrayList<>();
        PreparedStatement ps2 =  null, ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(GET_SENT_MESSAGES);
            ps.setString(1, sender);
            rs = ps.executeQuery();
            while (rs.next()) {

                String guest = rs.getString(Constants.USERNAME);
                String subject = rs.getString(Constants.SUBJECT);
                String text = rs.getString(Constants.MESSAGE);
                long date = rs.getLong(Constants.DATE);

                Message message = new Message(sender, guest, subject, text, date);
                messages.add(message);
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
        return messages;
    }

}
