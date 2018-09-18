package com.example.jburgos.life_notes.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.jburgos.life_notes.MainFragment;
import com.example.jburgos.life_notes.adapter.ViewPagerAdapter;
import com.example.jburgos.life_notes.fragments.FavoriteFragment;
import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.fragments.SearchFragment;
import com.example.jburgos.life_notes.reminderNotification.ReminderNotificationJob;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    static final String TAG = "show_notification";
    //fragments
    SearchFragment searchFragment;
    MainFragment mainFragment;
    FavoriteFragment favoriteFragment;
    MenuItem prevMenuItem;

    @BindView(R.id.bar)
    BottomNavigationView bottomBar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //schedule reminder
        scheduleJob(this);

        bottomBar.setOnNavigationItemSelectedListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomBar.getMenu().getItem(0).setChecked(false);
                }

                bottomBar.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomBar.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Disable ViewPager Swipe
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setupViewPager(viewPager);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        switch (menuItem.getItemId()) {
            case R.id.action_home:
                viewPager.setCurrentItem(0);
                setTitle(R.string.app_name);
                break;

            case R.id.action_favorite:
                viewPager.setCurrentItem(2);
                setTitle(getString(R.string.fragment_title_bookmarks));
                break;

            case R.id.navigation_search:
                viewPager.setCurrentItem(1);
                setTitle(getString(R.string.fragment_title_search));
                break;
        }

        return false;

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mainFragment = new MainFragment();
        searchFragment = new SearchFragment();
        favoriteFragment = new FavoriteFragment();
        adapter.addFragment(mainFragment);
        adapter.addFragment(searchFragment);
        adapter.addFragment(favoriteFragment);
        viewPager.setAdapter(adapter);
    }

    public static void scheduleJob(Context context) {
        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //creating new job and adding it with dispatcher
        Job job = createJob(dispatcher);
        dispatcher.mustSchedule(job);
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher) {

        Job job = dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                //.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //call this service when the criteria are met.
                .setService(ReminderNotificationJob.class)
                //unique id of the task
                .setTag(TAG)
                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(30, 60))
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        return job;
    }

}
