package android.course.books.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.course.books.Activities.ProfileActivity;
import android.course.books.DatabaseHandkers.DataHandler;
import android.course.books.R;
import android.course.books.Utils.Constants;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyHolder> {

    private final boolean isFollowing;
    private List<String> list;
    private Context context;

    public FollowersAdapter(Context context, List<String> list, boolean isFollowing){
        this.list = list;
        this.context = context;
        this.isFollowing = isFollowing;
    }

    public void setList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.follower_item, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        myHolder.username.setText(list.get(i));
        Bitmap img = DataHandler.getInstance().getUserImage(list.get(i));
        if (img != null)
            myHolder.img.setImageBitmap(img);
    }

    @Override
    public int getItemCount() {
        return list == null? 0 : list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private CircleImageView img;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.name_follow_row);
            img = (CircleImageView) itemView.findViewById(R.id.prof_follow_thumb);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra(Constants.USERNAME, list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
            // set on long click to enable the user to un-follow a user
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isFollowing)
                        showDialog();
                    return isFollowing;
                }
            });
        }

        private void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setPositiveButton(R.string.reply, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            DataHandler.getInstance().unFollowUser(list.remove(getAdapterPosition()));
                            notifyItemRemoved(getAdapterPosition());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setMessage(R.string.unfollowUser);
            builder.create().show();
        }
    }
}
