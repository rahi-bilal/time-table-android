package com.scheduleme.timetable;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.scheduleme.timetable.database.DatabaseContract;

import org.w3c.dom.Text;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ExamRecyclerViewAdapter extends RecyclerView.Adapter<ExamRecyclerViewAdapter.ExamRecyclerViewHolder> {
    private static final int shapeSource[]= {R.drawable.circle, R.drawable.circle_3, R.drawable.circle_2};
    private Context context;
    private Cursor cursor;
    private DatabaseUtility databaseUtility;
    private static Typeface fontAwesomeTypeface;
    private ExamRecyclerViewItemClickListener examRecyclerViewItemClickListener;


    public static class ExamRecyclerViewHolder extends RecyclerViewAdapter.RecyclerViewHolder{
        private TextView examLabelTextView;
        private TextView dateTextView;
        private TextView timeTextView;
        private TextView subjectTextView;
        private TextView typeTextView;
        private TextView ellipseTextView;
        private LinearLayout imageLinearLayout;

        //Constructor for ExamRecyclerViewHolder
        public ExamRecyclerViewHolder(View view){
            super(view);
            examLabelTextView= view.findViewById(R.id.exam_label);
            dateTextView= view.findViewById(R.id.exam_date_text_view);
            timeTextView= view.findViewById(R.id.exam_time_text_view);
            subjectTextView= view.findViewById(R.id.exam_subject_text_view);
            typeTextView= view.findViewById(R.id.exam_type_text_view);
            ellipseTextView= view.findViewById(R.id.exam_ellipse_text_view);
            imageLinearLayout= view.findViewById(R.id.exam_image_linear_layout);
        }
    }

    public ExamRecyclerViewAdapter(Context context, Cursor cursor, ExamRecyclerViewItemClickListener listener){
        this.context= context;
        this.cursor= cursor;
        databaseUtility= new DatabaseUtility(context);
        examRecyclerViewItemClickListener= listener;
        fontAwesomeTypeface= Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
    }


    @Override
    public ExamRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_recycler_view_holder, parent, false);
        return new ExamRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExamRecyclerViewHolder holder, int position) {
        if(!cursor.moveToPosition(position))
            return;
        //Setting contents of the Exam View Holder
        String date= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DATE));
        String time= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TIME));
        String subject= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_SUBJECT));
        String type= cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TYPE));
        final long id= cursor.getLong(cursor.getColumnIndex(DatabaseContract.SchemaVaribles._ID));
        holder.examLabelTextView.setText(Integer.toString(position+1));
        holder.examLabelTextView.setBackground(context.getDrawable(shapeSource[position%3]));
        holder.itemView.setTag(id);
        holder.dateTextView.setText(date);
        holder.timeTextView.setText(time);
        holder.subjectTextView.setText(subject);
        holder.typeTextView.setText(type);

        //Setting font TypeFace for ellipseTextView
        holder.ellipseTextView.setTypeface(fontAwesomeTypeface);

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

        // setting Edit Image item click listener
        ImageView editImageView= holder.itemView.findViewById(R.id.edit_exam_image_view);
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                examRecyclerViewItemClickListener.onEditClick(v, id);
            }
        });

        //setting delete image item click listener
        ImageView deleteImageView= holder.itemView.findViewById(R.id.delete_exam_image_view);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(id);
            }
        });

        //setting alarm image item click listener
        ImageView alarmImageView= holder.itemView.findViewById(R.id.exam_alarm_image_view);
        alarmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlarmPopupMenu(holder, context, v);
            }
        });

        //Setting image of the alarm status
        if (databaseUtility.getExamAlarmStatus(id)!=0){
            alarmImageView.setImageDrawable(context.getDrawable(R.drawable.ic_alarm_on));
        }


    }

    //Swap cursor
    public void swapCursor(Cursor newCursor){
        if (cursor!= null){
            cursor.close();
        }
        cursor= newCursor;
        if (newCursor!=null)
            notifyDataSetChanged();

    }

    //Set number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return cursor.getCount();

    }

    // delete the current Exam schedule when confirmed to delete
    private void showDeleteDialog(final long id){
        Cursor examCursor = databaseUtility.getExamScheduleById(id);
        databaseUtility.deleteExamSchedule((int)id);
        swapCursor(databaseUtility.getExamSchedule());

    }

    // show popup menu when alarm image view is clicked
    private void showAlarmPopupMenu(ExamRecyclerViewHolder holder, Context context, View v){
        PopupMenu popupMenu= new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.exam_alarm_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(getExamPopupMenuListener(holder));
        popupMenu.show();
    }

    //setting ExamAlarm popup menu item click listener
    private PopupMenu.OnMenuItemClickListener getExamPopupMenuListener(final ExamRecyclerViewHolder holder){
        PopupMenu.OnMenuItemClickListener alarmPopupMenuItemClickListener= new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ImageView alarmImageView= holder.itemView.findViewById(R.id.exam_alarm_image_view);
                Cursor alarmExamCursor= databaseUtility.getExamScheduleById((long) holder.itemView.getTag());
                alarmExamCursor.moveToNext();
                int dayOfMonth= alarmExamCursor.getInt(alarmExamCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DAY_OF_MONTH));
                int month= alarmExamCursor.getInt(alarmExamCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_MONTH));
                int year= alarmExamCursor.getInt(alarmExamCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_YEAR));
                Calendar calendar= Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                switch (item.getItemId()){
                    case R.id.exam_alarm_off:
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        removeAlarm(calendar);
                        //update alarm status
                        databaseUtility.updateExamAlarmStatus(0, (long)holder.itemView.getTag());
                        alarmImageView.setImageDrawable(context.getDrawable(R.drawable.ic_alarm_off));
                        break;
                    case R.id.before_one_day:
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        setAlarm(calendar);
                        //update exam alarm status
                        databaseUtility.updateExamAlarmStatus(1, (long)holder.itemView.getTag());
                        alarmImageView.setImageDrawable(context.getDrawable(R.drawable.ic_alarm_on));
                        break;
                    default:
                }
                return true;
            }
        };
        return alarmPopupMenuItemClickListener;
    }

    //setting exam alarm
    private void setAlarm(Calendar calendar){
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent= new Intent(context, ExamAlarmReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        showAlarmSetDialog(calendar, 1);
    }

    //removing alarm
    private void removeAlarm(Calendar calendar){
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent= new Intent(context, ExamAlarmReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        showAlarmSetDialog(calendar, 0);
    }

    //show alarm set successful dialog
    private void showAlarmSetDialog(Calendar calendar, int status){
        AlertDialog.Builder builder= new AlertDialog.Builder(context);
        SimpleDateFormat dateFormat= new SimpleDateFormat("E MMM d, Y");
        if (status==1){
            builder.setMessage("Alarm set on: "+ dateFormat.format(calendar.getTime()));
        }else{
            builder.setMessage("Alarm cancelled on: "+ dateFormat.format(calendar.getTime()));
        }
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


}
