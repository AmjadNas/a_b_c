package android.course.books_312316433_313601130.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.course.books_312316433_313601130.Adapters.FollowersAdapter;
import android.course.books_312316433_313601130.Adapters.GridSpacingItemDecoration;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Fragments.Listeners.DataLoadingListener;
import android.course.books_312316433_313601130.Network.NetworkResListener;
import android.course.books_312316433_313601130.Network.ResStatus;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FollowFragment extends Fragment {
    /**
     * fragment type tags
     */
    public static final String FOLLOWERS = Constants.FOLLOWERS;
    public static final String FOLLOWING = Constants.FOLLOWING;

    private String tag;
    private List<String> list;
    private FollowersAdapter adapter;
    private String username;
    private DataLoadingListener mListener;

    public FollowFragment(){}

    /**
     * factory method to initialize fragments
     * @param tag fragment type tag
     * @param username
     * @return a new instance of FollowFragment
     */
    public static FollowFragment newInstance(String tag, String username){
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putString(Constants.TAG, tag);
        args.putString(Constants.USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            tag = getArguments().getString(Constants.TAG);
            username = getArguments().getString(Constants.USERNAME);

            list = new ArrayList<>();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // check if the parent activity implements the interface
        if (context instanceof DataLoadingListener)
            mListener = (DataLoadingListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // avoid leaks
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout and initialize the views in the fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View v){
        if (mListener != null)
            mListener.onLoadingStart();
        // initialize the list dependant on fragment type
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.menuList);
        if (tag.equals(FOLLOWERS))
            list = DataHandler.getInstance().getUserFollowers(username);
        else if (tag.equals(FOLLOWING))
            list = DataHandler.getInstance().getUserFollowings(username);
        // set the adapter and and the recycler layout type
        adapter = new FollowersAdapter(getContext(), list, tag.equals(FOLLOWING));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (mListener != null)
            mListener.onnLoadingDone();
    }
    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
