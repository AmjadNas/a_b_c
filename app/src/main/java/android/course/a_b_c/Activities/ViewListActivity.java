package android.course.a_b_c.Activities;

import android.content.Intent;
import android.course.a_b_c.Adapters.SimpleListAdapter;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Objects.Chapter;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.Converter;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ViewListActivity extends AppCompatActivity  {

    private SimpleListAdapter adapter;
    private Chapter c;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String[] arr = null;

        if (intent != null){
             i = intent.getIntExtra(Constants.VIEW_TYPE, -2);
            int resource = intent.getIntExtra(Constants.RESOURCE, -2);

            if (resource == Constants.STORY_LINES) {

                String storyTitle = intent.getStringExtra(Constants.STORY_TITLE);
                String title = intent.getStringExtra(Constants.TITLE);
                 c = DataHandler.getInstance().getChapter(storyTitle, title);
                getSupportActionBar().setTitle(c.getTitle());
                if (c != null)
                    arr = (String[]) c.getLines().toArray();

            }else if(R.array.storyCategory == resource || R.array.storyGenre == resource){
                arr = getResources().getStringArray(resource);
                if (R.array.storyCategory == resource)
                    getSupportActionBar().setTitle(R.string.add_categories);
                else
                    getSupportActionBar().setTitle(R.string.add_genres);

            }

            if (i > -2){
                adapter = new SimpleListAdapter(this, i, arr);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menuList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            }
        }else
            finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (i == Constants.STORY_LINES_VIEW_TYPE)
            getMenuInflater().inflate(R.menu.social_options, menu);
        else
            getMenuInflater().inflate(R.menu.mymenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                Intent intent = new Intent();
                String s = Converter.fromListToString(adapter.getStrs());
                intent.putExtra(Constants.RESULT, s);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.item_like_mn:
                if (!DataHandler.getInstance().userLikes(c.getStoryTitle())) {
                    DataHandler.getInstance().LikeStory(c.getStoryTitle(), true);
                    Toast.makeText(this, R.string.addedTovaourites, Toast.LENGTH_SHORT).show();
                } else {
                    DataHandler.getInstance().unLikeStory(c.getStoryTitle(), true);
                    Toast.makeText(this, R.string.removedFromFavourites, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.item_comment_mn:
                launchActivity();
                return true;
            case R.id.item_share_mn:
                return true;
            case android.R.id.home:
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == Constants.COMMENT_CHAPTER) {
                Intent intent = new Intent();
                intent.putExtra(Constants.STORY_TITLE, c.getStoryTitle());
                intent.putExtra(Constants.TITLE, c.getTitle());
                setResult(RESULT_OK, intent);
            }
        }
    }

    private void shareChapter() {
    }

    private void launchActivity(){
        if (c != null) {
            Intent intent = new Intent(this, AddCommentActivity.class);
            intent.putExtra(Constants.VIEW_TYPE, Constants.COMMENTS);
            intent.putExtra(Constants.STORY_TITLE, c.getStoryTitle());
            intent.putExtra(Constants.TITLE, c.getTitle());
            startActivityForResult(intent, Constants.COMMENT_CHAPTER);
        }
    }
}
