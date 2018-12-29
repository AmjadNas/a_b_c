package android.course.a_b_c.Activities;

import android.content.res.Resources;
import android.course.a_b_c.Adapters.GridSpacingItemDecoration;
import android.course.a_b_c.Adapters.StoriesAdapter;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.course.a_b_c.Fragments.ChaptersFragment.LIST;

public class SearchActivity extends AppCompatActivity {
    private EditText searchBox;
    private StoriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchBox = (EditText) findViewById(R.id.searchBox_sa) ;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.include);
        adapter = new StoriesAdapter(this, null, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void search(View view) {
        String txtSearch = searchBox.getText().toString();

        if (!txtSearch.isEmpty())
            performSearch(txtSearch);
        else
            Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
    }

    private void performSearch(String txtSearch) {
        List<Story> stories = DataHandler.getInstance().getStoriesBy(Constants.TAGS, Constants.TAG, Constants.STORY_TITLE, txtSearch, null, false);

        HashSet<Story> set = new HashSet<>(stories);

        stories = DataHandler.getInstance().getStoriesBy(Constants.STORY, Constants.TITLE, Constants.TITLE, txtSearch, null, true);

        set.addAll(stories);
        adapter.setStories(new ArrayList<>(set));
    }
}
