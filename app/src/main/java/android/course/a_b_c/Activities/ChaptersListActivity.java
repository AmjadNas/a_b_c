package android.course.a_b_c.Activities;

import android.content.Intent;
import android.course.a_b_c.Fragments.ChaptersFragment;
import android.course.a_b_c.Objects.Chapter;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ChaptersListActivity extends AppCompatActivity implements ChaptersFragment.OnFragmentInteractionListener {

    public static final int CHAPTER_ADDED = -12;
    private ChapterFragmentAdapter adapter;
    private boolean chapteradded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ChapterFragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.mainFragmentTabs);
        tabLayout.setupWithViewPager(pager);

    }

    @Override
    public void onItemDeleted(Chapter c) {

        adapter.getDeleteFragment().addChapterToList(c);
    }

    @Override
    public void chapterAdded(String c) {
        Intent intent = new Intent();
        intent.putExtra(Constants.STORY_TITLE, c);
        setResult(RESULT_OK);

    }

    @Override
    public void chapterChanged(Chapter c) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            getSupportFragmentManager().getFragments().get(0).onActivityResult(requestCode, resultCode, data);
    }

    private class ChapterFragmentAdapter extends FragmentPagerAdapter {
        private final String[] header = {"Chapter LIST", "Deleted Chapters"};
        private final String sTitle;
        private ChaptersFragment list;
        private ChaptersFragment delete;

        public ChapterFragmentAdapter(FragmentManager fm) {
            super(fm);
            sTitle = getIntent().getStringExtra(Constants.STORY_TITLE);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return header[position];
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                 list = ChaptersFragment.newInstance(ChaptersFragment.LIST, sTitle);
                return list;
            }
            else if (i == 1) {
                delete = ChaptersFragment.newInstance(ChaptersFragment.DELETED, null);
                return delete;
            }
            return null;
        }

        public ChaptersFragment getDeleteFragment() {
            return delete;
        }

        @Override
        public int getCount() {
            return header.length;
        }
    }
}
