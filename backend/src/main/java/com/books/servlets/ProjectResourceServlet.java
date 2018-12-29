package com.books.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.books.database.operations.ActivitiesResProvidor;
import com.books.database.operations.ChapterResProvidor;
import com.books.database.operations.MessageResProvider;
import com.books.database.operations.ConnPool;
import com.books.database.operations.ItemsResProvider;
import com.books.database.operations.NotificationResProvider;
import com.books.database.operations.UserResProvider;
import com.books.objects.ActivityEvent;
import com.books.objects.Chapter;
import com.books.objects.Comment;
import com.books.objects.Message;
import com.books.objects.MyNotification;
import com.books.objects.Reply;
import com.books.objects.Story;
import com.books.objects.User;
import com.books.utils.Constants;
import com.books.utils.Convector;
import com.books.utils.FilesUtils;

import net.sf.json.JSONObject;


/**
 * Servlet implementation class ProjectResourceServlet
 */
public class ProjectResourceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ========
    private static final int GET_ALL_ITEMS_JSON_REQ = 0;
    private static final int INSERT_CHAPTER_REQ = 2;
    private static final int INSERT_ACTIVITY_REQ = 3;
    private static final int INSERT_MESSAGE_REQ = 4;
    private static final int INSERT_ITEM_FAVE_REQ = 5;
    private static final int DELETE_ITEM_REQ = 6;
    private static final int REMOVE_FAVE_ITEM = 7;
    private static final int GET_ITEM_IMAGE_REQ = 8;
    private static final int GET_RECIEVED_MESSAGES_JSON_REQ = 9;
    private static final int GET_NEWEST = 10;
    private static final int GET_SENT_MESSAGES_JSON_REQ = 11;
    private static final int GET_USER_ACTIVITIES = 12;
    private static final int DELETE_CHAPTER_REQ = 13;
    private static final int GET_CHAPTER_COMMENTS_REQ = 14;
    private static final int GET_ACTIVITY_REPLIES = 15;
    private static final int GET_FAVE_ITEM_USER_REQ = 16;
    private static final int GET_NOTIFICATIONS_JSON_REQ = 17;
    private static final int DELETE_USER_REQ = 18;
    private static final int GET_USER_IMAGE_REQ = 19;
    private static final int GET_USER_JSON_REQ = 20;
    private static final int INSERT_USER_REQ = 21;
    private static final int GET_ITEM_USER_REQ = 23;
    private static final int FOLLOW_USER = 24;
    private static final int UNFOLLOW_USER = 25;
    private static final int INSERT_COMMENT = 26;
    private static final int INSERT_REPLY = 27;
    private static final int GET_USER_FOLLOWERS = 28;
    private static final int GET_USER_FOLLOWING = 29;
    private static final int ADD_TO_CURRENT_READ = 30;
    private static final int GET_CUURENT_READING = 31;
    private static final int GET_STORY_CHAPTERS = 32;
    private static final int DELETE_MESSAGE_REQ = 33;

    private static final String RESOURCE_FAIL_TAG = "{\"result_code\":0}";
    private static final String RESOURCE_SUCCESS_TAG = "{\"result_code\":1}";

    private static final String REQ = "req";

    public static final int DB_RETRY_TIMES = 5;



    public void init(ServletConfig config) throws ServletException {

        super.init();

        String tmp = config.getServletContext().getInitParameter("localAppDir");
        if (tmp != null) {
            FilesUtils.appDirName = config.getServletContext().getRealPath(tmp);
            System.out.println(FilesUtils.appDirName);
        }
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String respPage = null;
        String userReq = req.getParameter(REQ);
        Connection conn = null;
        int retry = DB_RETRY_TIMES;

        if (userReq != null) {

            int reqNo = Integer.valueOf(userReq);
            System.out.println("ProjectResourceServlet:: req code ==>" + reqNo);

            while (retry > 0) {
                try {
                    switch (reqNo) {
                        case GET_USER_FOLLOWERS: {
                            String username = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();
                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            List<String> followers = activitiesResProvidor.getFollowersForUser(username, conn);
                            if (followers != null && !followers.isEmpty()){
                                JSONObject obj = new JSONObject();
                                obj.put(Constants.FOLLOWERS, followers);
                                respPage = obj.toString();
                            }
                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case GET_USER_FOLLOWING:{
                            String username = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();
                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            List<String> followings = activitiesResProvidor.getUserFollowings(username, conn);
                            if (followings != null && !followings.isEmpty()){
                                JSONObject obj = new JSONObject();
                                obj.put(Constants.FOLLOWING, followings);
                                respPage = obj.toString();
                            }
                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }

                        case DELETE_MESSAGE_REQ: {
                            String sender = req.getParameter(Constants.SENDER);
                            String receiver = req.getParameter(Constants.USERNAME);
                            String subject = req.getParameter(Constants.SUBJECT);
                            String msg = req.getParameter(Constants.MESSAGE);
                            Long date = Long.valueOf(req.getParameter(Constants.DATE));

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();

                            Message message = new Message(sender, receiver, subject, msg, date);

                            MessageResProvider messageResProvider = new MessageResProvider();
                            if (messageResProvider.deleteMessage(message, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case INSERT_COMMENT:{
                            String sTitle = req.getParameter(Constants.STORY_TITLE);
                            String title = req.getParameter(Constants.TITLE);
                            String username = req.getParameter(Constants.USERNAME);
                            String message = req.getParameter(Constants.MESSAGE);
                            long date = Long.valueOf(req.getParameter(Constants.DATE));

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();
                            Comment r = new Comment(sTitle, title, username,message, date);
                            ChapterResProvidor chapterResProvidor = new ChapterResProvidor();
                            if (chapterResProvidor.insertComment(r, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case INSERT_REPLY:{
                            String id = req.getParameter(Constants.ID);
                            String username = req.getParameter(Constants.USERNAME);
                            String message = req.getParameter(Constants.MESSAGE);

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();
                            Reply r = new Reply(Long.valueOf(id), username,message);
                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            if (activitiesResProvidor.insertReply(r, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case FOLLOW_USER: {

                            String follower = req.getParameter(Constants.FOLLOWER);
                            String username = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();

                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            if (activitiesResProvidor.insertFollowUser(follower, username, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case UNFOLLOW_USER: {

                            String follower = req.getParameter(Constants.FOLLOWER);
                            String username = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();

                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            if (activitiesResProvidor.unFollowUser(follower, username, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case GET_STORY_CHAPTERS:{
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;
                            String id = req.getParameter(Constants.TITLE);

                            ChapterResProvidor chapterResProvidor = new ChapterResProvidor();
                            List<Chapter> itemsList = chapterResProvidor
                                    .getStoryChapters(id, conn);
                            String resultJson = Convector.toJson(Constants.CHAPTERS, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case GET_USER_ACTIVITIES:{
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;
                            String id = req.getParameter(Constants.USERNAME);

                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            List<ActivityEvent> itemsList = activitiesResProvidor
                                    .getActivitiesForUser(id, conn);
                            String resultJson = Convector.toJson(Constants.ACTIVITIES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case GET_CUURENT_READING:{
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;
                            String id = req.getParameter(Constants.USERNAME);

                            ItemsResProvider itemsResProvider = new ItemsResProvider();
                            List<Story> itemsList = itemsResProvider
                                    .getCurrentReadingStories(id, conn);
                            String resultJson = Convector.toJson(Constants.STORIES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case GET_ITEM_USER_REQ: {
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;
                            String id = req.getParameter(Constants.USERNAME);

                            ItemsResProvider itemsResProvider = new ItemsResProvider();
                            List<Story> itemsList = itemsResProvider
                                    .getItemForUser(id, conn);
                            String resultJson = Convector.toJson(Constants.STORIES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }

                        case GET_NEWEST: {
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;

                            ItemsResProvider itemsResProvider = new ItemsResProvider();
                            List<Story> itemsList = itemsResProvider
                                    .getNewestItems(conn);
                            String resultJson = Convector.toJson(Constants.STORIES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case GET_ALL_ITEMS_JSON_REQ: {

                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;

                            ItemsResProvider itemsResProvider = new ItemsResProvider();
                            List<Story> itemsList = itemsResProvider
                                    .getAllItems(conn);
                            String resultJson = Convector.toJson(Constants.STORIES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case GET_USER_JSON_REQ: {
                            String id = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;
                            conn = ConnPool.getInstance().getConnection();
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");

                            UserResProvider userResProvider = new UserResProvider();
                            User user = userResProvider.getUser(id, conn);

                            if (user != null) {
                                String resultJson = user.toJson();
                                if (resultJson != null && !resultJson.isEmpty()) {
                                    respPage = resultJson;
                                } else {
                                    resp.sendError(404);
                                }
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case GET_ACTIVITY_REPLIES:{
                            String id = req.getParameter(Constants.ID);
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;

                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            List<Reply> itemsList = activitiesResProvidor
                                    .getActvityReplies(Long.valueOf(id), conn);
                            String resultJson = Convector.toJson(Constants.REPLIES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }

                        case GET_CHAPTER_COMMENTS_REQ:{
                            String sTitle = req.getParameter(Constants.STORY_TITLE);
                            String title = req.getParameter(Constants.TITLE);
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;

                            ChapterResProvidor chapterResProvidor = new ChapterResProvidor();
                            List<Comment> itemsList = chapterResProvidor
                                    .getCommentsForChapter(sTitle, title, conn);
                            String resultJson = Convector.toJson(Constants.COMMENTS, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }

                        case DELETE_CHAPTER_REQ: {
                            String sTitle = req.getParameter(Constants.STORY_TITLE);
                            String title = req.getParameter(Constants.TITLE);
                            respPage = RESOURCE_FAIL_TAG;
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            conn = ConnPool.getInstance().getConnection();

                            ChapterResProvidor chapterResProvidor = new ChapterResProvidor();
                            Chapter item = new Chapter(sTitle, title);
                            if (chapterResProvidor.deleteItem(item, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }
                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case DELETE_ITEM_REQ: {
                            String id = req.getParameter(Constants.TITLE);
                            respPage = RESOURCE_FAIL_TAG;
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            conn = ConnPool.getInstance().getConnection();

                            ItemsResProvider itemsResProvider = new ItemsResProvider();
                            Story item = new Story(id);
                            if (itemsResProvider.deleteItem(item, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }
                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case INSERT_ACTIVITY_REQ: {

                            String id = req.getParameter(Constants.ID);
                            String username = req.getParameter(Constants.USERNAME);
                            String message = req.getParameter(Constants.MESSAGE);

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();

                            ActivityEvent chapter = new ActivityEvent(Long.valueOf(id), username, message);
                            ActivitiesResProvidor activitiesResProvidor = new ActivitiesResProvidor();
                            if (activitiesResProvidor.insertActivity(chapter, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case INSERT_CHAPTER_REQ: {

                            String sTitle = req.getParameter(Constants.STORY_TITLE);
                            String title = req.getParameter(Constants.TITLE);
                            String lines = req.getParameter(Constants.LINES);

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();

                            Chapter chapter = new Chapter(sTitle, title);
                            chapter.setLines(Convector.fromStringToLines(lines));

                            ChapterResProvidor chapterResProvidor = new ChapterResProvidor();
                            if (chapterResProvidor.insertChapter(chapter, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case INSERT_MESSAGE_REQ: {

                            String sender = req.getParameter(Constants.SENDER);
                            String receiver = req.getParameter(Constants.USERNAME);
                            String subject = req.getParameter(Constants.SUBJECT);
                            String msg = req.getParameter(Constants.MESSAGE);
                            Long date = Long.valueOf(req.getParameter(Constants.DATE));

                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();

                            Message message = new Message(sender, receiver, subject, msg, date);

                            MessageResProvider messageResProvider = new MessageResProvider();
                            if (messageResProvider.insertMessage(message, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }

                        case GET_ITEM_IMAGE_REQ: {
                            String id = req.getParameter(Constants.TITLE);
                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();
                            ItemsResProvider itemsResProvider = new ItemsResProvider();

                            byte[] imgBlob = itemsResProvider.getImage(id, conn);

                            if (imgBlob != null && imgBlob.length > 0) {
                                ServletOutputStream os = resp.getOutputStream();
                                os.write(imgBlob);
                            } else {
                                resp.sendError(404);
                            }

                            retry = 0;
                            break;
                        }

                        case GET_USER_IMAGE_REQ: {
                            String id = req.getParameter(Constants.USERNAME);
                            respPage = RESOURCE_FAIL_TAG;

                            conn = ConnPool.getInstance().getConnection();
                            UserResProvider userResProvider = new UserResProvider();

                            byte[] imgBlob = userResProvider.getImage(id, conn);

                            if (imgBlob != null && imgBlob.length > 0) {
                                ServletOutputStream os = resp.getOutputStream();
                                os.write(imgBlob);
                            } else {
                                resp.sendError(404);
                            }

                            retry = 0;
                            break;
                        }
                        case GET_SENT_MESSAGES_JSON_REQ: {
                            String id = req.getParameter(Constants.USERNAME);
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;

                            MessageResProvider messageResProvider = new MessageResProvider();
                            List<Message> itemsList = messageResProvider
                                    .getSentMessages(id, conn);
                            String resultJson = Convector.toJson(Constants.MESSAGES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case GET_RECIEVED_MESSAGES_JSON_REQ: {
                            String id = req.getParameter(Constants.USERNAME);
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;

                            MessageResProvider messageResProvider = new MessageResProvider();
                            List<Message> itemsList = messageResProvider
                                    .getRecivedMessages(id, conn);
                            String resultJson = Convector.toJson(Constants.MESSAGES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        case ADD_TO_CURRENT_READ: {
                            String id = req.getParameter(Constants.TITLE);
                            String username = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            conn = ConnPool.getInstance().getConnection();
                            ItemsResProvider itemsResProvider = new ItemsResProvider();

                            if (itemsResProvider.insertReadingr(username, id, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }
                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case INSERT_ITEM_FAVE_REQ: {
                            String id = req.getParameter(Constants.TITLE);
                            String username = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            conn = ConnPool.getInstance().getConnection();
                            ItemsResProvider itemsResProvider = new ItemsResProvider();

                            if (itemsResProvider.insertFaveItem(id, username, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }
                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case REMOVE_FAVE_ITEM: {
                            String id = req.getParameter(Constants.TITLE);
                            String username = req.getParameter(Constants.USERNAME);

                            respPage = RESOURCE_FAIL_TAG;
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            conn = ConnPool.getInstance().getConnection();
                            ItemsResProvider itemsResProvider = new ItemsResProvider();

                            if (itemsResProvider.removeFaveItem(id, username, conn)) {
                                respPage = RESOURCE_SUCCESS_TAG;
                            }
                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);

                            retry = 0;
                            break;
                        }
                        case GET_FAVE_ITEM_USER_REQ: {

                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;

                            String username = req.getParameter(Constants.USERNAME);

                            ItemsResProvider itemsResProvider = new ItemsResProvider();
                            List<Story> itemsList = itemsResProvider
                                    .getfAVEItemForUser(username, conn);
                            String resultJson = Convector.toJson(Constants.STORIES, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
					/*case GET_SOLD_ITEM_USER_REQ: {

						conn = ConnPool.getInstance().getConnection();
						respPage = RESOURCE_FAIL_TAG;

						String username = req.getParameter(Constants.USERNAME);

						ItemsResProvider itemsResProvider = new ItemsResProvider();
						List<Item> itemsList = itemsResProvider
								.getSoldItemForUser(username, conn);
						String resultJson = Item.toJson(itemsList);
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						if (resultJson != null && !resultJson.isEmpty()) {
							respPage = resultJson;

						} else {
							resp.sendError(404);
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);
						retry = 0;
						break;
					}*/
                        case GET_NOTIFICATIONS_JSON_REQ: {
                            String id = req.getParameter(Constants.USERNAME);
                            conn = ConnPool.getInstance().getConnection();
                            respPage = RESOURCE_FAIL_TAG;
                            NotificationResProvider notificationResProvider = new NotificationResProvider();
                            List<MyNotification> itemsList = notificationResProvider
                                    .getNotificationsForUser(id, conn);
                            String resultJson = Convector.toJson(Constants.NOTIFICATIONS, itemsList);
                            resp.addHeader("Content-Type",
                                    "application/json; charset=UTF-8");
                            if (resultJson != null && !resultJson.isEmpty()) {
                                respPage = resultJson;

                            } else {
                                resp.sendError(404);
                            }

                            PrintWriter pw = resp.getWriter();
                            pw.write(respPage);
                            retry = 0;
                            break;
                        }
                        // == end items apis

                        default:
                            retry = 0;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    retry--;
                } catch (Throwable t) {
                    t.printStackTrace();
                    retry = 0;
                } finally {
                    if (conn != null) {
                        ConnPool.getInstance().returnConnection(conn);
                    }
                }
            }
        }
    }

}
