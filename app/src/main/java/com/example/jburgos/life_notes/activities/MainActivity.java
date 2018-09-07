package com.example.jburgos.life_notes.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;


import com.example.jburgos.life_notes.fragments.FavoriteFragment;
import com.example.jburgos.life_notes.fragments.MainFragment;
import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.fragments.SearchFragment;
import com.example.jburgos.life_notes.reminderNotification.ReminderNotificationJob;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //@BindView(R.id.empty_view_main)
    //View emptyView;
    @BindView(R.id.bar)
    BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ReminderNotificationJob.schedule();

        bottomBar.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new MainFragment());

        }

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();

            return true;

        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.action_home:

                fragment = new MainFragment();
                break;

            case R.id.action_favorite:

                fragment = new FavoriteFragment();
                break;

            case R.id.navigation_search:

                fragment = new SearchFragment();
                break;
        }

        return loadFragment(fragment);

    }
}