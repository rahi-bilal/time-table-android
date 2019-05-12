package com.scheduleme.timetable;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ExamScheduleFragment extends Fragment {

    private View rootView;
    private RecyclerView examRecyclerView;
    private ExamRecyclerViewAdapter examRecyclerViewAdapter;
    private RecyclerView.LayoutManager examRecyclerViewLayoutManager;
    private Cursor cursor;
    private DatabaseUtility databaseUtility;
    private ExamViewHolderItemClickListener examViewHolderItemClickListener;

    public interface ExamViewHolderItemClickListener{
        void onEditViewHolderClicked(int id);
        void onDeleteViewHolderClicked(long id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.exam_schedule_fragment, container, false);

        //Getting layout of examRecyclerView
        examRecyclerView= rootView.findViewById(R.id.exam_schedule_recycler_view);

        //get data of exam schedule in cursor
        databaseUtility= new DatabaseUtility(getContext());
        cursor= databaseUtility.getExamSchedule();

        //setting examRecyclerView LayoutManager
        examRecyclerView.setHasFixedSize(true);
        examRecyclerViewLayoutManager= new LinearLayoutManager(rootView.getContext());
        examRecyclerView.setLayoutManager(examRecyclerViewLayoutManager);
        //setting adapter for examRecyclerView
        examRecyclerViewAdapter= new ExamRecyclerViewAdapter(getContext(), cursor, new ExamRecyclerViewItemClickListener() {
            @Override
            public void onEditClick(View view, long id) {
                examViewHolderItemClickListener.onEditViewHolderClicked((int)id);
            }
        });
        examRecyclerView.setAdapter(examRecyclerViewAdapter);

        // Returning the created fragment layout for ExamScheduleFragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Exam Schedule");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
           examViewHolderItemClickListener= (ExamViewHolderItemClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+ "must implement ExamViewHolderItemClickListener");
        }
    }
}
