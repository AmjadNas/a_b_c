package android.course.a_b_c.Activities;

import android.content.res.Resources;
import android.course.a_b_c.Adapters.GridSpacingItemDecoration;
import android.course.a_b_c.Adapters.StoriesAdapter;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Fragments.DiscoverFragments.StoriesFragment;
import android.course.a_b_c.Fragments.TabbedFragment;
import android.course.a_b_c.Network.NetworkConnector;
import android.course.a_b_c.Network.NetworkResListener;
import android.course.a_b_c.Network.ResStatus;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.ArrayList;
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
