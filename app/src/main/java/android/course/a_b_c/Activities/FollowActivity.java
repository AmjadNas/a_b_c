package android.course.a_b_c.Activities;

import android.course.a_b_c.Fragments.FollowFragment;
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

public class FollowActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters_list);
        username = getIntent().getStringExtra(Constants.USERNAME);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        TabLayout tabs = (TabLayout) findViewById(R.id.mainFragmentTabs);
        tabs.setupWithViewPager(viewPager);
    }

    private class PageAdapter extends FragmentPagerAdapter {
        private final String[] header = {"Following", "Followers"};

        public PageAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0)
                return FollowFragment.newInstance(FollowFragment.FOLLOWING, username);
            else if (i == 1)
                return FollowFragment.newInstance(FollowFragment.FOLLOWERS, username);
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return header[position];
        }

        @Override
        public int getCount() {
            return header.length;
        }
    }
}