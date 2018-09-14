package com.example.jburgos.life_notes.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


import com.example.jburgos.life_notes.MainFragment;
import com.example.jburgos.life_notes.adapter.ViewPagerAdapter;
import com.example.jburgos.life_notes.fragments.FavoriteFragment;
import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.fragments.SearchFragment;
import com.example.jburgos.life_notes.reminderNotification.ReminderNotificationJob;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    SearchFragment searchFragment;
    MainFragment mainFragment;
    FavoriteFragment favoriteFragment;
    MenuItem prevMenuItem;


    @BindView(R.id.bar)
    BottomNavigationView bottomBar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ReminderNotificationJob.schedule();

        bottomBar.setOnNavigationItemSelectedListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomBar.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomBar.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomBar.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

         //Disable ViewPager Swipe
       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
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
                setTitle("Bookmarks");
                break;

            case R.id.navigation_search:
                viewPager.setCurrentItem(1);
                setTitle("Search Notes");
                break;
        }

        return false;

    }


    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mainFragment=new MainFragment();
        searchFragment=new SearchFragment();
        favoriteFragment=new FavoriteFragment();
        adapter.addFragment(mainFragment);
        adapter.addFragment(searchFragment);
        adapter.addFragment(favoriteFragment);
        viewPager.setAdapter(adapter);
    }
}
