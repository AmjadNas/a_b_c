package android.course.books.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.course.books.Activities.AddCommentActivity;
import android.course.books.DatabaseHandkers.DataHandler;
import android.course.books.Objects.ActivityEvent;
import android.course.books.R;
import android.course.books.Utils.Constants;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.MyHolder> {

    private List<ActivityEvent> list;
    private Context context;

    public ActivitiesAdapter(Context context, List<ActivityEvent> activities) {
        list = activities;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder viewHolder, int i) {
        ActivityEvent a = list.get(i);

        Bitmap img = DataHandler.getInstance().getUserImage(a.getUsername());
        if (img == null)
            viewHolder.img.setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_24dp));
        else
            viewHolder.img.setImageBitmap(img);
        viewHolder.message.setText(a.getMessage());
        viewHolder.name.setText(a.getUsername());

    }

    @Override
    public int getItemCount() {
        return list == null? 0 : list.size();
    }

    public void setActivities(List<ActivityEvent> activities) {
        list = activities;
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        private CircleImageView img;
        private TextView name, message, reply;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            img = (CircleImageView) itemView.findViewById(R.id.prof_act_thumb);
            name  = (TextView) itemView.findViewById(R.id.name_act_row);
            message = (TextView) itemView.findViewById(R.id.msg_act_row);
            reply = (TextView) itemView.findViewById(R.id.ply_act_row);
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddCommentActivity.class);
                    intent.putExtra(Constants.VIEW_TYPE, Constants.REPLIES);
                    intent.putExtra(Constants.ID, list.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }

        private void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.tags_dialog, null);
            final EditText editText = (EditText)v.findViewById(R.id.diag_tags_add);
            editText.setHint(R.string.addReply);
            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton(R.string.reply, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String str = editText.getEditableText().toString();
                            sendReply(str);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setMessage(R.string.addReply);
            builder.create().show();
        }

        private void sendReply(String str){
            if (!str.isEmpty())
                DataHandler.getInstance().insertReply(list.get(getAdapterPosition()).getId(), str);
        }
    }


}
