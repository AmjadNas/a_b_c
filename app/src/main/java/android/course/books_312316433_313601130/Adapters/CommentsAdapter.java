package android.course.books_312316433_313601130.Adapters;

import android.content.Context;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Objects.Comment;
import android.course.books_312316433_313601130.Objects.Reply;
import android.course.books_312316433_313601130.R;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private Context context;
    private List<Reply> replies;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Reply> replies, List<Comment> comments){
        this.context = context;
        this.replies = replies;
        this.comments = comments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.reply_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        // bind the data from the replies list if not null
        // else bind from comments list
        if (replies != null){

            viewHolder.message.setText(replies.get(i).getMessage());
            viewHolder.username.setText(replies.get(i).getUsername());
            Bitmap img = DataHandler.getInstance().getUserImage(replies.get(i).getUsername());
            if (img != null)
                viewHolder.thum.setImageBitmap(img);
            else
                viewHolder.thum.setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_24dp));
        }else if (comments != null){
            viewHolder.message.setText(comments.get(i).getComment());
            viewHolder.username.setText(comments.get(i).getUsername());
            Bitmap img = DataHandler.getInstance().getUserImage(comments.get(i).getUsername());
            if (img != null)
                viewHolder.thum.setImageBitmap(img);
            else
                viewHolder.thum.setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_24dp));
        }
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView thum;
        private TextView username, message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            thum = (CircleImageView)itemView.findViewById(R.id.prof_act_thumb);
            username = (TextView)itemView.findViewById(R.id.name_rep_row);
            message = (TextView)itemView.findViewById(R.id.msg_rep_row);
        }
    }

    @Override
    public int getItemCount() {

        if (replies == null)
            return comments == null ? 0 : comments.size();
        if (comments == null)
            return replies == null ? 0 : replies.size();

        return 0;
    }
}
