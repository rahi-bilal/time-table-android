package com.scheduleme.timetable;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeFragment extends Fragment {
    private View rootView;
    private ImageView examImageView;
    private ImageView weekImageView;
    private ImageView toDoImageView;
    private FragmentManager fragmentManager;

    OnImageSelectedListener imageSelectListener;

    public interface OnImageSelectedListener{
        void onImageSelected(int id);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating layout for home activity fragment
        rootView= inflater.inflate(R.layout.home_fragment, container, false);

        //getting reference of layout elements
        examImageView= rootView.findViewById(R.id.home_fragment_exam_image_view);
        weekImageView= rootView.findViewById(R.id.home_fragment_week_image_view);
        toDoImageView= rootView.findViewById(R.id.home_fragment_to_do_image_view);

        //setting animation for image view
        verticalFlipAnimation(weekImageView, 5000);
        verticalFlipAnimation(examImageView, 6000);
        verticalFlipAnimation(toDoImageView, 7000);

        //setting listeners for image view
        weekImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelectListener.onImageSelected(R.id.home_fragment_week_image_view);
            }
        });

        examImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelectListener.onImageSelected(R.id.home_fragment_exam_image_view);
            }
        });

        toDoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelectListener.onImageSelected(R.id.home_fragment_to_do_image_view);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            imageSelectListener= (HomeFragment.OnImageSelectedListener) context;
        }catch (ClassCastException e){
            Toast.makeText(context, "Should implement Listeners", Toast.LENGTH_SHORT).show();
            throw new ClassCastException(context.toString()+ "must implement OnEditSelectedListener and OnDeleteSelectedListener");
        }
    }

    @Override
    public void onResume() {
        getActivity().setTitle(getString(R.string.app_title));
        fragmentManager= getActivity().getSupportFragmentManager();
        for (int i= 0; i<fragmentManager.getBackStackEntryCount(); i++){
            fragmentManager.popBackStack();
        }
        super.onResume();
    }

    private void verticalFlipAnimation(View view, final int delay){
        final ObjectAnimator verticalFlipAnimator= ObjectAnimator.ofFloat(view, "translationY", 0f, 50f, 0f);
        verticalFlipAnimator.setDuration(2000);
        verticalFlipAnimator.setInterpolator(new BounceInterpolator());
        //verticalFlipAnimator.start();
        final Handler animationHandler= new Handler();
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                verticalFlipAnimator.start();
                animationHandler.postDelayed(this, delay);
            }
        };
        animationHandler.post(runnable);
    }
}
