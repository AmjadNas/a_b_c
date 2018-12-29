package android.course.a_b_c.Activities;


import android.content.res.Resources;
import android.course.a_b_c.Adapters.CommentsAdapter;
import android.course.a_b_c.Adapters.GridSpacingItemDecoration;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Network.NetworkConnector;
import android.course.a_b_c.Network.NetworkResListener;
import android.course.a_b_c.Network.ResStatus;
import android.course.a_b_c.Objects.Comment;
import android.course.a_b_c.Objects.Reply;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddCommentActivity extends AppCompatActivity implements NetworkResListener {
    private EditText comment;
    private String viewType;
    private List<Reply> replies;
    private List<Comment> comments;
    private CommentsAdapter adapter;
    private String stTtitle, title;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        // display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.include);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));

        viewType = getIntent().getStringExtra(Constants.VIEW_TYPE);
        comment = (EditText)findViewById(R.id.edittext_chatbox);

        if (viewType != null){
            getSupportActionBar().setTitle(viewType);
            if (viewType.equals(Constants.COMMENTS)){
                 stTtitle = getIntent().getStringExtra(Constants.STORY_TITLE);
                 title = getIntent().getStringExtra(Constants.TITLE);
                 NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_CHAPTER_COMMENTS_REQ,
                         DataHandler.getInstance().getChapter(stTtitle, title), null, this);
                comments = new ArrayList<>();

                adapter = new CommentsAdapter(this, null, comments);
                recyclerView.setAdapter(adapter);

            }else if (viewType.equals(Constants.REPLIES)){
                 id = getIntent().getLongExtra(Constants.ID, -1);
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_ACTIVITY_REPLIES,
                        DataHandler.getInstance().getActivityByID(id), this);
                replies = new ArrayList<>();

                adapter = new CommentsAdapter(this, replies, null);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addMassage(View view) {
        String txtComment = comment.getText().toString();

        if (!txtComment.isEmpty()) {
            if (viewType.equals(Constants.COMMENTS)) {
                if (DataHandler.getInstance().insertComment(stTtitle, title, txtComment)) {
                    comments.add(0, new Comment(title, stTtitle, DataHandler.getInstance().getUser().getUsername(), comment.getText().toString()));
                    adapter.notifyItemInserted(0);
                    setResult(RESULT_OK);
                }

            } else if (viewType.equals(Constants.REPLIES)) {

                if (DataHandler.getInstance().insertReply(id, txtComment)) {
                    replies.add(0, new Reply(id, DataHandler.getInstance().getUser().getUsername(), comment.getText().toString()));
                    adapter.notifyItemInserted(0);
                }
            }
            comment.setText("");
        }else
            Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onPreUpdate() {

    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        if (status == ResStatus.SUCCESS) {
            if (viewType.equals(Constants.COMMENTS))
                DataHandler.getInstance().parseCommentss(adapter, comments, res);
            else if (viewType.equals(Constants.REPLIES)){
                DataHandler.getInstance().parseReplies(adapter, replies, res);
            }
        }else {
            if (viewType.equals(Constants.COMMENTS)) {
                comments = DataHandler.getInstance().getChapterComments(stTtitle, title);
                adapter.setComments(comments);
            }
            else if (viewType.equals(Constants.REPLIES)){
                replies = DataHandler.getInstance().getActivityRepliesByID(id);
                adapter.setReplies(replies);
            }
        }
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }
}
