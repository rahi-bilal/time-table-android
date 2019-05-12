package com.scheduleme.timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scheduleme.timetable.database.DatabaseContract;
import com.scheduleme.timetable.database.ScheduleDbHelper;

public class DatabaseUtility {

    private ScheduleDbHelper scheduleDbHelper;
    private SQLiteDatabase sqLiteDatabase;

    DatabaseUtility(Context context){
        scheduleDbHelper= new ScheduleDbHelper(context);
        sqLiteDatabase= scheduleDbHelper.getWritableDatabase();
    }

    // methods for add week schedule
    public Cursor getCursorById(long id){
        String selection= DatabaseContract.SchemaVaribles._ID+ "= ?";
        String[] selectionArgs= {Long.toString(id)};
        return sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public Cursor getTodaySchedule(String day){
        String selectionArgs[]= {day};
        String selection= DatabaseContract.SchemaVaribles.COLUMN_NAME_DAY+ "= ?";
        return sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM
        );
    }

    public boolean removeItemById(long id){
        return sqLiteDatabase.delete(
                DatabaseContract.SchemaVaribles.TABLE_NAME,
                DatabaseContract.SchemaVaribles._ID+ "= "+id,
                null)>0;

    }

    public boolean undoDelete(Cursor cursor){
        cursor.moveToNext();
        ContentValues scheduleData= new ContentValues();
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_DAY, cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_DAY)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM, cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO, cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_SUBJECT_CODE, cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_SUBJECT_CODE)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TEACHER_NAME, cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TEACHER_NAME)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_ROOM_NUMBER, cursor.getString(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_ROOM_NUMBER)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_HOURS, cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_HOURS)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_MINUTES, cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_MINUTES)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_HOURS, cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_HOURS)));
        scheduleData.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_MINUTES, cursor.getInt(cursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_TO_MINUTES)));
        long result=sqLiteDatabase.insert(DatabaseContract.SchemaVaribles.TABLE_NAME, null, scheduleData);
        if (result!= -1)
            return true;
        return false;
    }

    public boolean updateRowById(ContentValues contentValues, long id){
        String whereClause= DatabaseContract.SchemaVaribles._ID+ "= ?";
        String[] whereArgs= {Long.toString(id)};
        sqLiteDatabase.update(
                DatabaseContract.SchemaVaribles.TABLE_NAME,
                contentValues,
                whereClause,
                whereArgs
        );
        return true;
    }

    public boolean updateAlarmStatus(int status, long id){
        sqLiteDatabase.execSQL("UPDATE "+ DatabaseContract.SchemaVaribles.TABLE_NAME+
                " SET "+ DatabaseContract.SchemaVaribles.COLUMN_NAME_ALARM_STATUS+ " = "+ status+
                " WHERE "+ DatabaseContract.SchemaVaribles._ID+ " = "+ id+"");
        return true;

    }
    // End of methods for add week schedule


    //Methods for add exam schedule
    //Update Exam Alarm Status
    public boolean updateExamAlarmStatus(int status, long id){
        sqLiteDatabase.execSQL("UPDATE "+ DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME+
                " SET "+ DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_ALARM_STATUS+ " = "+ status+
                " WHERE "+ DatabaseContract.SchemaVaribles._ID+ " = "+ id+ "");
        return true;
    }

    //Add new Exam Schedule to database
    public boolean addExam(ContentValues contentValues){
        long result= sqLiteDatabase.insert(
                DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME,
                null,
                contentValues
        );
        if(result!= -1)
            return true;
        return false;
    }

    public Cursor getExamSchedule(){
        return sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getExamScheduleById(long id){
        String selection= DatabaseContract.SchemaVaribles._ID+ "= ?";
        String[] selectionArgs= {Long.toString(id)};
        return sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
    }

    public int getExamAlarmStatus(long id){
        String selection= DatabaseContract.SchemaVaribles._ID+ "= ?";
        String[] selectionArgs= {Long.toString(id)};
        String[] columns= {DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_ALARM_STATUS};
        Cursor examStatusCursor= sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null

        );
        examStatusCursor.moveToNext();
        return examStatusCursor.getInt(examStatusCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_EXAM_ALARM_STATUS));

    }

    public boolean deleteExamSchedule(int id){
        return sqLiteDatabase.delete(DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME, DatabaseContract.SchemaVaribles._ID+ " = ?", new String[] {Integer.toString(id)})>0;
    }

    //End of methods for add exam schedule

    //Methods for EditExamSchedule Fragment

    public Cursor getExamCursorById(int id){
        String selection= DatabaseContract.SchemaVaribles._ID+ "= ?";
        String[] selectionArgs= {Integer.toString(id)};
        return sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
    }

    public boolean updateExam(ContentValues contentValues, int id){
        String whereClause= DatabaseContract.SchemaVaribles._ID+ "= ?";
        String whereArgs[]= {Integer.toString(id)};
        sqLiteDatabase.update(
                DatabaseContract.SchemaVaribles.EXAM_TABLE_NAME,
                contentValues,
                whereClause,
                whereArgs
        );
        return true;
    }

    /**
     * To Do list cursor
     */
    public Cursor getToDoListCursor(){
        return sqLiteDatabase.query(
                DatabaseContract.SchemaVaribles.TO_DO_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DatabaseContract.SchemaVaribles._ID
        );
    }

    public boolean addToDoTask(ContentValues contentValues){
        long result;
        result= sqLiteDatabase.insert(
                DatabaseContract.SchemaVaribles.TO_DO_TABLE_NAME,
                null,
                contentValues
        );
        if (result!= -1) {
            return true;
        }
        return false;
    }

    public boolean deleteToDoTask(int id){
        return sqLiteDatabase.delete(DatabaseContract.SchemaVaribles.TO_DO_TABLE_NAME, DatabaseContract.SchemaVaribles._ID+ " = ?", new String[] {Integer.toString(id)})>0;
    }


}
