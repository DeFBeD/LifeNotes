package com.example.jburgos.life_notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jburgos.life_notes.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    static final int SETTINGS_INTENT_REPLY = 1;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    //@BindView(R.id.empty_view_main)
    //View emptyView;
    @BindView(R.id.bar)
    BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            Bundle bundle = getIntent().getExtras();

            MainFragment mainFragment = new MainFragment();
            mainFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, mainFragment)
                    .addToBackStack("tag")
                    .commit();

        }

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        MainFragment mainFragment = new MainFragment();
                        //mainFragment.setArguments(bundle);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, mainFragment)
                                .addToBackStack("main")
                                .commit();

                        return true;
                    case R.id.action_favorite:

                        fragmentManager = getSupportFragmentManager();
                        FavoriteFragment favoriteFragment = new FavoriteFragment();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, favoriteFragment)
                                .addToBackStack("favorite")
                                .commit();

                        return true;
                    case R.id.navigation_search:

                        fragmentManager = getSupportFragmentManager();
                        SearchFragment searchFragment = new SearchFragment();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, searchFragment)
                                .addToBackStack("search")
                                .commit();
                        return true;
                }
                return false;
            }


        });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addNewNoteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(addNewNoteIntent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplication(), SettingsActivity.class);
            startActivityForResult(settingsIntent, SETTINGS_INTENT_REPLY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
