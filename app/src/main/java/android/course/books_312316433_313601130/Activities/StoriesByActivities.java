package android.course.books_312316433_313601130.Activities;

import android.content.res.Resources;
import android.course.books_312316433_313601130.Adapters.GridSpacingItemDecoration;
import android.course.books_312316433_313601130.Adapters.StoriesAdapter;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Objects.Story;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.List;

public class StoriesByActivities extends AppCompatActivity  {

    private List<Story> stories;
    private StoriesAdapter adapter;
    private ProgressBar progressBar;
    private String type;
    private String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_by_activities);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = toolbar.findViewById(R.id.progressBar2);
        progressBar.setVisibility(ProgressBar.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        type = getIntent().getStringExtra(Constants.VIEW_TYPE);

        label  = getIntent().getStringExtra(Constants.RESOURCE);
        getSupportActionBar().setTitle(label);

        initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        String key;
        //matches the type of filter the stories to be loaded by
        if (type.equals(Constants.CATEGORIES))
            key = Constants.CATEGORY;
        else
            key = Constants.GENRE;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menuList);
        stories = DataHandler.getInstance().getStoriesBy(type, key, label, Constants.STORY_TITLE );
        adapter = new StoriesAdapter(this, stories, false);
        progressBar.setVisibility(ProgressBar.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));

    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
