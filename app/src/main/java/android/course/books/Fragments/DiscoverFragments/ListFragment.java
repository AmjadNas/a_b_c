package android.course.books.Fragments.DiscoverFragments;

import android.content.res.Resources;
import android.course.books.Adapters.GridSpacingItemDecoration;
import android.course.books.Adapters.SimpleListAdapter;
import android.course.books.R;
import android.course.books.Utils.Constants;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListFragment extends Fragment {
    private final static String TAG = "tag";

    private int param;

    public ListFragment(){}

    public static ListFragment newInstance(int tag){
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TAG, tag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param = getArguments().getInt(TAG, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.menuList);
        String[] arr = null;
        if (param == Constants.CATEGORY_LIST)
            arr = getResources().getStringArray(R.array.storyCategory);
        else if (param == Constants.GENRE_LIST)
            arr = getResources().getStringArray(R.array.storyGenre);

        SimpleListAdapter adapter = new SimpleListAdapter(getContext(), Constants.NORMAL_LINES_VIEW_TYPE, arr);
        adapter.setGenre(param == Constants.GENRE_LIST);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        //initPager(view);

        return view;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
