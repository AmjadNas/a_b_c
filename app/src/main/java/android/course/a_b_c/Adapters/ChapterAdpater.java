package android.course.a_b_c.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.course.a_b_c.Activities.NewChapterActivity;
import android.course.a_b_c.Activities.ViewListActivity;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Objects.Chapter;
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

import java.util.List;

public class ChapterAdpater extends RecyclerView.Adapter<ChapterAdpater.ChapterHolder> {
    private boolean isView;
    private List<Chapter> list;
    private Context context;
    private ChapterAdapterListener mListener;
    private boolean isDelete;

    public ChapterAdpater(Context context, List<Chapter> list, ChapterAdapterListener mListener, boolean isDelete){
        this.list = list;
        this.context = context;
        this.mListener = mListener;
        this.isDelete = isDelete;
    }

    @NonNull
    @Override
    public ChapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.chapter_item, viewGroup, false);
        return new ChapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterHolder viewHolder, int i) {
        int b = i+1;
        Chapter c = list.get(i);
        viewHolder.num.setText(String.valueOf(b));
        viewHolder.commnets.setText(String.valueOf(c.getCommentcount()));
        viewHolder.title.setText(c.getTitle());
        viewHolder.length.setText(String.valueOf(c.getWordCount()));
        viewHolder.overflow.setOnClickListener(inflateMenuClickListener(i));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setChapters(List<Chapter> chapters) {
        list = chapters;
        notifyDataSetChanged();
    }

    public void setIsView(boolean b) {
        isView = b;
    }

    public class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView overflow;
        private TextView num, title, commnets, length;

        public ChapterHolder(View itemView){
            super(itemView);
            num = (TextView) itemView.findViewById(R.id.ch_it_num);
            title = (TextView) itemView.findViewById(R.id.ch_title_it);
            commnets = (TextView) itemView.findViewById(R.id.cmt_ch_it);
            length = (TextView) itemView.findViewById(R.id.ch_length_it);
            overflow = (ImageView) itemView.findViewById(R.id.ch_overflow);
            if (isDelete || isView)
                overflow.setVisibility(View.GONE);
            if (!isDelete)
                itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewListActivity.class);
            intent.putExtra(Constants.VIEW_TYPE, Constants.STORY_LINES_VIEW_TYPE);
            intent.putExtra(Constants.RESOURCE, Constants.STORY_LINES);
            intent.putExtra(Constants.STORY_TITLE, list.get(getAdapterPosition()).getStoryTitle());
            intent.putExtra(Constants.TITLE, list.get(getAdapterPosition()).getTitle());
            context.startActivity(intent);
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
                    Intent intent = new Intent(context, NewChapterActivity.class);
                    intent.putExtra(Constants.STORY_TITLE, list.get(pos).getStoryTitle());
                    intent.putExtra(Constants.TITLE, list.get(pos).getTitle());
                    ((Activity)context).startActivityForResult(intent, Constants.EDIT_CHAPTER);
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

    private void shareItem(int pos) {
    }

    private void removeItem(int pos){
        Chapter c = list.get(pos);
        if (DataHandler.getInstance().removeChapter(c.getStoryTitle(), c.getTitle())) {
            list.remove(pos);
            Toast.makeText(context, context.getString(R.string.itemDeleted), Toast.LENGTH_SHORT).show();
            notifyItemRemoved(pos);
            mListener.OnItemDeleted(c);
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

    public interface ChapterAdapterListener {

        void OnItemDeleted(Chapter c);
    }
}
