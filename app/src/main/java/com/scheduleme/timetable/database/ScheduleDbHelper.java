package com.scheduleme.timetable.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.scheduleme.timetable.database.DatabaseContract.SchemaVaribles.CREATE_EXAM_SCHEDULE_TABLE;
import static com.scheduleme.timetable.database.DatabaseContract.SchemaVaribles.CREATE_TABLE_SCHEDULE;
import static com.scheduleme.timetable.database.DatabaseContract.SchemaVaribles.CREATE_TO_DO_TABLE;
import static com.scheduleme.timetable.database.DatabaseContract.SchemaVaribles.DELETE_EXAM_SCHEDULE_TABLE;
import static com.scheduleme.timetable.database.DatabaseContract.SchemaVaribles.DELETE_TABLE_SCHEDULE;


public class ScheduleDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION= 1;
    public static final String DATABASE_NAME= "Schedule.db";

    public ScheduleDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXAM_SCHEDULE_TABLE);
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TO_DO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_SCHEDULE);
        db.execSQL(DELETE_EXAM_SCHEDULE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
