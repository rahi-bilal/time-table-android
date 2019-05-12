package com.scheduleme.timetable.database;

import android.provider.BaseColumns;

public class DatabaseContract {
    private DatabaseContract(){}

    public static class SchemaVaribles implements BaseColumns{
        /**
         * Week Schedule Schema variables
         */
        public static final String TABLE_NAME= "schedule_entry";
        public static final String COLUMN_NAME_DAY= "day";
        public static final String COLUMN_NAME_TIME_FROM= "time_from";
        public static final String COLUMN_NAME_TIME_TO= "time_to";
        public static final String COLUMN_NAME_SUBJECT_CODE= "subject_code";
        public static final String COLUMN_NAME_TEACHER_NAME= "teacher_name";
        public static final String COLUMN_NAME_ROOM_NUMBER= "room_number";
        public static final String COLUMN_NAME_TIME_FROM_HOURS= "time_from_hours";
        public static final String COLUMN_NAME_TIME_FROM_MINUTES= "time_from_minutes";
        public static final String COLUMN_NAME_TIME_TO_HOURS= "time_to_hours";
        public static final String COLUMN_NAME_TIME_TO_MINUTES= "time_to_minutes";
        public static final String COLUMN_NAME_ALARM_STATUS= "alarm_status";

        /**
         * Exam Schedule Schema Variables
         */
        public static final String EXAM_TABLE_NAME= "exam_schedule_entry";
        public static final String COLUMN_NAME_EXAM_SUBJECT= "subject";
        public static final String COLUMN_NAME_EXAM_DATE= "date";
        public static final String COLUMN_NAME_EXAM_TIME= "time";
        public static final String COLUMN_NAME_EXAM_TYPE= "type";
        public static final String COLUMN_NAME_EXAM_DAY_OF_MONTH= "day_of_month";
        public static final String COLUMN_NAME_EXAM_MONTH= "month";
        public static final String COLUMN_NAME_EXAM_YEAR= "year";
        public static final String COLUMN_NAME_EXAM_ALARM_STATUS= "status";

        /**
         * to do schema variables
         */
        public static final String TO_DO_TABLE_NAME= "to_do_entry";
        public static final String COLUMN_NAME_TO_DO_CONTENT= "to_do_content";

        /**
         * create week schedule entry table sql command
         */
        public static final String CREATE_TABLE_SCHEDULE=
                "CREATE TABLE "+ SchemaVaribles.TABLE_NAME+ "("+
                SchemaVaribles._ID+ " INTEGER PRIMARY KEY, "+
                SchemaVaribles.COLUMN_NAME_DAY+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_TIME_FROM+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_TIME_TO+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_TIME_FROM_HOURS+ " NUMBER,"+
                        SchemaVaribles.COLUMN_NAME_TIME_FROM_MINUTES+ " NUMBER,"+
                        SchemaVaribles.COLUMN_NAME_TIME_TO_HOURS+ " NUMBER,"+
                        SchemaVaribles.COLUMN_NAME_TIME_TO_MINUTES+ " NUMBER,"+
                SchemaVaribles.COLUMN_NAME_SUBJECT_CODE+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_TEACHER_NAME+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_ROOM_NUMBER+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_ALARM_STATUS+ " INTEGER DEFAULT 0 )";
        /**
         * delete week schedule entry table sql command
         */
        public static final String DELETE_TABLE_SCHEDULE=
                "DROP TABLE IF EXIST "+ SchemaVaribles.TABLE_NAME;

        /**
         * create exam schedule entry sql command
         */
        public static final String CREATE_EXAM_SCHEDULE_TABLE=
                "CREATE TABLE "+ SchemaVaribles.EXAM_TABLE_NAME+ "("+
                SchemaVaribles._ID+ " INTEGER PRIMARY KEY, "+
                SchemaVaribles.COLUMN_NAME_EXAM_SUBJECT+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_EXAM_DATE+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_EXAM_TIME+ " TEXT, "+
                SchemaVaribles.COLUMN_NAME_EXAM_DAY_OF_MONTH+ " INTEGER, "+
                        SchemaVaribles.COLUMN_NAME_EXAM_MONTH+ " INTEGER, "+
                        SchemaVaribles.COLUMN_NAME_EXAM_YEAR+ " INTEGER, "+
                SchemaVaribles.COLUMN_NAME_EXAM_ALARM_STATUS+ " INTEGER DEFAULT 0, "+
                SchemaVaribles.COLUMN_NAME_EXAM_TYPE+ " TEXT )";

        /**
         * delete exam schedule entry table sql command
         */
        public static final String DELETE_EXAM_SCHEDULE_TABLE=
                "DROP TABLE IF EXIST "+SchemaVaribles.EXAM_TABLE_NAME;
        /**
         * create to do table sql command
         */
        public static final String CREATE_TO_DO_TABLE=
                        "CREATE TABLE "+ TO_DO_TABLE_NAME+ "("+
                        SchemaVaribles._ID+ " INTEGER PRIMARY KEY, "+
                        SchemaVaribles.COLUMN_NAME_TO_DO_CONTENT+ " TEXT )";

    }
}
