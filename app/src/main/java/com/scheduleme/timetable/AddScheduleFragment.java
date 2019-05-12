package com.scheduleme.timetable;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.scheduleme.timetable.database.DatabaseContract;
import com.scheduleme.timetable.database.ScheduleDbHelper;

import org.w3c.dom.Text;

public class AddScheduleFragment extends Fragment {

    private View rootView;
    private EditText subjectEditText;
    private EditText teacherEditText;
    private EditText roomEditText;
    private EditText fromEditText;
    private EditText toEditText;
    private Button addButton;
    private TextView dayTextView;
    private ScheduleDbHelper scheduleDbHelper;

    private int dayIndex;

    private int timeList[]= new int[4];
    private static int dayTextViewId[]= {R.id.sun_tv, R.id.mon_tv, R.id.tue_tv, R.id.wed_tv, R.id.thu_tv, R.id.fri_tv, R.id.sat_tv};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dayIndex=1;
        if (getArguments()!=null){
            dayIndex= getArguments().getInt("dayIndex");
        }

    }

    public static AddScheduleFragment newInstance(int dayIndex){
        AddScheduleFragment fragment= new AddScheduleFragment();
        Bundle arguments= new Bundle();
        arguments.putInt("dayIndex", dayIndex);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.add_schedule_fragment, container, false);



        fromEditText= rootView.findViewById(R.id.from_edit_text);
        toEditText= rootView.findViewById(R.id.to_edit_text);
        subjectEditText= rootView.findViewById(R.id.subject_edit_text);
        teacherEditText= rootView.findViewById(R.id.teacher_edit_text);
        roomEditText= rootView.findViewById(R.id.room_edit_text);
        dayTextView=rootView.findViewById(dayTextViewId[dayIndex]);
        dayTextView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        dayTextView.setTextColor(getResources().getColor(R.color.colorWhite));

        //Initializing ScheduleDbHelper
        scheduleDbHelper= new ScheduleDbHelper(rootView.getContext());

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

        //Adding listener for add Button
        addButton= rootView.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
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
                        DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO};
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
                                && toEditText.getText().toString().compareTo(cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO)))<=0){
                                canInsert= false;
                                break;
                        }
                    }
                }
                cursor.close();
                db.close();

                if (canInsert){
                    addNewSchedule(rootView);
                }else {
                    Typeface fontAwesomeTypeFace= Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
                    View snackBarView;
                    TextView snackBarTextView;
                    Snackbar snackbar= Snackbar.make(rootView.findViewById(R.id.add_schedule_scroll_view), getActivity().getString(R.string.exclamation)+" You are already scheduled at this time.", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.snackbarAction));
                    snackBarView= snackbar.getView();
                    snackBarView.setMinimumHeight(150);
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
                    snackBarTextView= (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    snackBarTextView.setTypeface(fontAwesomeTypeFace);
                    snackbar.show();
                }

            }
        });

        return rootView;
    }

    public void addNewSchedule(View rootView){
        SQLiteDatabase db=  scheduleDbHelper.getWritableDatabase();

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
        long result=db.insert(DatabaseContract.SchemaVaribles.TABLE_NAME, null, scheduleData);

        if (result!= -1){
            Typeface fontAwesomeTypeFace= Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            View snackBarView;
            TextView snackBarTextView;
            Snackbar snackbar= Snackbar.make(rootView.findViewById(R.id.add_schedule_scroll_view), getActivity().getString(R.string.check)+" Week Schedule added successful.", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.snackbarAction));
            snackBarView= snackbar.getView();
            snackBarView.setMinimumHeight(150);
            snackBarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
            snackBarTextView= (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            snackBarTextView.setTypeface(fontAwesomeTypeFace);
            snackbar.show();
        }
        db.close();
    }


}
