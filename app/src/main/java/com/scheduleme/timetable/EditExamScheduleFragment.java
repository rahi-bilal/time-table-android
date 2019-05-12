package com.scheduleme.timetable;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
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

import com.scheduleme.timetable.database.DatabaseContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditExamScheduleFragment extends Fragment {

    private View rootView;
    private Bundle bundle;
    private int id;
    private Cursor editExamScheduleCursor;
    private DatabaseUtility databaseUtility;
    private EditText subjectEditText;
    private EditText examDateEditText;
    private EditText examTimeEditText;
    private EditText examTypeEditText;
    private Button updateExamScheduleButton;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleTimeFormat;
    private String dateToSet, timeToSet;
    private int examDayOfMonth, examMonth, examYear;
    private Typeface fontAwesomeTypeFace;

    public EditExamScheduleFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.edit_exam_schedule_fragment, container, false);

        //get id of the Exam schedule to be updated
        bundle= getArguments();
        id= bundle.getInt("id");

        //getting reference of components in the EditExam User Interface
        subjectEditText= rootView.findViewById(R.id.u_exam_subject_edit_text);
        examDateEditText= rootView.findViewById(R.id.u_exam_date_edit_text);
        examTimeEditText= rootView.findViewById(R.id.u_exam_time_edit_text);
        examTypeEditText= rootView.findViewById(R.id.u_exam_type_edit_text);
        updateExamScheduleButton= rootView.findViewById(R.id.u_exam_update_button);

        //initializing databaseUtility object
        databaseUtility= new DatabaseUtility(getContext());

        //Initializing typeFace
        fontAwesomeTypeFace= Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");

        //getting data to be edited
        editExamScheduleCursor= databaseUtility.getExamCursorById(id);
        editExamScheduleCursor.moveToNext();

        //setting data of the ExamSchedule to be edited
        subjectEditText.setText(editExamScheduleCursor.getString(editExamScheduleCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_SUBJECT)));
        examDateEditText.setText(editExamScheduleCursor.getString(editExamScheduleCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DATE)));
        examTimeEditText.setText(editExamScheduleCursor.getString(editExamScheduleCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TIME)));
        examTypeEditText.setText(editExamScheduleCursor.getString(editExamScheduleCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TYPE)));
        calendar= Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, editExamScheduleCursor.getInt(editExamScheduleCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DAY_OF_MONTH)));
        calendar.set(Calendar.MONTH, editExamScheduleCursor.getInt(editExamScheduleCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_MONTH)));
        calendar.set(Calendar.YEAR, editExamScheduleCursor.getInt(editExamScheduleCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_YEAR)));

        //setting format for date and time
        simpleDateFormat= new SimpleDateFormat("dd MMM, y");
        simpleTimeFormat= new SimpleDateFormat("hh:mm a");


        //setting up listener to show DatePickerDialog for examDateEditText
        examDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //setting up listener to show TimePickerDialog for examTimeEditText
        examTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //setting up listener to update ExamSchedule Button
        updateExamScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectEditText.getText().length()==0){
                    subjectEditText.setError("This field cannot be empty.");
                    return;
                }
                if (examDateEditText.getText().length()==0){
                    examDateEditText.setError("This field cannot be empty.");
                    return;
                }
                if (examTimeEditText.getText().length()==0){
                    examTimeEditText.setError("This field cannot be empty.");
                    return;
                }
                if (examTypeEditText.getText().length()==0){
                    examTypeEditText.setError("This field cannot be empty.");
                    return;
                }
                updateExamSchedule();
            }
        });

        return rootView;
    }

    public void showDatePickerDialog(){
        calendar= Calendar.getInstance();
        DatePickerDialog datePickerDialog= new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateToSet= simpleDateFormat.format(calendar.getTime());
                examDayOfMonth= dayOfMonth;
                examMonth= month;
                examYear= year;
                examDateEditText.setText(dateToSet);
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
                examTimeEditText.setText(timeToSet);

            }
        }, 0, 0, false);
        timePickerDialog.show();
    }

    public void updateExamSchedule(){
        ContentValues contentValues= new ContentValues();
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_SUBJECT, subjectEditText.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DATE, examDateEditText.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TIME, examTimeEditText.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_TYPE, examTypeEditText.getText().toString());
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_DAY_OF_MONTH, examDayOfMonth);
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_MONTH, examMonth);
        contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_YEAR, examYear);
        if(databaseUtility.updateExam(contentValues, id)){
            showUpdateSuccessDialog();
        }
    }

    private void showUpdateSuccessDialog(){
        View snackBarView;
        TextView snackBarTextView;
        Snackbar snackbar= Snackbar.make(rootView.findViewById(R.id.update_exam_schedule_scroll_view), getActivity().getString(R.string.check)+" Exam Schedule updated successful.", Snackbar.LENGTH_LONG)
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
