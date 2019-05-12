package com.scheduleme.timetable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scheduleme.timetable.database.DatabaseContract;
import com.scheduleme.timetable.database.ScheduleDbHelper;

public class ScheduleFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private SQLiteDatabase sqLiteDatabase;
    protected Bundle bundle;
    protected int fragmentIndex;
    private String day[]= {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    OnEditSelectedListener editSelectedListener;
    OnDeleteSelectedListener deleteSelectedListener;
    OnNotificationSelectedListener notificationSelectedListener;

    public interface OnEditSelectedListener{
        void onEditSelected(int id);
    }

    public interface OnDeleteSelectedListener{
        void onDeleteSelected(long id, int itemIndex);
    }

    public interface OnNotificationSelectedListener{
        void onNotificationSelected(View v, long id, RecyclerViewAdapter.RecyclerViewHolder holder, int itemIndex);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.schedule_fragment, container, false);

        //Getting today's Schedule
        bundle= getArguments();
        fragmentIndex= bundle.getInt(rootView.getResources().getString(R.string.position));
        final ScheduleDbHelper scheduleDbHelper= new ScheduleDbHelper(rootView.getContext());
        sqLiteDatabase= scheduleDbHelper.getWritableDatabase();
        final Cursor cursor= getTodaySchedule(day[fragmentIndex]);

        //Setting up RecyclerView
        recyclerView= rootView.findViewById(R.id.day_schedule_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager= new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter= new RecyclerViewAdapter(rootView.getContext(), cursor, fragmentIndex, new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, long id) {
                editSelectedListener.onEditSelected((int)id);
            }

            @Override
            public void onDeleteClick(View v, long id) {
                deleteSelectedListener.onDeleteSelected(id, fragmentIndex);
            }

            @Override
            public void onNotificationClicked(View v, long id, RecyclerViewAdapter.RecyclerViewHolder holder) {
                notificationSelectedListener.onNotificationSelected(v, id, holder, fragmentIndex);
            }
        });

        //Setting adapter for Schedule RecyclerView
        recyclerView.setAdapter(recyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            editSelectedListener= (OnEditSelectedListener) context;
            deleteSelectedListener= (OnDeleteSelectedListener) context;
            notificationSelectedListener= (OnNotificationSelectedListener) context;
        }catch (ClassCastException e){
            Toast.makeText(context, "Should implement Listeners", Toast.LENGTH_SHORT).show();
            throw new ClassCastException(context.toString()+ "must implement OnEditSelectedListener and OnDeleteSelectedListener");
        }
    }

    private Cursor getTodaySchedule(String day){
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

}
