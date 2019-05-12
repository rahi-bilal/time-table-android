package com.scheduleme.timetable;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.scheduleme.timetable.database.DatabaseContract;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private Cursor cursor;
    private Context context;
    private static Typeface fontAwesomeTypeface;
    private Map<String, Integer> daysMap= new HashMap<>();
    private static final int shapeSource[]= {R.drawable.circle, R.drawable.circle_3, R.drawable.circle_2};
    private int fragmentIndex;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public RecyclerViewAdapter(Context context, Cursor cursor, int fragmentIndex, RecyclerViewItemClickListener listener){
        this.context= context;
        this.cursor= cursor;
        this.recyclerViewItemClickListener= listener;
        this.fragmentIndex= fragmentIndex;
        daysMap.put("Sun", Calendar.SUNDAY);
        daysMap.put("Mon", Calendar.MONDAY);
        daysMap.put("Tue", Calendar.TUESDAY);
        daysMap.put("Wed", Calendar.WEDNESDAY);
        daysMap.put("Thu", Calendar.THURSDAY);
        daysMap.put("Fri", Calendar.FRIDAY);
        daysMap.put("Sat", Calendar.SATURDAY);
        fontAwesomeTypeface= Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView labelTextView;
        private TextView timeTextView;
        private TextView subjectTextView;
        private TextView roomTextView;
        private TextView teacherTextView;
        private LinearLayout imageLinearLayout;
        private TextView ellipseTextView;
        public RecyclerViewHolder(View view){
            super(view);
            timeTextView= view.findViewById(R.id.time_text_view);
            subjectTextView= view.findViewById(R.id.subject_text_view);
            roomTextView= view.findViewById(R.id.room_text_view);
            teacherTextView= view.findViewById(R.id.teacher_text_view);
            labelTextView= view.findViewById(R.id.lecture_label);
            imageLinearLayout= view.findViewById(R.id.image_linear_layout);
            ellipseTextView= view.findViewById(R.id.ellipse_text_view);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_view_holder, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        if (!cursor.moveToPosition(position))
            return;
        String fromTime= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM));
        String toTime= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO));
        String subject= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_SUBJECT_CODE));
        String teacher= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TEACHER_NAME));
        String roomNumber= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_ROOM_NUMBER));
        int status= cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_ALARM_STATUS));
        String time= fromTime+"-"+toTime;
        holder.timeTextView.setText(time);
        holder.subjectTextView.setText(subject);
        holder.teacherTextView.setText(teacher);
        holder.roomTextView.setText(roomNumber);
        holder.labelTextView.setText(Integer.toString(position+1));
        holder.ellipseTextView.setTypeface(fontAwesomeTypeface);
        holder.labelTextView.setBackground(context.getDrawable(shapeSource[position%3]));
        final long id= cursor.getLong(cursor.getColumnIndex(DatabaseContract.SchemaVaribles._ID));
        holder.itemView.setTag(id);
        final ImageView alarmImageView= holder.itemView.findViewById(R.id.alarm_image_view);
        if (status!=0){
            alarmImageView.setImageDrawable(context.getDrawable(R.drawable.ic_alarm_on));
        }

        //Setting NotificationItemClickListener
        alarmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemClickListener.onNotificationClicked(v, id, holder);
            }
        });
        //Setting DeleteItemClickListener
        ImageView deleteImageView= holder.itemView.findViewById(R.id.delete_schedule_image_view);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemClickListener.onDeleteClick(v, id);
            }
        });

        //Setting EditImageItemClickListener
        ImageView editImageView= holder.itemView.findViewById(R.id.edit_schedule_image_view);
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemClickListener.onItemClick(v, id);
            }
        });

        //setting listener for ellipseTextView to show action icons
        holder.ellipseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.imageLinearLayout.getVisibility()== View.GONE){
                    holder.ellipseTextView.setText(context.getResources().getString(R.string.arrow_right_circle));
                    holder.imageLinearLayout.setVisibility(View.VISIBLE);
                    holder.imageLinearLayout.startAnimation(AnimationUtils.loadAnimation(
                            context, R.anim.slideleft
                    ));
                }else{
                    holder.ellipseTextView.setText(context.getResources().getString(R.string.arrow_left_circle));
                    holder.imageLinearLayout.startAnimation(AnimationUtils.loadAnimation(
                            context, R.anim.slideright
                    ));
                    holder.imageLinearLayout.setVisibility(View.GONE);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

}
