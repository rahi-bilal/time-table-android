package com.scheduleme.timetable;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;

public class WeekScheduleFragment extends Fragment {
    private View rootView;
    private ViewPager weekScheduleViewPager;
    private PagerTabStrip weekViewPagerTabStrip;
    private ViewPagerAdapter weekViewPagerAdapter;
    private int itemIndex=-1;
    private Bundle bundle;
    private DatabaseUtility databaseUtility;
    private String[] day= {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String pagerTitle[]= {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseUtility= new DatabaseUtility(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.week_schedule_view_pager, container, false);

        if (getArguments()!=null){
            bundle= getArguments();
            itemIndex= bundle.getInt("itemIndex");
        }


        //Get Reference to ViewPager
        weekScheduleViewPager= rootView.findViewById(R.id.week_view_pager);
        weekViewPagerTabStrip= rootView.findViewById(R.id.week_view_pager_tab_strip);

        //Instantiate ViewPager with PagerAdapter
        weekViewPagerAdapter= new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        weekScheduleViewPager.setAdapter(weekViewPagerAdapter);
        //Setting current fragment to today's Schedule
        weekScheduleViewPager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);

        return rootView;
    }

    @Override
    public void onPause() {
        itemIndex= weekScheduleViewPager.getCurrentItem();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Changing title of toolbar when fragment attached to the activity
        getActivity().setTitle("Week Schedule");
        weekViewPagerAdapter= new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        weekScheduleViewPager.setAdapter(weekViewPagerAdapter);
        if (itemIndex!= -1){
            weekScheduleViewPager.setCurrentItem(itemIndex);

        }else {
            weekScheduleViewPager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
        }

    }


    /**
     * ViewPagerAdapterClass
     */
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle= new Bundle();
            bundle.putInt(getResources().getString(R.string.position), position);
            Cursor dayScheduleCursor= databaseUtility.getTodaySchedule(day[position]);
            if (dayScheduleCursor.getCount()<=0){
                EmptyScheduleFragment emptyScheduleFragment= new EmptyScheduleFragment();
                emptyScheduleFragment.setArguments(bundle);
                return emptyScheduleFragment;
            }else {
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                scheduleFragment.setArguments(bundle);
                return scheduleFragment;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pagerTitle[position];
        }

    }

}
