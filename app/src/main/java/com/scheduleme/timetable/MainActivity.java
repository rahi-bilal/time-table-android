package com.scheduleme.timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.scheduleme.timetable.database.DatabaseContract;
import com.scheduleme.timetable.database.ScheduleDbHelper;

public class MainActivity extends AppCompatActivity implements ScheduleFragment.OnEditSelectedListener
            , ExamScheduleFragment.ExamViewHolderItemClickListener, ScheduleFragment.OnDeleteSelectedListener
            ,ScheduleFragment.OnNotificationSelectedListener, HomeFragment.OnImageSelectedListener
            ,EmptyScheduleFragment.EmptyScheduleFragmentListener, ToDoFragment.OnToDoFragmentInteractionListener{

    private boolean exitFlag= false;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DatabaseUtility databaseUtility;

    private static Fragment fragment;
    private ScheduleDbHelper scheduleDbHelper;
    private Typeface fontAwesomeTypeFace;
    private Class fragmentClass;
    String day[]= {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private Map<String, Integer> daysMap= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup ActionBar
        setupActionBar();

        //setup Navigation item selection listener
        drawerLayout= findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.app_navigation_view);
        setupNavigationListener(navigationView);
        navigationView.getMenu().getItem(0).setChecked(true);

        //Setting current fragment to be HomeFragment in the bodyContainer layout
        fragment= new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.home_fragment_enter_fade_in, R.anim.home_fragment_exit_fade_out)
                .replace(R.id.body_layout, fragment)
                .commit();
        fragmentClass= null;

        //Adding backstack change listener
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentfragment= getCurrentFragment();
                if (currentfragment instanceof HomeFragment){
                    navigationView.setCheckedItem(R.id.home_activity);
                }
                if (currentfragment instanceof WeekScheduleFragment){
                    navigationView.setCheckedItem(R.id.view_week_schedule);
                }
                if (currentfragment instanceof ExamScheduleFragment){
                    navigationView.setCheckedItem(R.id.view_exam_schedule);
                }
                if (currentfragment instanceof AddScheduleFragment){
                    setTitle("Add Week Schedule");
                    navigationView.setCheckedItem(R.id.add_schedule);
                }
                if (currentfragment instanceof AddExamSchedule){
                    setTitle("Add Exam Schedule");
                    navigationView.setCheckedItem(R.id.add_exam);
                }
            }
        });

        //Instantiate ScheduleDbHelper
        scheduleDbHelper= new ScheduleDbHelper(getBaseContext());

        //Initializing fontAwesomeTypeFace
        fontAwesomeTypeFace= Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        //Initializing DatabaseUtility
        databaseUtility= new DatabaseUtility(this);

        //Initializing DaysMap
        daysMap.put("Sun", Calendar.SUNDAY);
        daysMap.put("Mon", Calendar.MONDAY);
        daysMap.put("Tue", Calendar.TUESDAY);
        daysMap.put("Wed", Calendar.WEDNESDAY);
        daysMap.put("Thu", Calendar.THURSDAY);
        daysMap.put("Fri", Calendar.FRIDAY);
        daysMap.put("Sat", Calendar.SATURDAY);



    }

    public Fragment getCurrentFragment(){
        return this.getSupportFragmentManager().findFragmentById(R.id.body_layout);
    }

    @Override
    protected void onResume() {
        if (fragment instanceof WeekScheduleFragment) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.body_layout, fragment).commit();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (getSupportFragmentManager().findFragmentById(R.id.body_layout) instanceof HomeFragment){
            if (exitFlag) {
                super.onBackPressed();
            } else{
                exitFlag= true;
                showExitSnackBar();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFlag= false;
                    }
                }, 2000);
                }
            }else{
                super.onBackPressed();
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.to_do:
                updateFragment(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupActionBar(){
        Toolbar toolbar= findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setupNavigationListener(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        updateFragment(item);
                        return true;
                    }
                }
        );
    }


    public void updateFragment(MenuItem item){
        FragmentManager fragmentManager= getSupportFragmentManager();
        Fragment currentFragment= fragmentManager.findFragmentById(R.id.body_layout);
        fragmentClass= null;
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.home_activity:
                if (!(currentFragment instanceof HomeFragment)) {
                    fragmentClass= HomeFragment.class;
                    for (int i=0; i<fragmentManager.getBackStackEntryCount(); i++){
                        fragmentManager.popBackStack();
                    }
                }
                break;
            case R.id.to_do:
                if (!(currentFragment instanceof ToDoFragment)) {
                    fragmentClass= ToDoFragment.class;
                }
                break;
            case R.id.add_schedule:
                if (!(currentFragment instanceof AddScheduleFragment)) {
                    fragmentClass= AddScheduleFragment.class;
                }
                break;
            case R.id.add_exam:
                if (!(currentFragment instanceof AddExamSchedule)) {
                    fragmentClass= AddExamSchedule.class;
                }
                break;
            case R.id.view_exam_schedule:
                if (!(currentFragment instanceof ExamScheduleFragment)) {
                    fragmentClass= ExamScheduleFragment.class;
                }
                break;
            case R.id.view_week_schedule:
                if (!(currentFragment instanceof WeekScheduleFragment)) {
                    fragmentClass= WeekScheduleFragment.class;
                }
                break;
            default:
                fragmentClass= null;
        }
        try {
            if (fragmentClass!=null){
                fragment= (Fragment) fragmentClass.newInstance();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (fragmentClass != null){
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
                    .replace(R.id.body_layout, fragment)
                    .addToBackStack(null)
                    .commit();
            setTitle(item.getTitle().toString());
        }

    }

    @Override
    public void onEditSelected(int id) {
        fragment= new EditScheduleFragment();
        Bundle bundle= new Bundle();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
         //viewPager.setVisibility(View.GONE);
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        setTitle("Edit Week Schedule");
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit);
        fragmentTransaction.replace(R.id.body_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onDeleteSelected(long id, final int itemIndex) {
        final DatabaseUtility databaseUtility= new DatabaseUtility(this);
        final Cursor cursor= databaseUtility.getCursorById(id);
        cursor.getCount();
        if(databaseUtility.removeItemById(id)){
            loadFragment(itemIndex);
            Snackbar snackbar= Snackbar.make(findViewById(R.id.body_layout), getString(R.string.check)+" Schedule deleted successful.", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (databaseUtility.undoDelete(cursor))
                                loadFragment(itemIndex);
                        }
                    })
                    .setActionTextColor(this.getResources().getColor(R.color.snackbarAction));
            View snackbarView= snackbar.getView();
            snackbarView.setMinimumHeight(150);
            snackbarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
            TextView snackBarTextView= snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            snackBarTextView.setTypeface(fontAwesomeTypeFace);
            snackbar.show();
        }
    }

    @Override
    public void onImageSelected(int id) {
        switch (id){
            case R.id.home_fragment_week_image_view:
                loadFragment(-1);
                break;
            case R.id.home_fragment_exam_image_view:
                loadFragment(-2);
                break;
            case R.id.home_fragment_to_do_image_view:
                loadFragment(-3);
                break;
            default:
                loadFragment(-1);
        }
    }

    private void loadFragment(int itemIndex){
        switch (itemIndex){
            case -1:
                fragment= new WeekScheduleFragment();
                setTitle("Week Schedule");
                break;
            case -2:
                fragment= new ExamScheduleFragment();
                setTitle("Exam Schedule");
                break;
            case -3:
                fragment= new ToDoFragment();
                setTitle("To Do");
                break;
            default:
                fragment= new WeekScheduleFragment();
                setTitle("Week Schedule");
        }
        if (itemIndex>=0){
            Bundle bundle= new Bundle();
            bundle.putInt("itemIndex", itemIndex);
            fragment.setArguments(bundle);
        }
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragmenmt_fade_out);
        fragmentTransaction.replace(R.id.body_layout, fragment);
        if (itemIndex>=0){
            fragmentTransaction.addToBackStack("DeleteUpdateFragment");
            fragmentManager.popBackStack("DeleteUpdateFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }else {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }

    private void loadAddScheduleFragment(int itemIndex){
        AddScheduleFragment addScheduleFragment= null;
        try {
            addScheduleFragment= AddScheduleFragment.newInstance(itemIndex);
        }catch (Exception e){
            e.printStackTrace();
        }
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragmenmt_fade_out);
        fragmentTransaction.replace(R.id.body_layout, addScheduleFragment).addToBackStack(null).commit();

    }

    private void loadToDoFragment(){
        ToDoFragment toDoFragment = ToDoFragment.newInstance();
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragmenmt_fade_out);
        fragmentTransaction.replace(R.id.body_layout, toDoFragment).addToBackStack("ToDoFragment").commit();
        fragmentManager.popBackStack("ToDoFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);


    }

    /**
     *
     * @param v
     * @param id
     * @param holder
     * @param itemIndex
     */
    @Override
    public void onNotificationSelected(View v, final long id, final RecyclerViewAdapter.RecyclerViewHolder holder, final int itemIndex) {
        PopupMenu popupMenu= new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.alarm_popup_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int alarmFromHour, alarmFromMinute;
                ImageView alarmImageView= holder.itemView.findViewById(R.id.alarm_image_view);
                Cursor holderCursor= databaseUtility.getCursorById(id);
                holderCursor.moveToNext();
                int fromHour= holderCursor.getInt(holderCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_HOURS));
                int fromMinute= holderCursor.getInt(holderCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_TIME_FROM_MINUTES));
                alarmFromHour= fromHour;
                alarmFromMinute= fromMinute;
                int status= holderCursor.getInt(holderCursor.getColumnIndex(DatabaseContract.SchemaVaribles.COLUMN_NAME_ALARM_STATUS));

                //removing alarm if previously set for this Schedule
                switch (status){
                    case 1:
                        removeNotification(holder, alarmFromHour, alarmFromMinute, itemIndex);
                        break;
                    case 2:
                        if (fromMinute>=15){
                            alarmFromMinute-=15;
                        }else{
                            alarmFromHour-=1;
                            alarmFromMinute= 45+alarmFromMinute;
                        }
                        removeNotification(holder, alarmFromHour, alarmFromMinute, itemIndex);
                        break;
                    case 3:
                        if (fromMinute>=30){
                        alarmFromMinute-=30;
                    }else {
                        alarmFromHour-=1;
                        alarmFromMinute= 30+alarmFromMinute;
                    }
                    removeNotification(holder, alarmFromHour, alarmFromMinute, itemIndex);
                    break;
                }

                //Adding alarm according to new item selection
                switch (item.getItemId()){
                    case R.id.off:
                        if(databaseUtility.updateAlarmStatus(0, id)){
                            alarmImageView.setImageDrawable(getDrawable(R.drawable.ic_alarm_off));
                            showUpdateSnackBar(0, fromHour, fromMinute);
                        }
                        break;
                    case R.id.on_time:
                        addNotification(holder, fromHour, fromMinute, itemIndex);
                        if(databaseUtility.updateAlarmStatus(1, id)){
                            alarmImageView.setImageDrawable(getDrawable(R.drawable.ic_alarm_on));
                            showUpdateSnackBar(1, fromHour, fromMinute);
                        }
                        break;
                    case R.id.fifteen:
                        alarmFromHour= fromHour; alarmFromMinute= fromMinute;
                        if (fromMinute>=15){
                            alarmFromMinute-=15;
                        }else {
                            alarmFromHour-=1;
                            alarmFromMinute= 45+fromMinute;
                        }
                        addNotification(holder, alarmFromHour, alarmFromMinute, itemIndex);
                        if(databaseUtility.updateAlarmStatus(2, id)){
                            alarmImageView.setImageDrawable(getDrawable(R.drawable.ic_alarm_on));
                            showUpdateSnackBar(2, fromHour, fromMinute);
                        }
                        break;
                    case R.id.thirty:
                        alarmFromHour= fromHour; alarmFromMinute= fromMinute;
                        if (fromMinute>=30){
                            alarmFromMinute-=30;
                        }else {
                            alarmFromHour-=1;
                            alarmFromMinute= 30+fromMinute;
                        }
                        addNotification(holder, alarmFromHour, alarmFromMinute, itemIndex);
                        if(databaseUtility.updateAlarmStatus(3, id)){
                            alarmImageView.setImageDrawable(getDrawable(R.drawable.ic_alarm_on));
                            showUpdateSnackBar(3, fromHour, fromMinute);
                        }
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    /**
     *
     * @param holder
     * @param fromHour
     * @param fromMinute
     * @param itemIndex
     */
    private void removeNotification(RecyclerViewAdapter.RecyclerViewHolder holder, int fromHour, int fromMinute, int itemIndex){
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, fromHour);
        calendar.set(Calendar.MINUTE, fromMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, daysMap.get(day[itemIndex]));
        AlarmManager alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent= new Intent(this, ScheduleAlarmReceiver.class);
        Bundle bundle= new Bundle();
        bundle.putString("time", fromHour+":"+fromMinute);
        intent.putExtra("bundle", bundle);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(pendingIntent);

    }

    /**
     *
     * @param holder
     * @param fromHour
     * @param fromMinute
     * @param itemIndex
     */
    private void addNotification(RecyclerViewAdapter.RecyclerViewHolder holder, int fromHour, int fromMinute, int itemIndex){
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, fromHour);
        calendar.set(Calendar.MINUTE, fromMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, daysMap.get(day[itemIndex]));
        AlarmManager alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent= new Intent(this, ScheduleAlarmReceiver.class);
        Bundle bundle= new Bundle();
        TextView timeTextView= (TextView)holder.itemView.findViewById(R.id.time_text_view);
        TextView subjectTextView= (TextView)holder.itemView.findViewById(R.id.subject_text_view);
        TextView roomTextView= (TextView)holder.itemView.findViewById(R.id.room_text_view);
        TextView teacherTextView= (TextView)holder.itemView.findViewById(R.id.teacher_text_view);
        String timeFrom= timeTextView.getText().toString();
        String subject= subjectTextView.getText().toString();
        String room= roomTextView.getText().toString();
        String teacher= teacherTextView.getText().toString();
        String[] data={timeFrom, subject, room, teacher};
        bundle.putStringArray("notificationData", data);
        intent.putExtra("bundle", bundle);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7 ,pendingIntent);
    }

    /**
     *
     * @param status
     * @param fromHour
     * @param fromMinute
     */
    public void showUpdateSnackBar(int status, int fromHour, int fromMinute){
        Snackbar snackbar;
        View snackBarView;
        TextView snackBarTextView;
        switch (status){
            case 0:
                snackbar= Snackbar.make(findViewById(R.id.body_layout), "Notification removed for this Schedule", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(this.getResources().getColor(R.color.snackbarAction));
                snackBarView= snackbar.getView();
                snackBarView.setMinimumHeight(150);
                snackBarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
                snackBarTextView= (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                snackBarTextView.setTypeface(fontAwesomeTypeFace);
                snackbar.show();
                break;
            case 1:
                snackbar= Snackbar.make(findViewById(R.id.body_layout), "Notification set On Time for this Schedule", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(this.getResources().getColor(R.color.snackbarAction));
                snackBarView= snackbar.getView();
                snackBarView.setMinimumHeight(150);
                snackBarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
                snackBarTextView= (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                snackBarTextView.setTypeface(fontAwesomeTypeFace);
                snackbar.show();
                break;
            case 2:
                snackbar= Snackbar.make(findViewById(R.id.body_layout), "Notification set 15 minutes before for this Schedule", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(this.getResources().getColor(R.color.snackbarAction));
                snackBarView= snackbar.getView();
                snackBarView.setMinimumHeight(150);
                snackBarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
                snackBarTextView= (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                snackBarTextView.setTypeface(fontAwesomeTypeFace);
                snackbar.show();
                break;
            case 3:
                snackbar= Snackbar.make(findViewById(R.id.body_layout), "Notification set 30 minutes before for this Schedule", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(this.getResources().getColor(R.color.snackbarAction));
                snackBarView= snackbar.getView();
                snackBarView.setMinimumHeight(150);
                snackBarView.setBackgroundColor(getResources().getColor(R.color.iconsBackground));
                snackBarTextView= (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                snackBarTextView.setTypeface(fontAwesomeTypeFace);
                snackbar.show();
                break;
            default:

        }
    }

    public void showExitSnackBar(){
        Snackbar exitSnackBar= Snackbar.make(findViewById(R.id.body_layout), "Press back again to Exit...", Snackbar.LENGTH_LONG);
        exitSnackBar.getView().setBackgroundColor(getResources().getColor(R.color.iconsBackground));
        exitSnackBar.show();
    }

    /**
     * Methods for Exam Schedule Started here
     * @param id
     */
    @Override
    public void onEditViewHolderClicked(int id) {
        fragment= new EditExamScheduleFragment();
        Bundle bundle= new Bundle();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        setTitle("Edit Exam Schedule");
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit);
        fragmentTransaction.replace(R.id.body_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDeleteViewHolderClicked(long id) {

    }

    /**
     * Method implementation for add ScheduleNow
     */
    @Override
    public void onAddScheduleNowButtonClicked(int position) {
        loadAddScheduleFragment(position);
    }

    @Override
    public void onToDoFragmentInteraction(int id) {
        if (databaseUtility.deleteToDoTask(id)){
            loadToDoFragment();
        }
    }
}
