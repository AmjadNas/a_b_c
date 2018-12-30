package android.course.books_312316433_313601130.Adapters;

import android.content.Context;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Fragments.ActivitiesFragment;
import android.course.books_312316433_313601130.Fragments.DiscoverFragments.ListFragment;
import android.course.books_312316433_313601130.Fragments.DiscoverFragments.StoriesFragment;
import android.course.books_312316433_313601130.Fragments.MessagesFragment;
import android.course.books_312316433_313601130.R;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabbedFragmentsAdapter extends FragmentPagerAdapter {

    private final String username;
    private String[] header;
    private int navLocation;


    public TabbedFragmentsAdapter(Context context, int num, FragmentManager fm) {
        super(fm);
        navLocation = num;
        switch (num){
            case R.id.navigation_home:
                header = context.getResources().getStringArray(R.array.discoverHeader);
                break;
            case R.id.navigation_library:
                header = context.getResources().getStringArray(R.array.libraryHeader);
                break;
            case R.id.navigation_mStories:
                header = context.getResources().getStringArray(R.array.myStoriesHeader);
                break;
            case R.id.navigation_activities:
                header = context.getResources().getStringArray(R.array.mActivitiesHeader);
                break;
            case R.id.navigation_messages:
                header = context.getResources().getStringArray(R.array.messagesHeader);
                break;
                default:
                    header = new String[0];
                break;
        }
        username = DataHandler.getInstance().getUser().getUsername();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return header[position];
    }

    @Override
    public Fragment getItem(int i) {
        switch (navLocation){
            case R.id.navigation_home:
                return getDiscoverFragment(i);
            case R.id.navigation_library:
                return getLibraryFragment(i);
            case R.id.navigation_mStories:
                return getMyStoriesFragment(i);
            case R.id.navigation_activities:
                return new ActivitiesFragment();
            case R.id.navigation_messages:
                return getMessagesFragment(i);
            default:
                return null;
        }
    }



    @Override
    public int getCount() {
        return header.length;
    }

    private Fragment getDiscoverFragment(int i){
        switch (i){
            case 0: case 3:
                return ListFragment.newInstance(i);
            case 1:
                return StoriesFragment.newInstance(StoriesFragment.RECOMMENDED_TAG, username);
            case 2:
                return StoriesFragment.newInstance(StoriesFragment.NEWEST_TAG, username);
        }
        return null;
    }

    private Fragment getLibraryFragment(int i) {
        switch (i){
            case 0:
                return StoriesFragment.newInstance(StoriesFragment.CURRENT_READ, username);
        }
        return null;
    }

    private Fragment getMessagesFragment(int i) {
        switch (i){
            case 0:
                return MessagesFragment.newInstance(MessagesFragment.INBOX);
            case 1:
                return MessagesFragment.newInstance(MessagesFragment.SENT);
            case 2:
                return MessagesFragment.newInstance(MessagesFragment.TRASH);
        }
        return null;
    }

    private Fragment getMyStoriesFragment(int i) {
        switch (i){
            case 0:
                return StoriesFragment.newInstance(StoriesFragment.MY_STORIES_TAG, username);
            case 1:
                return StoriesFragment.newInstance(StoriesFragment.DELETED_TAG, username);
        }
        return null;
    }

}
