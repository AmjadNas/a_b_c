package android.course.books_312316433_313601130.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.course.books_312316433_313601130.Activities.NewChapterActivity;
import android.course.books_312316433_313601130.Adapters.ChapterAdpater;
import android.course.books_312316433_313601130.Adapters.GridSpacingItemDecoration;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Network.NetworkConnector;
import android.course.books_312316433_313601130.Network.NetworkResListener;
import android.course.books_312316433_313601130.Network.ResStatus;
import android.course.books_312316433_313601130.Objects.Chapter;
import android.course.books_312316433_313601130.Objects.Story;
import android.course.books_312316433_313601130.Utils.Constants;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.course.books_312316433_313601130.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChaptersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChaptersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChaptersFragment extends Fragment implements ChapterAdpater.ChapterAdapterListener, NetworkResListener {
    /**
     * fragment type tags
     */
    public final static String DELETED = "deleted";
    public final static String LIST = "list";
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private String sTitle;
    private List<Chapter> chapters;
    private OnFragmentInteractionListener mListener;
    private ChapterAdpater adapter;

    public ChaptersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param o
     * @return A new instance of fragment ChaptersFragment.
     */
    public static ChaptersFragment newInstance(String param1, String o) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        if (o != null)
            args.putString(Constants.TITLE, o);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            sTitle = getArguments().getString(Constants.TITLE);
        }
        if (mParam1.equals(LIST))
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_STORY_CHAPTERS, new Story(sTitle), this);

        chapters = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chapters_list, container, false);
        // initialize the list dependant on fragment type
        if (mParam1.equals(DELETED)){
            initFab(view, false);
        }else if (mParam1.equals(LIST)) {
            initFab(view, true);
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.include);
        adapter = new ChapterAdpater(getContext(), chapters, this, mParam1.equals(DELETED));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));

        return view;
    }

    private void initFab(View v, boolean show) {
        FloatingActionButton floatingActionButton = (FloatingActionButton) v.findViewById(R.id.fab);
        if (!show)
            floatingActionButton.hide();
        else
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), NewChapterActivity.class);
                    intent.putExtra(Constants.STORY_TITLE, sTitle);
                    getActivity().startActivityForResult(intent, Constants.ADDCHAPTER);
                }
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle request in case of a comment was added, a chapter was added or a chapter was edited
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String title = data.getStringExtra(Constants.TITLE);
            if (requestCode == Constants.ADDCHAPTER) {
                chapters.add(DataHandler.getInstance().getChapter(sTitle, title));
                adapter.notifyItemInserted(chapters.size() - 1);
                mListener.chapterAdded(sTitle);
            } else if (requestCode == Constants.EDIT_CHAPTER) {
                int i = chapters.indexOf(new Chapter(sTitle, title));
                chapters.remove(i);
                Chapter c = DataHandler.getInstance().getChapter(sTitle, title);
                chapters.add(i, c);
                adapter.notifyItemChanged(i);
                mListener.chapterAdded(sTitle);
            }else if (requestCode == Constants.COMMENT_CHAPTER){
                int i = chapters.indexOf(new Chapter(sTitle, title));
                chapters.remove(i);
                Chapter c = DataHandler.getInstance().getChapter(sTitle, title);
                chapters.add(i, c);
                adapter.notifyItemChanged(i);
            }
        }
    }

    public void onButtonPressed(Chapter c) {
        if (mListener != null) {
            mListener.onItemDeleted(c);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // check if the parent activity implements the interface
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // avoid leaks
        mListener = null;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void addChapterToList(Chapter c) {
        chapters.add(c);
        adapter.notifyItemInserted(chapters.size()-1);
    }

    @Override
    public void OnItemDeleted(Chapter c) {
        onButtonPressed(c);
    }

    @Override
    public void onPreUpdate() {
        if (mListener != null)
            mListener.chaptersLoading(true);
    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        // if there's data from the internet parse it and display it in the recyclerView
        // else load the data from the device
        if (status == ResStatus.SUCCESS){
            DataHandler.getInstance().parseChapters(adapter, chapters, res);
        }else {
            chapters = DataHandler.getInstance().getChaptersBytStoryTitle(sTitle);
            adapter.setChapters(chapters);
        }
        if (mListener != null)
            mListener.chaptersLoading(false);
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onItemDeleted(Chapter c);
        void chapterAdded(String c);
        void chaptersLoading(boolean isLoading);
    }
}
