package android.course.books_312316433_313601130.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.course.books_312316433_313601130.Activities.NewEventActivity;
import android.course.books_312316433_313601130.Adapters.ActivitiesAdapter;
import android.course.books_312316433_313601130.Adapters.GridSpacingItemDecoration;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Network.NetworkConnector;
import android.course.books_312316433_313601130.Network.NetworkResListener;
import android.course.books_312316433_313601130.Network.ResStatus;
import android.course.books_312316433_313601130.Objects.ActivityEvent;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.course.books_312316433_313601130.Utils.DataLoadingListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

public class ActivitiesFragment extends Fragment implements View.OnClickListener, NetworkResListener {

    private static final int ADD_ACTIVITY = 555;
    private ActivitiesAdapter adapter;
    private List<ActivityEvent> activities;
    private DataLoadingListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_USER_ACTIVITIES,
                DataHandler.getInstance().getUser(), this);
        activities = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stories, container, false);
        initRecycler(view);
        return view;
    }

    private void initRecycler(View view) {
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.include);

        adapter = new ActivitiesAdapter(getContext(), activities);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), NewEventActivity.class);
        getActivity().startActivityForResult(intent, ADD_ACTIVITY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == ADD_ACTIVITY){
                long id = data.getLongExtra(Constants.ID, -1);
                if (id >= 0) {
                    activities.add(0, DataHandler.getInstance().getActivityByID(id));
                    adapter.notifyItemInserted(0);
                }
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DataLoadingListener)context;
        }catch (Exception e){}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
    }

    @Override
    public void onPreUpdate() {
        if (listener != null)
            listener.onLoadingStart();
    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        if (status == ResStatus.SUCCESS){
            DataHandler.getInstance().parseActivities(adapter, activities, res);
        }else {
            activities = DataHandler.getInstance().getActivities();
            adapter.setActivities(activities);
        }
        if (listener != null)
            listener.onnLoadingDone();
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }
}
