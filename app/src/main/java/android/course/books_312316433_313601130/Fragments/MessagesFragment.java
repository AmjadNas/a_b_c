package android.course.books_312316433_313601130.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.course.books_312316433_313601130.Activities.NewMessageActivity;
import android.course.books_312316433_313601130.Adapters.GridSpacingItemDecoration;
import android.course.books_312316433_313601130.Adapters.MessagesAdapter;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Fragments.Listeners.DataLoadingListener;
import android.course.books_312316433_313601130.Fragments.Listeners.Refreshable;
import android.course.books_312316433_313601130.Network.NetworkConnector;
import android.course.books_312316433_313601130.Network.NetworkResListener;
import android.course.books_312316433_313601130.Network.ResStatus;
import android.course.books_312316433_313601130.Objects.Message;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
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

public class MessagesFragment extends Fragment implements View.OnClickListener, NetworkResListener, Refreshable {
    /**
     * fragment type tags
     */
    public static final String SENT = "sent";
    public static final String TRASH = "trash";
    public static final String INBOX = "inbox";

    private final static String PARAM = "param";
    private String tag;
    private MessagesAdapter adapter;
    private List<Message> messages;
    private DataLoadingListener listener;

    public MessagesFragment(){}

    public static MessagesFragment newInstance(String TAG){
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putString(PARAM, TAG);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            tag = getArguments().getString(PARAM);

            if (tag.equals(SENT))
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_SENT_MESSAGES_JSON_REQ,
                        DataHandler.getInstance().getUser(), this);
            else if (tag.equals(INBOX))
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_RECIEVED_MESSAGES_JSON_REQ,
                        DataHandler.getInstance().getUser(), this);
            messages = new ArrayList<>();
        }
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

        adapter = new MessagesAdapter(getContext(), messages);
        // initialize the list dependant on fragment type
        if (tag.equals(INBOX) || tag.equals(SENT))
            adapter.setListener((MessagesAdapter.MessageAdapterListener) getParentFragment());
        adapter.setIsNotDelete(!tag.equals(TRASH));
        adapter.setSetiSRecieved(tag.equals(INBOX));
        // set the adapter and and the recycler layout type
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        if (tag.equals(INBOX))
            fab.setOnClickListener(this);
        else
            fab.hide();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle requests when the results are OK
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.SEND_MESAGE && tag.equals(SENT)){
                messages = DataHandler.getInstance().getSentMessages();
                adapter.setMessages(messages);
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DataLoadingListener)context;
        }catch (Exception e){}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), NewMessageActivity.class);
        ((Activity)getContext()).startActivityForResult(intent, Constants.SEND_MESAGE);
    }

    @Override
    public void onPreUpdate() {
        if (listener != null)
            listener.onLoadingStart();
    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        // if there's data from the internet parse it and display it in the recyclerView
        // else load the data from the device
        if (status == ResStatus.SUCCESS) {
            if (tag.equals(SENT) || tag.equals(INBOX)) {
               DataHandler.getInstance().parseMessages(adapter, messages,res);

            }
        }else {
            if (tag.equals(SENT))
                messages = DataHandler.getInstance().getSentMessages();
            else if (tag.equals(INBOX))
                messages = DataHandler.getInstance().getInboxMessages();
            adapter.setMessages(messages);
        }
        if (listener != null)
            listener.onnLoadingDone();
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }

    /**
     * adds the message to the deleted list
     * @param message to be deleted
     */
    public void addToDeletedList(Message message) {
        messages.add(message);
        adapter.notifyItemInserted(messages.size()-1);
    }

    @Override
    public void refresh() {
        // clear clear message and reload them
        messages.clear();
        if (tag.equals(SENT))
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_SENT_MESSAGES_JSON_REQ,
                    DataHandler.getInstance().getUser(), this);
        else if (tag.equals(INBOX))
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_RECIEVED_MESSAGES_JSON_REQ,
                    DataHandler.getInstance().getUser(), this);
    }
}
