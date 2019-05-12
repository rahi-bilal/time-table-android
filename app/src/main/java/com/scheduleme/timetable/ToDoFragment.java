package com.scheduleme.timetable;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.scheduleme.timetable.database.DatabaseContract;

public class ToDoFragment extends Fragment {
    private View rootView;
    private RecyclerView toDoRecyclerView;
    private ToDORecyclerViewAdapter toDORecyclerViewAdapter;
    private RecyclerView.LayoutManager toDoRecyclerViewLayoutManager;
    private DatabaseUtility databaseUtility;
    private Cursor cursor;
    private OnToDoFragmentInteractionListener toDoFragmentInteractionListener;

    public ToDoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ToDoFragment newInstance() {
        ToDoFragment fragment = new ToDoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_to_do, container, false);

        //Getting reference of toDoRecyclerView
        toDoRecyclerView= rootView.findViewById(R.id.to_do_recycler_view);

        //setting layoutManager for toDoRecyclerView
        toDoRecyclerView.setHasFixedSize(true);
        toDoRecyclerViewLayoutManager= new LinearLayoutManager(rootView.getContext());
        toDoRecyclerView.setLayoutManager(toDoRecyclerViewLayoutManager);

        //Initializing databaseUtility object
        databaseUtility= new DatabaseUtility(getContext());

        //setting Adapter for toDoRecyclerView
        toDORecyclerViewAdapter= new ToDORecyclerViewAdapter(getContext(), databaseUtility.getToDoListCursor(), new ToDoRecyclerViewInterface() {
            @Override
            public void onDoneClick(int id) {
                toDoFragmentInteractionListener.onToDoFragmentInteraction(id);
            }
        });
        toDoRecyclerView.setAdapter(toDORecyclerViewAdapter);




        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()){
            case R.id.to_do_menu_add:
                showAddDialog();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnToDoFragmentInteractionListener) {
            toDoFragmentInteractionListener = (OnToDoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.to_do_menu, menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        toDoFragmentInteractionListener = null;
    }

    public void showAddDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
            final View addDialogView= LayoutInflater.from(getContext()).inflate(R.layout.to_do_add_dialog, null);
            builder.setView(addDialogView);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText toDoTaskEditText= addDialogView.findViewById(R.id.to_do_task_edit_text);
                    String task= toDoTaskEditText.getText().toString();
                    ContentValues contentValues= new ContentValues();
                    contentValues.put(DatabaseContract.SchemaVaribles.COLUMN_NAME_TO_DO_CONTENT, task);
                    if (databaseUtility.addToDoTask(contentValues)){
                        toDORecyclerViewAdapter.swapCursor(databaseUtility.getToDoListCursor());
                    }

                }
            })
            .create().show();

    }

    public interface OnToDoFragmentInteractionListener {
        void onToDoFragmentInteraction(int id);
    }
}
