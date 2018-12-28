package android.course.a_b_c.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.course.a_b_c.Adapters.ChapterAdpater;
import android.course.a_b_c.Adapters.GridSpacingItemDecoration;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Objects.Chapter;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.Converter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewStoryActivity extends AppCompatActivity {

    private TextView title, synopsis, completed, rated, language, likes,
            comments, length, published,
            author;
    private ImageView cover;
    private Story story;
    private TextView tags, categories, genres;
    private List<Chapter> cList;
    private ChapterAdpater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        if (intent == null)
            finish();
        String index = intent.getStringExtra(Constants.RESOURCE);
        if (index == null)
            finish();
        else {
            initViews();
            bindViews(index);
            initCollapsingToolbar();
            initRecyclerView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                shareItem();
                return true;
            case R.id.action_fave:
                if (DataHandler.getInstance().userLikes(story.getTitle())) {
                    if (DataHandler.getInstance().unLikeStory(story.getTitle(), true))
                        story.decreaseLikes();
                }
                else {
                    if (DataHandler.getInstance().LikeStory(story.getTitle(), true))
                        story.increaseLikes();
                }
                likes.setText(String.valueOf(story.getLikes()));

                return true;
            case R.id.actiod_addto_current:
                DataHandler.getInstance().addToCurrentReading(story.getTitle(), true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareItem() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_TEXT, " Story title: " + story.getTitle() +
                "\n Author: " + story.getAuthor() +
                "\n Synopsis: " + story.getSynopsis());
        Bitmap img = DataHandler.getInstance().getStoryCover(story.getTitle());
        if (img != null) {
            if (Converter.saveBitmap(this, story.getTitle(), img)) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                File imagePath = new File(getFilesDir(), "images");
                File newFile = new File(imagePath, story.getTitle()+".png");
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "android.course.a_b_c.fileprovider", newFile);
                intent.setDataAndType(uri, getContentResolver().getType(uri));
                //intent.setData(uri);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }
        intent.setType("*/*");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));

    }

    private void initViews() {

        title = (TextView) findViewById(R.id.txt_title_vs);
        author = (TextView) findViewById(R.id.wt_by_vs);
        published = (TextView) findViewById(R.id.pub_vs);
        synopsis = (TextView) findViewById(R.id.synop_vs);
        completed = (TextView) findViewById(R.id.comp_vs);
        language = (TextView) findViewById(R.id.lang_vs);
        likes = (TextView) findViewById(R.id.lks_vs);
        comments = (TextView) findViewById(R.id.cmt_vs);
        length = (TextView) findViewById(R.id.len_vs);
        rated = (TextView) findViewById(R.id.rate_vs);
        cover = (ImageView) findViewById(R.id.backdrop);
        tags = (TextView) findViewById(R.id.tags_txt_st_view);
        categories = (TextView) findViewById(R.id.txt_cates_st_view);
        genres = (TextView) findViewById(R.id.txt_genres_st_view);
    }

    private void bindViews(String index){
        story =  DataHandler.getInstance().getStoryByTitle(index);
        FloatingActionButton fab = findViewById(R.id.fab);

        if (story.getAuthor().equals(DataHandler.getInstance().getUser().getUsername()))
            fab.hide();
        else
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataHandler.getInstance().addToCurrentReading(story.getTitle(), true);
                }
            });

        title.setText(story.getTitle());
        author.setText(story.getAuthor());
        published.setText(story.getDate());
        synopsis.setText(story.getSynopsis());
        completed.setText(story.getCompletedStatues());
        language.setText(story.getLanguage());
        likes.setText(String.valueOf(story.getLikes()));
        length.setText(String.valueOf(story.getWrodCount()));
        int cmts = DataHandler.getInstance().getCommentsCountForStory(story.getTitle());
        comments.setText(String.valueOf(cmts));
        rated.setText(story.getRated());
        Bitmap img = DataHandler.getInstance().getStoryCover(story.getTitle());
        cover.setImageBitmap(img);
        tags.setText(story.getTagsString());
        categories.setText(story.getCategoryString());
        genres.setText(story.getGenreString());
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menuList);
        cList = DataHandler.getInstance().getChaptersBytStoryTitle(story.getTitle());
        adapter = new ChapterAdpater(this, cList, null, false);
        adapter.setIsView(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }
    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(story.getTitle());
                    isShow = true;

                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.COMMENT_CHAPTER){
            String sTitle = data.getStringExtra(Constants.STORY_TITLE);
            String title = data.getStringExtra(Constants.TITLE);
            int index = cList.indexOf(new Chapter(sTitle,title));
            int cmts = DataHandler.getInstance().getCommentsCountForStory(story.getTitle());
            Chapter c = DataHandler.getInstance().getChapter(sTitle, title);

            cList.remove(index);
            cList.add(index, c);
            comments.setText(String.valueOf(cmts));
            adapter.notifyItemChanged(index);
        }
    }
}
