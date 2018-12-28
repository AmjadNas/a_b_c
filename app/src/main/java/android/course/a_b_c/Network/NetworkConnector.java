package android.course.a_b_c.Network;

import android.content.Context;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Objects.ActivityEvent;
import android.course.a_b_c.Objects.Chapter;
import android.course.a_b_c.Objects.Comment;
import android.course.a_b_c.Objects.Message;
import android.course.a_b_c.Objects.Reply;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.Objects.User;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.Converter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NetworkConnector {

    private static NetworkConnector mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    // server address
    private final String PORT = "8080";
    private final String IP = "192.168.0.108";
    private final String HOST_URL = "http://" + IP + ":" + PORT +"/";
    private final String BASE_URL = HOST_URL + "projres";

    // server requests

    public static final String GET_ALL_ITEMS_JSON_REQ = "0";
    public static final String INSERT_ITEM_REQ = "1";
    public static final String INSERT_CHAPTER_REQ = "2";
    public static final String INSERT_ACTIVITY_REQ ="3";
    public static final String INSERT_MESSAGE_REQ = "4";
    public static final String INSERT_ITEM_FAVE_REQ = "5";
    public static final String DELETE_ITEM_REQ = "6";
    public static final String REMOVE_FAVE_ITEM = "7";
    public static final String GET_ITEM_IMAGE_REQ = "8";
    public static final String GET_RECIEVED_MESSAGES_JSON_REQ = "9";
    public static final String GET_NEWEST ="10" ;
    public static final String GET_SENT_MESSAGES_JSON_REQ = "11";
    public static final String GET_USER_ACTIVITIES = "12";
    public static final String DELETE_CHAPTER_REQ = "13";
    public static final String GET_CHAPTER_COMMENTS_REQ = "14";
    public static final String GET_ACTIVITY_REPLIES = "15";
    public static final String GET_FAVE_ITEM_USER_REQ = "16";
    public static final String GET_NOTIFICATIONS_JSON_REQ = "17";
    public static final String DELETE_USER_REQ = "18";
    public static final String GET_USER_IMAGE_REQ = "19";
    public static final String GET_USER_JSON_REQ = "20";
    public static final String INSERT_USER_REQ = "21";
    public static final String INSERT_USER_IMAGE_REQ = "22";
    public static final String GET_ITEM_USER_REQ = "23";
    public static final String FOLLOW_USER = "24";
    public static final String UNFOLLOW_USER = "25";
    public static final String INSERT_COMMENT = "26";
    public static final String INSERT_REPLY = "27";
    public static final String GET_USER_FOLLOWERS = "28";
    public static final String GET_USER_FOLLOWING = "29";
    public static final String ADD_TO_CURRENT_READ = "30";
    public static final String GET_CUURENT_READING = "31";
    public static final String GET_STORY_CHAPTERS = "32";
    public static final String DELETE_MESSAGE_REQ = "33";

    private String tempReq;
    private static final String RESOURCE_FAIL_TAG = "{\"result_code\":0}";
    private static final String RESOURCE_SUCCESS_TAG = "{\"result_code\":1}";

    public static final String REQ = "req";


    private NetworkConnector() {

    }

    public static synchronized NetworkConnector getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkConnector();
        }
        return mInstance;
    }

    public void initialize(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    private void addToRequestQueue(String query, final String request, final NetworkResListener listener) {

        String reqUrl = BASE_URL + "?" + query;
        notifyPreUpdateListeners(listener);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, reqUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        notifyPostUpdateListeners(response, ResStatus.SUCCESS, request, listener);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        JSONObject err = null;
                        try {
                            err = new JSONObject(RESOURCE_FAIL_TAG);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyPostUpdateListeners(err, ResStatus.FAIL, request, listener);
                        }

                    }
                });

        getRequestQueue().add(jsObjRequest);
    }

    private void addImageRequestToQueue(String query, final NetworkResListener listener){

        String reqUrl = BASE_URL + "?" + query;

        notifyPreUpdateListeners(listener);

        getImageLoader().get(reqUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                notifyPostBitmapUpdateListeners(bm, ResStatus.SUCCESS, listener);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                notifyPostBitmapUpdateListeners(null, ResStatus.FAIL, listener);
            }
        });
    }

    private ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void sendRequestToServer(String requestCode, String username, String follower, NetworkResListener listener){

        if(username==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case FOLLOW_USER: case UNFOLLOW_USER:{
                builder.appendQueryParameter(REQ , requestCode);

                builder.appendQueryParameter(Constants.USERNAME , username);
                builder.appendQueryParameter(Constants.FOLLOWER , follower);

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);

                break;
            }
        }
    }

    private void uploadItemImage(final Story item, final NetworkResListener listener) {

        String reqUrl = HOST_URL + "web_item_manage?";
        notifyPreUpdateListeners(listener);


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, reqUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            notifyPostUpdateListeners(obj, ResStatus.SUCCESS, INSERT_ITEM_REQ, listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(RESOURCE_FAIL_TAG );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyPostUpdateListeners(obj, ResStatus.FAIL, INSERT_ITEM_REQ, listener);
                        }

                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.TITLE, item.getTitle());
                params.put(Constants.AUTHOR, item.getAuthor());
                params.put(Constants.LANGUAGE,  item.getLanguage());
                params.put(Constants.SYNOPSIS, item.getSynopsis());
                params.put(Constants.LIKES, String.valueOf(item.getLikes()));
                params.put(Constants.RATING, item.getRated());
                params.put(Constants.TAGS, item.getTagsString());
                params.put(Constants.CHARACTERS, item.getCharactersString());
                params.put(Constants.CATEGORIES, item.getCategoryString());
                params.put(Constants.GENRES, item.getGenreString());
                params.put(Constants.STATUS, String.valueOf(item.isStatus()));
                params.put(Constants.DATE, String.valueOf(item.getDateFormat()));
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                Bitmap img = DataHandler.getInstance().getStoryCover(item.getTitle());
                if (img != null) {
                    long imagename = System.currentTimeMillis();
                    byte[] pic = Converter.getBitmapAsByteArray(img);
                    params.put("fileField", new DataPart(imagename + ".png", pic));
                }
                return params;
            }
        };

        //adding the request to volley
        getRequestQueue().add(volleyMultipartRequest);
    }

    public void sendRequestToServer(String requestCode, ActivityEvent data, NetworkResListener listener){

        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_ACTIVITY_REQ:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.ID , String.valueOf(data.getId()));
                builder.appendQueryParameter(Constants.USERNAME , data.getUsername());
                builder.appendQueryParameter(Constants.MESSAGE , data.getMessage());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);

                break;
            }
            case GET_ACTIVITY_REPLIES:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.ID , String.valueOf(data.getId()));

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);
                break;
            }
        }
    }

    public void sendRequestToServer(String requestCode, Reply data, NetworkResListener listener){
        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_REPLY:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.ID , String.valueOf(data.getActID()));
                builder.appendQueryParameter(Constants.USERNAME , data.getUsername());
                builder.appendQueryParameter(Constants.MESSAGE , data.getMessage());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);

                break;
            }
        }
    }

    public void sendRequestToServer(String requestCode, Comment data, NetworkResListener listener){
        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_COMMENT:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.STORY_TITLE , data.getStoryTiele());
                builder.appendQueryParameter(Constants.TITLE , data.getChapterTitle());
                builder.appendQueryParameter(Constants.USERNAME , data.getUsername());
                builder.appendQueryParameter(Constants.MESSAGE , data.getComment());
                builder.appendQueryParameter(Constants.DATE , String.valueOf(data.getDateFormat()));

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);

                break;
            }
        }
    }

    public void sendRequestToServer(String requestCode, Chapter data, String lines, NetworkResListener listener){

        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_CHAPTER_REQ:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.STORY_TITLE , data.getStoryTitle());
                builder.appendQueryParameter(Constants.TITLE , data.getTitle());
                builder.appendQueryParameter(Constants.LINES , lines);

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);

                break;
            }
            case DELETE_CHAPTER_REQ:case GET_CHAPTER_COMMENTS_REQ:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.STORY_TITLE , data.getStoryTitle());
                builder.appendQueryParameter(Constants.TITLE , data.getTitle());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);
                break;
            }
        }
    }

    public void sendRequestToServer(String requestCode, Story data, NetworkResListener listener){
        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_ITEM_REQ:{

                uploadItemImage(data, listener);

                break;
            }
            case DELETE_ITEM_REQ: case GET_STORY_CHAPTERS:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.TITLE , data.getTitle());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);
                break;
            }
            case GET_ITEM_IMAGE_REQ: {

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.TITLE , data.getTitle());

                String query = builder.build().getEncodedQuery();
                addImageRequestToQueue(query, listener);
                break;
            }

            case ADD_TO_CURRENT_READ: case REMOVE_FAVE_ITEM: case INSERT_ITEM_FAVE_REQ:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.TITLE , data.getTitle());
                builder.appendQueryParameter(Constants.USERNAME , DataHandler.getInstance().getUser().getUsername());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);
                break;
            }
        }
    }


    public void update(NetworkResListener listener, String req){

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter(REQ , req);
        String query = builder.build().getEncodedQuery();

        addToRequestQueue(query, req ,listener);
    }

    /**
     * used for chat upload
     * @param requestCode
     * @param data
     * @param listener
     */
    public void sendRequestToServer(String requestCode, Message data, NetworkResListener listener) {

        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_MESSAGE_REQ: case DELETE_MESSAGE_REQ:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.SENDER , data.getSender());
                builder.appendQueryParameter(Constants.USERNAME , data.getUsername());
                builder.appendQueryParameter(Constants.SUBJECT , data.getSubject());
                builder.appendQueryParameter(Constants.MESSAGE , data.getMessage());
                builder.appendQueryParameter(Constants.DATE , String.valueOf(data.getDateFormat()));

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);
                break;
            }
        }
    }

    /**
     * used for register user
     * @param requestCode
     * @param data
     * @param listener
     */
    public void sendRequestToServer(String requestCode, User data, NetworkResListener listener) {

        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_USER_REQ:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.USERNAME ,data.getUsername());
                builder.appendQueryParameter(Constants.FNAME ,data.getFirstName());
                builder.appendQueryParameter(Constants.LNAME ,data.getLastName());
                builder.appendQueryParameter(Constants.EMAIL ,data.getEmail());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);
                break;
            }
            case INSERT_USER_IMAGE_REQ: {
                uploadUserImage(data, listener, requestCode);
                break;
            }
            case GET_USER_IMAGE_REQ:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.USERNAME , data.getUsername());

                String query = builder.build().getEncodedQuery();
                addImageRequestToQueue(query, listener);
                break;
            }
            case GET_ITEM_USER_REQ:
            case DELETE_USER_REQ:
            case GET_CUURENT_READING:
            case GET_SENT_MESSAGES_JSON_REQ:
            case GET_FAVE_ITEM_USER_REQ:
            case GET_RECIEVED_MESSAGES_JSON_REQ:
            case GET_NOTIFICATIONS_JSON_REQ:
            case GET_USER_ACTIVITIES:
            case GET_USER_FOLLOWERS:
            case GET_USER_FOLLOWING:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.USERNAME ,data.getUsername());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, requestCode ,listener);
                break;
            }
        }
    }

    private void uploadUserImage(final User item, final NetworkResListener listener, final String request) {

        String reqUrl = HOST_URL + "web_user_manage?";
        notifyPreUpdateListeners(listener);


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, reqUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            notifyPostUpdateListeners(obj, ResStatus.SUCCESS, request, listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(RESOURCE_FAIL_TAG );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyPostUpdateListeners(obj, ResStatus.FAIL, request, listener);
                        }

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.USERNAME ,item.getUsername());
                params.put(Constants.FNAME ,item.getFirstName());
                params.put(Constants.LNAME ,item.getLastName());
                params.put(Constants.EMAIL ,item.getEmail());
                return params;
            }
            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (item.getThumb() != null) {
                    byte[] pic = Converter.getBitmapAsByteArray(item.getThumb());
                    params.put("fileField", new DataPart(imagename + ".png", pic));
                }
                return params;
            }
        };

        //adding the request to volley
        getRequestQueue().add(volleyMultipartRequest);
    }

    private void clean() {

    }


    private  void notifyPostBitmapUpdateListeners(final Bitmap res, final ResStatus status, final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onPostUpdate(res, status);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }

    private  void notifyPostUpdateListeners(final JSONObject res, final ResStatus status,final String request, final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onPostUpdate(res, request, status);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }

    private void notifyPreUpdateListeners(final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                        listener.onPreUpdate();
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }
}
