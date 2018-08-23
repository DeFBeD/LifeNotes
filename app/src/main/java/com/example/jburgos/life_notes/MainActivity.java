package com.example.jburgos.life_notes;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.example.jburgos.life_notes.reminderNotification.ReminderNotificationJob;
import com.example.jburgos.life_notes.settings.SettingsActivity;
import com.example.jburgos.life_notes.viewModel.MainViewModel;
import com.example.jburgos.life_notes.adapter.MainNoteListAdapter;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.utils.AppExecutors;
import com.example.jburgos.life_notes.widget.WidgetProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MainNoteListAdapter.ItemClickListener {

    static final int SETTINGS_INTENT_REPLY = 1;
    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_NOTE_ID = "extraNoteId";

    private MainNoteListAdapter mAdapter;
    private AppDatabase dataBase;
    private FirebaseAnalytics mFireBaseAnalytics;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ReminderNotificationJob.schedule();
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);

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

        chooseLayout();
        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<NoteEntry> note = mAdapter.getNotes();
                        dataBase.noteDao().deleteNote(note.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        //initialize database and set up the ViewModel on the List of notes
        dataBase = AppDatabase.getInstance(getApplicationContext());

        mFireBaseAnalytics.logEvent("addNewNoteIntent", null);
        mFireBaseAnalytics.logEvent("chooseLayout", null);
        mFireBaseAnalytics.setAnalyticsCollectionEnabled(true);

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

        if (id == R.id.action_favorite) {
            Intent settingsIntent = new Intent(getApplication(), FavoriteActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Getting intent from settings and launching a desired layoutManager
    @Override
    public void onActivityResult(int settingsRequestCode, int settingsResultcode, Intent resultData) {
        super.onActivityResult(settingsRequestCode, settingsResultcode, resultData);

        if (settingsRequestCode == SETTINGS_INTENT_REPLY) {
            chooseLayout();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpWidget();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setUpWidget();
    }

    //gets shared preference and sets appropriate layout and sets up views
    private void chooseLayout() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getApplication());
        String orderType = sharedPrefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.pref_list_view));

        if (orderType.equals(getString(R.string.pref_list_view))) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            setUpAdapter();
            setUpViewModel();
        } else if (orderType.equals(getString(R.string.pref_grid_view))) {
            GridLayoutManager grid =
                    new GridLayoutManager(getApplicationContext(), 2);
            mRecyclerView.setLayoutManager(grid);
            setUpAdapter();
            setUpViewModel();
        } else if (orderType.equals(getString(R.string.pref_staggered_view))) {
            StaggeredGridLayoutManager grid =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(grid);
            setUpAdapter();
            setUpViewModel();
        }
    }

    private void setUpAdapter() {
        mAdapter = new MainNoteListAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    //sets all the notes from the database as a viewModel
    public void setUpViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getNotes().observe(this, new Observer<List<NoteEntry>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntry> noteEntries) {
                Log.d(TAG, "updating from LiveData in ViewModel");
                mAdapter.setNotes(noteEntries);
            }
        });

    }

    private void setUpWidget() {
        Intent intent = new Intent(getApplicationContext(), WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getBaseContext()).getAppWidgetIds(new ComponentName(getBaseContext(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getBaseContext().sendBroadcast(intent);
    }


    @Override
    public void onItemClickListener(int noteId) {
        Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        startActivity(intent);

        // [START custom_event]
        Bundle params = new Bundle();
        params.putInt("note_id", noteId);
        mFireBaseAnalytics.logEvent("note_id_intent", params);
        // [END custom_event]

    }

}
