package com.example.jburgos.life_notes.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.viewModel.AddNoteViewModel;
import com.example.jburgos.life_notes.viewModel.AddNoteViewModelFactory;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.utils.AppExecutors;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityEditNote extends AppCompatActivity {

    private static final String TAG = ActivityEditNote.class.getSimpleName();

    // id being received through intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";

    // instance id for rotation
    public static final String NOTE_INSTANCE_ID = "instanceOfNoteId";
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_ID_FOR_NOTE = -1;
    private int noteId = DEFAULT_ID_FOR_NOTE;


    //variables for handling photo logic
    static final int REQUEST_TAKE_PHOTO = 1034;
    private String photoFileName = ".jpg";
    private File photoFile;
    private Uri photoUri;
    private Boolean isImageRemoved = false;
    private Boolean isImageTaken = false;

    //Member Variable for database
    private AppDatabase database;
    private int isFavorite;
    private String description;

    private static final String DATE_FORMAT = "MM/dd/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    String dateString;
    Date date;

    //firebase
    private FirebaseAnalytics mFirebaseAnalytics;

    // Fields for views
    @BindView(R.id.editTextNote)
    EditText editText;
    @BindView(R.id.saveButton)
    FloatingActionButton saveButton;
    @BindView(R.id.shareNoteContentsButton)
    ImageButton shareButton;
    @BindView(R.id.launchCameraButton)
    ImageButton picButton;
    @BindView(R.id.userTakenImage)
    ImageView image;
    @BindView(R.id.archiveTab)
    ImageButton bookmark;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.remove_pic_button)
    ImageButton removePicture;
    @BindView(R.id.editNotesToolbar)
    Toolbar addNoteToolbar;
    @BindView(R.id.header_text_editNote)
    TextView headerText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);
        if (null != addNoteToolbar) {
            addNoteToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

            addNoteToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(ActivityEditNote.this);
                }
            });
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setButtons();

        //initialize database
        database = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(NOTE_INSTANCE_ID)) {
            noteId = savedInstanceState.getInt(NOTE_INSTANCE_ID, DEFAULT_ID_FOR_NOTE);
        }

        headerText.setText("Add a Note");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {
            headerText.setText("Edit Note");
            if (noteId == DEFAULT_ID_FOR_NOTE) {
                // populate the UI
                noteId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_ID_FOR_NOTE);

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NOTE_INSTANCE_ID, noteId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        image.setVisibility(View.VISIBLE);
        if (photoUri == null || !isImageTaken) {
            removePicture.setVisibility(View.INVISIBLE);
        } else {
            removePicture.setVisibility(View.VISIBLE);
        }
    }

    /**
     * init all the buttons
     */
    private void setButtons() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });

        removePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setVisibility(View.INVISIBLE);
                removePicture.setVisibility(View.INVISIBLE);
                isImageRemoved = true;
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite == 0) {
                    isFavorite = 1;
                    bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                } else if (isFavorite == 1) {
                    isFavorite = 0;
                    bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                }
            }
        });

        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                isImageRemoved = false;
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNoteTextIntent(description);
            }
        });

    }

    //populate everything in the ui if theres a task id
    private void populateUI(NoteEntry note) {
        if (note == null) {
            return;
        }

        editText.setText(note.getDescription());
        description = note.getDescription();
        isFavorite = note.getIsFavorite();

        if (isFavorite == 1) {
            bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
        } else {
            bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }


        //constant to original date
        date = note.getDateView();

        dateString = dateFormat.format(note.getDateView());
        dateTextView.setText(dateString);

        photoUri = Uri.parse(note.getImage());
         isImageTaken = true;
        if (photoUri.toString().isEmpty()) {
            removePicture.setVisibility(View.INVISIBLE);
        } else {
            removePicture.setVisibility(View.VISIBLE);
            Glide.with(this).load(photoUri).into(image);
        }
    }

    /**
     * onSaveButtonClicked inserts or updates database
     */
    public void onSaveButtonClicked() {
        final String description = editText.getText().toString();
        if(date == null){
             date = new Date();
        }
        final int favorite = isFavorite;
        final String photoU;
        if (photoUri == null || isImageRemoved || !isImageTaken && isFavorite == 0) {
            photoU = "";
        } else {
            photoU = String.valueOf(photoUri);
        }

        fireBaseEvents(description);

        final NoteEntry note = new NoteEntry(description, date, favorite, photoU);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (noteId == DEFAULT_ID_FOR_NOTE) {
                    database.noteDao().insertNotes(note);
                    Log.d(TAG, "inserted: " + description + "favorite: " + String.valueOf(favorite) + "uri: " + photoU);
                } else {
                    note.setId(noteId);
                    database.noteDao().updateNotes(note);
                    Log.d(TAG, "inserted:" + description + "favorite:" + String.valueOf(favorite) + "uri:" + photoU);
                }

                finish();
            }
        });
    }

    private void shareNoteTextIntent(String description) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_TEXT, description);

        startActivity(Intent.createChooser(share, "Share note!"));
    }

    private void dispatchTakePictureIntent() {
        // create Intent to take a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        // required for API >= 24
        photoUri = FileProvider.getUriForFile(ActivityEditNote.this, "com.example.android.imageProvider", photoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        // check that a camera app can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    // Returns the File for a photoUri stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photoUri based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName + randomNumber());
    }

    //method that returns a random number to add to the image file name
    public static int randomNumber() {
        Random random = new Random();
        return random.nextInt(1000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Glide.with(this).load(takenImage).into(image);
                isImageTaken = true;

            } else { // Result was a failure
                Toast.makeText(this, R.string.pictureWasNotTaken, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fireBaseEvents(String description) {
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, noteId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, description);
        bundle.putInt(FirebaseAnalytics.Param.INDEX, isFavorite);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}


