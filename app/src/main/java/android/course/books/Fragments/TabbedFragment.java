package android.course.books.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.course.books.Adapters.MessagesAdapter;
import android.course.books.Adapters.TabbedFragmentsAdapter;
import android.course.books.Adapters.StoriesAdapter;
import android.course.books.Fragments.DiscoverFragments.StoriesFragment;
import android.course.books.Fragments.Listeners.Refreshable;
import android.course.books.Objects.Message;
import android.course.books.Objects.Story;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.course.books.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabbedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabbedFragment extends Fragment implements ViewPager.OnPageChangeListener, StoriesAdapter.StoryAdapterListener, MessagesAdapter.MessageAdapterListener, Refreshable {

    private static final String ARG_PARAM1 = "param1";

    private int mParam1;
    private Context act;
    private TabbedFragmentsAdapter adapter;
    private int page;

    public TabbedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment TabbedFragment.
     */
    public static TabbedFragment newInstance(int param1) {
        TabbedFragment fragment = new TabbedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabbed, container, false);

        initPager(view);
        return view;
    }

    private void initPager(View view) {
        act = getContext();
        ViewPager pager = view.findViewById(R.id.viewPager);
        adapter = new TabbedFragmentsAdapter(act, mParam1, getChildFragmentManager());
        pager.setAdapter(adapter);
        // get the TabLayout from the parent activity and bind it according to the fragment's view pager
        TabLayout tabs = getActivity().findViewById(R.id.mainFragmentTabs);
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(this);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // call onActivityResult for the children fragments to handle requests manually
        // since they the system doesn't do that
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){

            switch (mParam1){
                case R.id.navigation_home:
                    getChildFragmentManager().getFragments().get(2).onActivityResult(
                            requestCode, resultCode, data);
                    getChildFragmentManager().getFragments().get(1).onActivityResult(
                            requestCode, resultCode, data);
                    break;
                case R.id.navigation_mStories: case R.id.navigation_activities:
                    getChildFragmentManager().getFragments().get(0).onActivityResult(
                            requestCode, resultCode, data);
                    break;
                case R.id.navigation_messages:
                    getChildFragmentManager().getFragments().get(1).onActivityResult(
                            requestCode, resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        page = i;

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void OnStoryDeleted(Story s) {
        if (mParam1 == R.id.navigation_mStories)
            ((StoriesFragment)getChildFragmentManager().getFragments().get(1)).addToDeletedList(s);

    }

    @Override
    public void OnMessageDeleted(Message message) {
        if (mParam1 == R.id.navigation_messages)
            ((MessagesFragment)getChildFragmentManager().getFragments().get(2)).addToDeletedList(message);
    }

    @Override
    public void refresh() {
        // get the current displayed fragment check if it implements the Refreshable interface
        // and invoke the refresh method
        Fragment fragment = getChildFragmentManager().getFragments().get(page);

        if (fragment instanceof  Refreshable)
            ((Refreshable)getChildFragmentManager().getFragments().get(page)).refresh();
    }
}
