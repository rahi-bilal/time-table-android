package com.scheduleme.timetable;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.scheduleme.timetable.database.DatabaseContract;
import com.scheduleme.timetable.database.ScheduleDbHelper;



public class EditScheduleFragment extends Fragment {

    private Bundle bundle;
    private int id;
    View rootView;
    private EditText subjectEditText;
    private EditText teacherEditText;
    private EditText roomEditText;
    private TextView dayTextView;
    private EditText fromEditText;
    private EditText toEditText;
    private Button updateButton;
    private ScheduleDbHelper scheduleDbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;
    private String previousDay;
    private Typeface fontAwesomeTypeFace;

    private int timeList[]= new int[4];
    private static int dayTextViewId[]= {R.id.mon_tv, R.id.tue_tv, R.id.wed_tv, R.id.thu_tv, R.id.fri_tv, R.id.sat_tv, R.id.sun_tv};

    public EditScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.edit_schedule_fragment, container, false);

        bundle= getArguments();
        id= bundle.getInt("id");
        fromEditText= rootView.findViewById(R.id.e_from_edit_text);
        toEditText= rootView.findViewById(R.id.e_to_edit_text);
        subjectEditText= rootView.findViewById(R.id.e_subject_edit_text);
        teacherEditText= rootView.findViewById(R.id.e_teacher_edit_text);
        roomEditText= rootView.findViewById(R.id.e_room_edit_text);
        updateButton= rootView.findViewById(R.id.e_update_button);


        //Initializing fontAwesomeTypeFace
        fontAwesomeTypeFace= Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");

        //Initializing ScheduleDbHelper
        scheduleDbHelper= new ScheduleDbHelper(rootView.getContext());
        sqLiteDatabase= scheduleDbHelper.getWritableDatabase();
        String selection= DatabaseContract.SchemaVaribles._ID+ "= ?";
        String[] selectionArgs= {Integer.toString(id)};
        cursor= sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        cursor.moveToNext();
        subjectEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_SUBJECT_CODE)));
        teacherEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TEACHER_NAME)));
        roomEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_ROOM_NUMBER)));
        fromEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM)));
        toEditText.setText((cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO))));
        timeList[0]= cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_HOURS));
        timeList[1]= cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_MINUTES));
        timeList[2]= cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_HOURS));
        timeList[3]= cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_MINUTES));
        switch (cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_DAY))){
            case "Mon":
                previousDay= "Mon";
                dayTextView= rootView.findViewById(dayTextViewId[0]);
                break;
            case "Tue":
                previousDay= "Tue";
                dayTextView= rootView.findViewById(dayTextViewId[1]);
                break;
            case "Wed":
                previousDay= "Wed";
                dayTextView= rootView.findViewById(dayTextViewId[2]);
                break;
            case "Thu":
                previousDay= "Thu";
                dayTextView= rootView.findViewById(dayTextViewId[3]);
                break;
            case "Fri":
                previousDay= "Fri";
                dayTextView= rootView.findViewById(dayTextViewId[4]);
                break;
            case "Sat":
                previousDay= "Sat";
                dayTextView= rootView.findViewById(dayTextViewId[5]);
                break;
            case "Sun":
                previousDay= "Sun";
                dayTextView= rootView.findViewById(dayTextViewId[6]);
                break;
            default:
                dayTextView=rootView.findViewById(dayTextViewId[0]);
        }
        dayTextView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        dayTextView.setTextColor(getResources().getColor(R.color.colorWhite));

        //Adding Listeners for each day TextView
        for (int i=0; i<7; i++){
            rootView.findViewById(dayTextViewId[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dayTextView.setTextColor(getResources().getColor(R.color.textColorPrimary));
                    dayTextView.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                    dayTextView= rootView.findViewById(v.getId());
                    dayTextView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    dayTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            });
        }

        //Show TimePickerDialog OnClick on TimeEditText
        fromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog= new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay<10){
                            fromEditText.setText("0"+hourOfDay+":"+minute);
                        }
                        if (minute<10){
                            fromEditText.setText(hourOfDay+":0"+minute);
                        }
                        if (hourOfDay<10 && minute<10){
                            fromEditText.setText("0"+hourOfDay+":0"+minute);
                        }
                        if (hourOfDay>9 && minute>9){
                            fromEditText.setText(hourOfDay+":"+minute);
                        }
                        timeList[0]= hourOfDay;
                        timeList[1]= minute;
                    }
                }, 0, 0, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        toEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog= new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay<10){
                            toEditText.setText("0"+hourOfDay+":"+minute);
                        }
                        if (minute<10){
                            toEditText.setText(hourOfDay+":0"+minute);
                        }
                        if (hourOfDay<10 && minute<10){
                            toEditText.setText("0"+hourOfDay+":0"+minute);
                        }
                        if (hourOfDay>9 && minute>9){
                            toEditText.setText(hourOfDay+":"+minute);
                        }
                        timeList[2]= hourOfDay;
                        timeList[3]= minute;
                    }
                }, 0, 0, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean canInsert= true;
                if (subjectEditText.getText().length()==0){
                    subjectEditText.setError("This field cannot be empty");
                    return;
                }
                if (teacherEditText.getText().length()==0){
                    teacherEditText.setError("This field cannot be empty");
                    return;
                }
                if (roomEditText.getText().length()==0){
                    roomEditText.setError("This field cannot be empty");
                    return;
                }
                if (fromEditText.getText().length()==0){
                    fromEditText.setError("This field cannot be empty");
                    return;
                }
                if (toEditText.getText().length()==0){
                    toEditText.setError("This field cannot be empty");
                    return;
                }
                SQLiteDatabase db= scheduleDbHelper.getReadableDatabase();
                String columns[]= {DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM,
                        DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO,
                        DatabaseContract.SchemaVaribles._ID};
                String selection= DatabaseContract.SchemaVaribles.COLUMN_NAME_DAY+ "= ?";
                String selectionArgs[]= {dayTextView.getText().toString()};
                Cursor cursor= db.query(
                        DatabaseContract.SchemaVaribles.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM
                );
                if (cursor.getCount()!=0){
                    while (cursor.moveToNext()){
                        if(fromEditText.getText().toString().compareTo(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM)))>=0
                                && toEditText.getText().toString().compareTo(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO)))<=0
                                && id!= cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles._ID))){
                            Snackbar snackbar= Snackbar.make(rootView.findViewById(R.id.update_week_schedule_scroll_view), getString(R.string.exclamation)+"  You are already scheduled at this time.", Snackbar.LENGTH_LONG)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(R.color.snackbarAction));
                            View snackbarView= snackbar.getView();
                            snackbarView.setMinimumHeight(150);
                            snackbarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
                            TextView snackBarTextView= (TextView)snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarTextView.setTypeface(fontAwesomeTypeFace);
                            snackbar.show();
                            canInsert= false;
                            break;
                        }
                    }
                }
                cursor.close();
                db.close();
                if (canInsert){updateSchedule(rootView);}
            }
        });

        return rootView;
    }

    public void updateSchedule(View rootView){
        sqLiteDatabase= scheduleDbHelper.getWritableDatabase();
        ContentValues scheduleData= new ContentValues();
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_DAY, dayTextView.getText().toString());
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM, fromEditText.getText().toString());
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO, toEditText.getText().toString());
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_SUBJECT_CODE, subjectEditText.getText().toString());
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TEACHER_NAME, teacherEditText.getText().toString());
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_ROOM_NUMBER, roomEditText.getText().toString());
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_HOURS, timeList[0]);
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_MINUTES, timeList[1]);
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_HOURS, timeList[2]);
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_MINUTES, timeList[3]);
        if(sqLiteDatabase.update(
                DatabaseContract.SchemaVaribles.TABLE_NAME,
                scheduleData,
                DatabaseContract.SchemaVaribles._ID+"= ?",
                new String[]{Integer.toString(id)}
        )>0){
           showAlertDialog();
        }

    }

    private void showAlertDialog(){
        Snackbar snackbar= Snackbar.make(rootView.findViewById(R.id.update_week_schedule_scroll_view), getString(R.string.check)+"  Schedule updated successful.", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.snackbarAction));
        snackbar.getView().setMinimumHeight(150);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.iconsBackground));
        TextView snackBarTextView= (TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackBarTextView.setTypeface(fontAwesomeTypeFace);
        snackbar.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
