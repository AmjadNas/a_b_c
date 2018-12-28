package android.course.a_b_c.Adapters;

import android.content.Context;
import android.course.a_b_c.Fragments.DiscoverFragments.StoriesFragment;
import android.course.a_b_c.R;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ProfileFragmentsAdapter extends FragmentPagerAdapter {

    private final String[] header;
    private final String username;

    public ProfileFragmentsAdapter(Context context, FragmentManager fm, String username){
        super(fm);
        this.username = username;
        header = context.getResources().getStringArray(R.array.profileHeaders);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return StoriesFragment.newInstance(StoriesFragment.PROFILE_STORIES, username);
            case 1:
                return StoriesFragment.newInstance(StoriesFragment.FAVOURITES, username);
            case 2:
                return StoriesFragment.newInstance(StoriesFragment.CURRENT_READ, username);

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return header.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return header[position];
    }
}
