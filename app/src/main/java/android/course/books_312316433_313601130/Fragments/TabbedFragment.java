package android.course.books_312316433_313601130.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.course.books_312316433_313601130.Adapters.MessagesAdpater;
import android.course.books_312316433_313601130.Adapters.TabbedFragmentsAdapter;
import android.course.books_312316433_313601130.Adapters.StoriesAdapter;
import android.course.books_312316433_313601130.Fragments.DiscoverFragments.StoriesFragment;
import android.course.books_312316433_313601130.Objects.Message;
import android.course.books_312316433_313601130.Objects.Story;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.course.books_312316433_313601130.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabbedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabbedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabbedFragment extends Fragment implements ViewPager.OnPageChangeListener, StoriesAdapter.StoryAdapterListener, MessagesAdpater.MessageAdapterListener, Refreshable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
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
           // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabbed, container, false);

        //initPager(view);
        initPager(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initPager(View view) {
        act = getContext();
        ViewPager pager = view.findViewById(R.id.viewPager);
        adapter = new TabbedFragmentsAdapter(act, mParam1, getChildFragmentManager());
        pager.setAdapter(adapter);

        TabLayout tabs = getActivity().findViewById(R.id.mainFragmentTabs);
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //act = context;
       /* if (context instanceof OnFragmentInteractionListener) {
            act = context;
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        Fragment fragment = getChildFragmentManager().getFragments().get(page);

        if (fragment instanceof  Refreshable)
            ((Refreshable)getChildFragmentManager().getFragments().get(page)).refresh();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
