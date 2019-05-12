package com.scheduleme.timetable;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;


public class EmptyScheduleFragment extends Fragment {
    private View rootView;
    private ImageView funImageView;
    private Button addScheduleNowButton;
    private Random random;
    private int itemIndex;
    private int[] funImageId= {R.drawable.ic_man_playing_basketball, R.drawable.ic_playstation, R.drawable.ic_sports1, R.drawable.ic_sports2};
    private EmptyScheduleFragmentListener emptyScheduleFragmentListener;

    public interface EmptyScheduleFragmentListener{
        void onAddScheduleNowButtonClicked(int position);
    }

    public EmptyScheduleFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        random= new Random();
        if (getArguments()!= null){
            itemIndex= getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_empty_schedule, container, false);

        //Getting reference to views int the fragment
        funImageView= rootView.findViewById(R.id.fun_image_view);
        addScheduleNowButton= rootView.findViewById(R.id.empty_fragment_add_schedule_button);

        //Setting dynamic image resource to funImageView
        funImageView.setImageResource(funImageId[random.nextInt(4)]);

        //underlying the text of the addScheduleNowButton
        addScheduleNowButton.setPaintFlags(addScheduleNowButton.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        //Adding on click listener for addScheduleNowButton to open addWeekScheduleFragment
        addScheduleNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyScheduleFragmentListener.onAddScheduleNowButtonClicked(itemIndex);
            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            emptyScheduleFragmentListener= (EmptyScheduleFragmentListener) context;
        }catch (ClassCastException e){
            Toast.makeText(context, "Must Implement interaction listener in containing class.", Toast.LENGTH_SHORT).show();
            throw new ClassCastException();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        emptyScheduleFragmentListener= null;
    }

}
