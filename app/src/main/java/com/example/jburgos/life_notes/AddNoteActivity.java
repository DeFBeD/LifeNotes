package com.example.jburgos.life_notes;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            Bundle bundle = getIntent().getExtras();

            NoteFragment notesDetailFragment = new NoteFragment();
            notesDetailFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.note_container, notesDetailFragment)
                    .commit();

        }
    }
}
