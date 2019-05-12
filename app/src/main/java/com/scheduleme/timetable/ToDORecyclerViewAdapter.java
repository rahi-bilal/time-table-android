package com.scheduleme.timetable;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.scheduleme.timetable.database.DatabaseContract;


public class ToDORecyclerViewAdapter extends RecyclerView.Adapter<ToDORecyclerViewAdapter.ToDoRecyclerViewHolder> {

    private Context context;
    private Cursor cursor;
    private Typeface fontawesomeTypeFace;
    private ToDoRecyclerViewInterface toDoRecyclerViewInterface;

    public ToDORecyclerViewAdapter(Context context, Cursor cursor, ToDoRecyclerViewInterface toDoRecyclerViewInterface){
        this.context= context;
        this.cursor= cursor;
        this.toDoRecyclerViewInterface= toDoRecyclerViewInterface;
        fontawesomeTypeFace= Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
    }

    @NonNull
    @Override
    public ToDoRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.to_do_view_holder, parent, false);
        return new ToDoRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoRecyclerViewHolder holder, int position) {
        holder.taskCompleteButton.setTypeface(fontawesomeTypeFace);
        holder.labelTextView.setText(Integer.toString(position+1));
        if (cursor.moveToPosition(position)){
            holder.itemView.setTag(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles._ID)));
            holder.toDoContentTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TO_DO_CONTENT)));
            holder.taskCompleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDoRecyclerViewInterface.onDoneClick((Integer) holder.itemView.getTag());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if (newCursor!= null){
            cursor= newCursor;
            notifyDataSetChanged();
        }
    }


    public static class ToDoRecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView labelTextView;
        private TextView toDoContentTextView;
        private Button taskCompleteButton;


        public ToDoRecyclerViewHolder(View view){
            super(view);
            labelTextView= view.findViewById(R.id.to_do_label_text_view);
            toDoContentTextView= view.findViewById(R.id.to_do_content_text_view);
            taskCompleteButton= view.findViewById(R.id.to_do_done_button);
        }

    }
}
