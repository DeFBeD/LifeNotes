package com.example.jburgos.life_notes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.example.jburgos.life_notes.ViewModel.AddNoteViewModel;
import com.example.jburgos.life_notes.ViewModel.AddNoteViewModelFactory;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.utils.AppExecutors;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {

    private View mRootView;
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = NoteFragment.class.getSimpleName();

    //Member Variable for database
    private AppDatabase database;

    // Fields for views
    @BindView(R.id.editTextNoteDescription)
    EditText mEditText;
    @BindView(R.id.saveButton)
    Button mButton;

    private int noteId = DEFAULT_TASK_ID;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment NoteFragment.
     * public static NoteFragment newInstance(int noteId) {
    NoteFragment fragment = new NoteFragment();
    Bundle args = new Bundle();
    args.putInt(EXTRA_NOTE_ID, noteId);
    fragment.setArguments(args);
    return fragment;
    }
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this,mRootView);

        initViews();

        //initialize database
        database = AppDatabase.getInstance(getContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            noteId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }


        Bundle bundle = getArguments();
        if (bundle != null) {
            mButton.setText(R.string.update_button);
            if (noteId == DEFAULT_TASK_ID) {
                // populate the UI
                noteId = bundle.getInt(EXTRA_NOTE_ID, DEFAULT_TASK_ID);

                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(database, noteId);

                final AddNoteViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddNoteViewModel.class);

                viewModel.getNote().observe(this, new Observer<NoteEntry>() {
                    @Override
                    public void onChanged(@Nullable NoteEntry noteEntry) {
                        viewModel.getNote().removeObserver(this);
                        populateUI(noteEntry);
                    }
                });
            }
        }
        return mRootView;
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, noteId);
        super.onSaveInstanceState(outState);
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String description = mEditText.getText().toString();
        Date date = new Date();

        final NoteEntry note = new NoteEntry(description,date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (noteId == DEFAULT_TASK_ID) {
                    // insert new task
                    database.noteDao().insertNotes(note);
                    Log.d(TAG,"insert");

                } else {
                    //update task
                    note.setId(noteId);
                    database.noteDao().updateNotes(note);
                    Log.d(TAG,"update");
                }


            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param note the taskEntry to populate the UI
     */
    private void populateUI(NoteEntry note) {

        if(note == null){
            return;
        }

        mEditText.setText(note.getDescription());


    }

}
