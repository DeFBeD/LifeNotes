package com.example.jburgos.life_notes.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.activities.ActivityEditNote;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.viewModel.AddNoteViewModel;
import com.example.jburgos.life_notes.viewModel.AddNoteViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomSheetFragment extends BottomSheetDialogFragment {


    //constants for note Id and logic
    public static String EXTRA_NOTE_ID = "extraNoteId";
    public static final String NOTE_ID = "noteId";
    public static final String STRING_NOTE_ID = "stringNoteId";
    public static final String NOTE_INSTANCE_ID = "instanceOfNoteId";
    private static final int DEFAULT_TASK_ID = -1;
    private int mTaskId = DEFAULT_TASK_ID;

    String photoUri;

    // Fields for views
    @BindView(R.id.bottom_Sheet_TextNote)
    TextView textForNotes;
    @BindView(R.id.editNoteButton)
    ImageButton editNoteSheetButton;
    @BindView(R.id.userTakenImage)
    ImageView image;
    @BindView(R.id.archiveTab)
    ImageView bookmark;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.editedDateTextView)
    TextView lastUpdatedDateTextView;

    //date logic
    private static final String DATE_FORMAT = "MM/dd/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    //new instance
    public static BottomSheetFragment newInstance(String noteExtra, int noteId) {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(STRING_NOTE_ID, noteExtra);
        bundle.putInt(NOTE_ID, noteId);
        bottomSheetFragment.setArguments(bundle);
        return bottomSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet, container, false);
        ButterKnife.bind(this, root);
        //initialize database
        AppDatabase dataBase = AppDatabase.getInstance(getContext());

        Bundle bundle = getArguments();

        if (savedInstanceState != null && savedInstanceState.containsKey(NOTE_INSTANCE_ID)) {
            mTaskId = savedInstanceState.getInt(NOTE_INSTANCE_ID, DEFAULT_TASK_ID);
        }

        if (mTaskId == DEFAULT_TASK_ID) {
            // populate the UI
            mTaskId = bundle.getInt(NOTE_ID);

            setUpViewModel(dataBase);
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(NOTE_INSTANCE_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    //populate views in activity
    private void populateUI(NoteEntry note) {

        if (note == null) {
            return;
        }

        textForNotes.setText("\t\t" + note.getDescription());
        int isFavorite = note.getIsFavorite();

        if (isFavorite == 1) {
            bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
        } else {
            bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }

        String date = dateFormat.format(note.getDateView());
        dateTextView.setText(date);//constant to original date; logic

        String updatedDateString = dateFormat.format(note.getEditedDateView());
        if (updatedDateString.equals(date)) {
            lastUpdatedDateTextView.setVisibility(View.GONE);

        } else {
            lastUpdatedDateTextView.setText("last edit: " + updatedDateString);
        }

        photoUri = String.valueOf(Uri.parse(note.getImage()));

        Glide.with(this).load(photoUri).into(image);

        editNoteSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityEditNote.class);
                intent.putExtra(EXTRA_NOTE_ID, mTaskId);
                startActivity(intent);
                dismiss();
            }
        });

    }

    private void setUpViewModel(AppDatabase dataBase) {
        AddNoteViewModelFactory factory = new AddNoteViewModelFactory(dataBase, mTaskId);
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