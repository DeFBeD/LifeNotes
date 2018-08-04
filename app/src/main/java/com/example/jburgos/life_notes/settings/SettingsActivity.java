package com.example.jburgos.life_notes.settings;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.jburgos.life_notes.R;



public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }

}
