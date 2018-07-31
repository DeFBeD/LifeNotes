package com.example.jburgos.life_notes;

import android.app.Fragment;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNoteActivity extends AppCompatActivity {

    // Extra for the note ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";



    //@BindView(R.id.pager)
    //ViewPager mPager;
    //private MyPagerAdapter mPagerAdapter;
    @BindView(R.id.up_container)
    View mUpButtonContainer;
    @BindView(R.id.action_up)
    View mUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

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


