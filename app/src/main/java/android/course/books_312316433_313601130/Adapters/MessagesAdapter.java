package android.course.books_312316433_313601130.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.course.books_312316433_313601130.Activities.NewMessageActivity;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Objects.Message;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyHolder> {

    private boolean isNotDelete;
    private boolean isRecived;
    private List<Message> list;
    private Context context;
    private MessageAdapterListener mListener;

    public MessagesAdapter(Context context, List<Message> messages) {
        list = messages;
        this.context = context;

    }

    public void setListener(MessageAdapterListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyHolder viewHolder, int i) {
        Message a = list.get(i);

        Bitmap img = DataHandler.getInstance().getUserImage(a.getSender());
        if (img == null)
            viewHolder.img.setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_24dp));
        else
            viewHolder.img.setImageBitmap(img);
        viewHolder.message.setText(a.getMessage());
        viewHolder.name.setText(a.getSender());
        viewHolder.subject.setText(a.getSubject());
    }

    @Override
    public int getItemCount() {
        return list == null? 0 : list.size();
    }

    public void setMessages(List<Message> messages) {
        list = messages;
        notifyDataSetChanged();
    }

    public void setIsNotDelete(boolean b) {
        isNotDelete = b;
    }

    public void setSetiSRecieved(boolean equals) {
        isRecived = equals;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        private CircleImageView img;
        private TextView name, message, reply, subject;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            img = (CircleImageView) itemView.findViewById(R.id.prof_act_thumb);
            name  = (TextView) itemView.findViewById(R.id.name_act_row);
            message = (TextView) itemView.findViewById(R.id.msg_msg_row);
            subject = (TextView) itemView.findViewById(R.id.subj_msg_row);
            reply = (TextView) itemView.findViewById(R.id.ply_act_row);
            if (!isRecived)
                reply.setVisibility(View.GONE);
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewMessageActivity.class);
                    intent.putExtra(Constants.USERNAME, list.get(getAdapterPosition()).getSender());
                    ((Activity)context).startActivityForResult(intent, Constants.SEND_MESAGE);
                }
            });
            if (isNotDelete)
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialog();
                        return true;
                    }
                });
        }

        private void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.removeItem)
                    .setPositiveButton(R.string.answerYes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int i = getAdapterPosition();
                            Message m = list.remove(i);
                            DataHandler.getInstance().deleteMessage(m);
                            notifyItemRemoved(i);
                            if (mListener != null) {
                                mListener.OnMessageDeleted(m);
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.answerNo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
    }

    /**
     * an interface that notifies of message deletion
     */
    public interface MessageAdapterListener{
        void OnMessageDeleted(Message message);
    }
}
