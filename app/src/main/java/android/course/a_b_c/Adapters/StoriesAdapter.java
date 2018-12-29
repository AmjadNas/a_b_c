package android.course.a_b_c.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.course.a_b_c.Activities.NewChapterActivity;
import android.course.a_b_c.Activities.NewStoryActivity;
import android.course.a_b_c.Activities.ChaptersListActivity;
import android.course.a_b_c.Activities.ViewStoryActivity;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ParentViewHolder> {
    private List<Story> list;
    private Context context;
    private int viewType;
    private static final int MY_STORY_VIEW_TYPE = 1;
    private StoryAdapterListener mStoryAdapterListener;
    boolean isDelete;

    public StoriesAdapter(Context context, List<Story> list, boolean myStories){
        this.list = list;
        this.context = context;
        if (myStories)
            viewType = 1;
        else
            viewType = 2;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MY_STORY_VIEW_TYPE) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_story_item, viewGroup, false);
            return new MyStoryViewHolder(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.story_item, viewGroup, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder myHolder, int i) {
        Story story = list.get(i);
        myHolder.st_title.setText(story.getTitle());
        myHolder.pageNum.setText(String.valueOf(story.getWrodCount()));
        myHolder.chapterNum.setText(String.valueOf(story.getChapterCount()));
        myHolder.status.setText(story.getStatusString());
        myHolder.thumbnail.setImageBitmap(DataHandler.getInstance().getStoryThumbnail(story.getTitle()));
        if (viewType == MY_STORY_VIEW_TYPE) {
            ((MyStoryViewHolder)myHolder).addChapter.setOnClickListener(getClickListener(NewChapterActivity.class, i));
            ((MyStoryViewHolder)myHolder).viewChapterList.setOnClickListener(getClickListener(ChaptersListActivity.class, i));
            ((MyStoryViewHolder)myHolder).overflow.setOnClickListener(inflateMenuClickListener(i));
        }else {
            ((MyHolder)myHolder).author.setText(story.getAuthor());
            ((MyHolder)myHolder).categories.setText(story.getCategoryString());
            ((MyHolder)myHolder).genres.setText(story.getGenreString());
            // myHolder.thumbnail.setImageBitmap(DataHandler.getInstance().getStoryCover(story.getTitle()));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setStories(List<Story> stories) {
        list = stories;
        notifyDataSetChanged();
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        private TextView st_title, pageNum, chapterNum, status;
        private ImageView thumbnail;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            st_title = (TextView)itemView.findViewById(R.id.text_st_title);
            pageNum = (TextView)itemView.findViewById(R.id.txt_st_length);
            chapterNum = (TextView)itemView.findViewById(R.id.txt_st_chaptersCount);
            status = (TextView)itemView.findViewById(R.id.txt_st_status);
            thumbnail = (ImageView)itemView.findViewById(R.id.st_thumb);

        }
    }

    public class MyHolder extends ParentViewHolder {
        private TextView  author, categories, genres;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
           // CardView card = (CardView)itemView.findViewById(R.id.story_card);
            author = (TextView)itemView.findViewById(R.id.txt_st_writer);
            categories = (TextView)itemView.findViewById(R.id.txt_st_categories);
            genres = (TextView)itemView.findViewById(R.id.txt_st_genres);
            if (!isDelete)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewStoryActivity.class);
                    intent.putExtra(Constants.RESOURCE, list.get(getAdapterPosition()).getTitle());
                    ((Activity)context).startActivityForResult(intent, 1);
                }
            });
        }
    }

    public class MyStoryViewHolder extends ParentViewHolder  {
        private TextView addChapter, viewChapterList;
        private ImageView overflow;

        public MyStoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // CardView card = (CardView)itemView.findViewById(R.id.story_card);
            addChapter = (TextView)itemView.findViewById(R.id.lbl_chapters_add);
            viewChapterList = (TextView)itemView.findViewById(R.id.lbl_chapters_list);
            overflow = (ImageView) itemView.findViewById(R.id.my_st_ovrflw);
        }
    }

    private View.OnClickListener inflateMenuClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();

                /*if (grid)
                    inflater.inflate(R.menu.item_fave_menu, popup.getMenu());
                else if(faveAdapter || viewOnly)
                    inflater.inflate(R.menu.item_view_menu, popup.getMenu());
                else*/
                inflater.inflate(R.menu.menu_item_manage, popup.getMenu());

                popup.setOnMenuItemClickListener(new MenuClickListener(i));
                popup.show();
            }
        };
    }

    private View.OnClickListener getClickListener(final Class<?> cls, final int i){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, cls);
                String ttl = list.get(i).getTitle();
                intent.putExtra(Constants.STORY_TITLE, ttl);
                ((Activity)context).startActivityForResult(intent, Constants.ADDCHAPTER);
            }
        };
    }

    /**
     * Menu click listener listens to overflow menu clicks
     */
    private class MenuClickListener implements PopupMenu.OnMenuItemClickListener {
        private int pos;

        public MenuClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.edit_menu_st: // add item to favourites
                    Intent intent = new Intent(context, NewStoryActivity.class);
                    intent.putExtra(Constants.TITLE, list.get(pos).getTitle());
                    ((Activity)context).startActivityForResult(intent, Constants.EDIT_STORY);
                    return true;
                case R.id.share_menu_st: // edit item
                    shareItem(pos);
                    return true;
                case R.id.delete_menu_st: // remove item from list
                    openDiallog(pos);
                    return true;
            }
            return false;

        }
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public void setListener(StoryAdapterListener listener){
        mStoryAdapterListener = listener;
    }

    private void shareItem(int pos) {
    }

    private void removeItem(int pos){
        Story s = list.remove(pos);
        if (DataHandler.getInstance().removeStory(s.getTitle())){
            Toast.makeText(context, context.getString(R.string.itemDeleted), Toast.LENGTH_SHORT).show();
            notifyItemRemoved(pos);
            mStoryAdapterListener.OnStoryDeleted(s);
        }
    }

    private void openDiallog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.answerYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(pos);
                dialog.dismiss();

            }
        })
        .setNegativeButton(R.string.answerNo, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
           }
       })
       .setTitle(R.string.askContinue);
        builder.create().show();
    }

    public interface StoryAdapterListener {
       void OnStoryDeleted(Story s);
    }
}
