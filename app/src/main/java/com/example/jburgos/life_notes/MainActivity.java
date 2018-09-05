package com.example.jburgos.life_notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jburgos.life_notes.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    static final int SETTINGS_INTENT_REPLY = 1;

    //@BindView(R.id.empty_view_main)
    //View emptyView;
    @BindView(R.id.bar)
    BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

    //Getting intent from settings and launching a desired layoutManager
    @Override
    public void onActivityResult(int settingsRequestCode, int settingsResultcode, Intent resultData) {
        super.onActivityResult(settingsRequestCode, settingsResultcode, resultData);

        if (settingsRequestCode == SETTINGS_INTENT_REPLY) {
            loadFragment(new MainFragment());
        }
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
