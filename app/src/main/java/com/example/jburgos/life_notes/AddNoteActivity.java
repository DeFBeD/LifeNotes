package com.example.jburgos.life_notes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jburgos.life_notes.viewModel.AddNoteViewModel;
import com.example.jburgos.life_notes.viewModel.AddNoteViewModelFactory;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.utils.AppExecutors;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNoteActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    // Extra for the task ID to be received after rotation
    public static final String NOTE_INSTANCE_ID = "instanceOfNoteId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddNoteActivity.class.getSimpleName();

    //Member Variable for database
    private AppDatabase database;
    private int isFavorite;

    // Fields for views
    @BindView(R.id.editTextNote)
    EditText mEditText;
    @BindView(R.id.saveButton)
    Button mButton;
    @BindView(R.id.favButton)
    Button favButton;

    private int mTaskId = DEFAULT_TASK_ID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);

        setButtons();

        //initialize database
        database = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(NOTE_INSTANCE_ID)) {
            mTaskId = savedInstanceState.getInt(NOTE_INSTANCE_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {
            mButton.setText(R.string.update_button);
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_TASK_ID);

                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(database, mTaskId);
                final AddNoteViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddNoteViewModel.class);

                //Observe the LiveData object in the ViewModel. Use it also when removing the observer
                viewModel.getNote().observe(this, new Observer<NoteEntry>() {
                    @Override
                    public void onChanged(@Nullable NoteEntry noteEntry) {
                        viewModel.getNote().removeObserver(this);
                        populateUI(noteEntry);
                    }
                });
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NOTE_INSTANCE_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * setButtons is called from onCreate to init the onclick for the save button
     */
    private void setButtons() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite == 0) {
                    isFavorite = 1;
                } else if (isFavorite == 1) {
                    isFavorite = 0;
                }
            }
        });
    }

    private void populateUI(NoteEntry note) {

        if (note == null) {
            return;
        }

        mEditText.setText(note.getDescription());
        isFavorite = note.getIsFavorite();

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        final String description = mEditText.getText().toString();
        final Date date = new Date();
        final int favorite = isFavorite;

        final NoteEntry note = new NoteEntry(description, date, favorite);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    // insert new task
                    database.noteDao().insertNotes(note);
                    Log.d(TAG, "inserted:" + description + "favorite:" + String.valueOf(favorite));
                } else {
                    //update task
                    note.setId(mTaskId);
                    database.noteDao().updateNotes(note);
                    Log.d(TAG, "inserted:" + description + "favorite:" + String.valueOf(favorite));
                }
                finish();
            }
        });
    }

}


