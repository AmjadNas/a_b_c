package android.course.books_312316433_313601130.Adapters;

import android.content.Context;
import android.content.Intent;
import android.course.books_312316433_313601130.Activities.StoriesByActivities;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class SimpleListAdapter extends RecyclerView.Adapter {

    private String[] list;
    private Context context;
    private int viewType;
    private HashSet<String> strs;
    private boolean isGenre;

    public SimpleListAdapter(Context ctx, int i, String[] list){

        this.list = list;
        viewType = i;
        context = ctx;
        strs = new HashSet<>();
    }

    @Override
    public int getItemViewType(int position) {

        // adapting view type to message if sent or received
       return viewType;
    }

    public void setGenre(boolean genre) {
        isGenre = genre;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (Constants.CHECKBOX_VIEW_TYPE == i){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.checkbox_row, viewGroup,false);
            return new CheckBoxViewHolder(view);
        }else if (Constants.STORY_LINES_VIEW_TYPE == i){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chapter_line_item, viewGroup,false);
            return new MyViewHolder(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.simple_row, viewGroup,false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (Constants.CHECKBOX_VIEW_TYPE == viewType)
            ((CheckBoxViewHolder)viewHolder).checkBox.setText(list[i]);
        else {
            ((MyViewHolder)viewHolder).textView.setText(list[i]);
        }

    }

    @Override
    public int getItemCount() {
        return list == null? 0 : list.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StoriesByActivities.class);
                    if (isGenre)
                        intent.putExtra(Constants.VIEW_TYPE, Constants.GENRES);
                    else
                        intent.putExtra(Constants.VIEW_TYPE, Constants.CATEGORIES);
                    intent.putExtra(Constants.RESOURCE, list[getAdapterPosition()]);
                    context.startActivity(intent);
                }
            });
        }
    }

    public class CheckBoxViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private CheckBox checkBox;

        public CheckBoxViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkedTextView);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked)
                strs.add(buttonView.getText().toString());
            else
                strs.remove(buttonView.getText().toString());
        }
    }

    public ArrayList<String> getStrs() {
        return new ArrayList<>(strs);
    }
}
