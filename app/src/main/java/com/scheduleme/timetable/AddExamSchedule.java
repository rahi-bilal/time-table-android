package com.scheduleme.timetable;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.scheduleme.timetable.DatabaseUtility;
import com.scheduleme.timetable.database.DatabaseContract;

public class AddExamSchedule extends Fragment {
    private View rootView;
    private EditText subjectEditText;
    private EditText dateEdittext;
    private EditText timeEditText;
    private EditText typeEditText;
    private Button addExamButton;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleTimeFormat;
    private String dateToset;
    private String timeToSet;
    private int examDayOfMonth;
    private int examMonth;
    private int examYear;
    private DatabaseUtility databaseUtility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.add_exam_fragment, container, false);
        subjectEditText= rootView.findViewById(R.id.exam_subject_edit_text);
        dateEdittext= rootView.findViewById(R.id.exam_date_edit_text);
        timeEditText= rootView.findViewById(R.id.exam_time_edit_text);
        typeEditText= rootView.findViewById(R.id.exam_type_edit_text);
        addExamButton= rootView.findViewById(R.id.exam_add_button);

        //initialize database utility class
        databaseUtility= new DatabaseUtility(rootView.getContext());

        //set default date of dateTextView to today's date
        calendar= Calendar.getInstance();
        simpleDateFormat= new SimpleDateFormat("dd MMM, y");
        dateToset= simpleDateFormat.format(calendar.getTime());
        dateEdittext.setText(dateToset);

        //set default time of timeTextView to current time
        simpleTimeFormat= new SimpleDateFormat("hh:mm a");
        timeToSet= simpleTimeFormat.format(calendar.getTime());

        //Add on click listener to show datePickerDialog
        dateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //Add on click listener to show timePickerDialog
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //Add on click listener to add new Exam Schedule
        addExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectEditText.getText().length()==0){
                    subjectEditText.setError("This field cannot be empty.");
                    return;
                }
                if (dateEdittext.getText().length()==0){
                    dateEdittext.setError("This field cannot be empty.");
                    return;
                }
                if (timeEditText.getText().length()==0){
                    timeEditText.setError("This field cannot be empty.");
                    return;
                }
                if (typeEditText.getText().length()==0){
                    typeEditText.setError("This field cannot be empty.");
                    return;
                }
                addExamSchedule();
            }
        });

        return rootView;
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog= new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateToset= simpleDateFormat.format(calendar.getTime());
                examDayOfMonth= dayOfMonth;
                examMonth= month;
                examYear= year;
                dateEdittext.setText(dateToset);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void showTimePickerDialog(){
        TimePickerDialog timePickerDialog= new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                timeToSet= simpleTimeFormat.format(calendar.getTime());
                timeEditText.setText(timeToSet);

            }
        }, 0, 0, false);
        timePickerDialog.show();
    }


    public void addExamSchedule(){
        ContentValues contentValues= new ContentValues();
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_SUBJECT, subjectEditText.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DATE, dateEdittext.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TIME, timeEditText.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TYPE, typeEditText.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DAY_OF_MONTH, examDayOfMonth);
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_MONTH, examMonth);
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_YEAR, examYear);
        if(databaseUtility.addExam(contentValues)){
            showInsertSuccessDialog();
        }
    }

    private void showInsertSuccessDialog(){
        Typeface fontAwesomeTypeFace= Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        View snackBarView;
        TextView snackBarTextView;
        Snackbar snackbar= Snackbar.make(rootView.findViewById(R.id.add_exam_schedule_scroll_view), getActivity().getString(R.string.check)+" Exam Schedule added successful.", Snackbar.LENGTH_LONG)
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
