package android.course.a_b_c.Fragments;

import android.content.res.Resources;
import android.course.a_b_c.Adapters.ChapterAdpater;
import android.course.a_b_c.Adapters.FollowersAdapter;
import android.course.a_b_c.Adapters.GridSpacingItemDecoration;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Network.NetworkConnector;
import android.course.a_b_c.Network.NetworkResListener;
import android.course.a_b_c.Network.ResStatus;
import android.course.a_b_c.Objects.ActivityEvent;
import android.course.a_b_c.Objects.User;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
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

public class FollowFragment extends Fragment implements NetworkResListener {

    public static final String FOLLOWERS = Constants.FOLLOWERS;
    public static final String FOLLOWING = Constants.FOLLOWING;

    private String tag;
    private List<String> list;
    private FollowersAdapter adapter;
    private String username;

    public FollowFragment(){}

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
            if (tag.equals(FOLLOWERS))
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_USER_FOLLOWERS,
                        new User(username), this);
            else if (tag.equals(FOLLOWING)) {
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_USER_FOLLOWING,
                        new User(username), this);
            }
            list = new ArrayList<>();
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View v){
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.menuList);
        adapter = new FollowersAdapter(getContext(), list, tag.equals(FOLLOWING));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onPreUpdate() {

    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        if (status == ResStatus.SUCCESS){
            if (tag.equals(FOLLOWERS))
                DataHandler.getInstance().parseStringList(adapter, list, FOLLOWERS,  res);
            else if (tag.equals(FOLLOWING))
                DataHandler.getInstance().parseStringList(adapter, list, FOLLOWING,  res);
        }else {
            if (tag.equals(FOLLOWERS))
                list = DataHandler.getInstance().getUserFollowers(username);
             else if (tag.equals(FOLLOWING))
                list = DataHandler.getInstance().getUserFollowings(username);

            adapter.setList(list);
        }
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }
}
