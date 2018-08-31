package com.example.jburgos.life_notes;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.example.jburgos.life_notes.adapter.MainNoteListAdapter;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.reminderNotification.ReminderNotificationJob;
import com.example.jburgos.life_notes.settings.SettingsActivity;
import com.example.jburgos.life_notes.utils.AppExecutors;
import com.example.jburgos.life_notes.viewModel.MainViewModel;
import com.example.jburgos.life_notes.widget.WidgetProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements MainNoteListAdapter.ItemClickListener {

    static final int SETTINGS_INTENT_REPLY = 1;
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_NOTE_ID = "extraNoteId";

    private MainNoteListAdapter mAdapter;
    private AppDatabase dataBase;
    private FirebaseAnalytics mFireBaseAnalytics;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        ReminderNotificationJob.schedule();
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getContext()));


        //chooses layout preference of the user to display the list
        chooseLayout();

        //Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
        swipeHandler();

        //initialize database and set up the ViewModel on the List of notes
        dataBase = AppDatabase.getInstance(getContext());

        mFireBaseAnalytics.logEvent("addNewNoteIntent", null);
        mFireBaseAnalytics.logEvent("chooseLayout", null);
        mFireBaseAnalytics.setAnalyticsCollectionEnabled(true);


        return rootView;
    }

    private void swipeHandler() {
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
   public void onStart() {
        super.onStart();
        updateWidget();
    }

    @Override
   public  void onStop() {
        super.onStop();
        updateWidget();
    }

    //gets shared preference and sets appropriate layout and sets up views
    private void chooseLayout() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        String orderType = sharedPrefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.pref_list_view));

        if (orderType.equals(getString(R.string.pref_list_view))) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            setUpAdapter();
            setUpViewModel();
        } else if (orderType.equals(getString(R.string.pref_grid_view))) {
            GridLayoutManager grid =
                    new GridLayoutManager(getContext(), 2);
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
        mAdapter = new MainNoteListAdapter(getContext(), this);
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
                //set empty view on RecyclerView, so it shows when the list has 0 items
                //toggleEmptyView(noteEntries);

            }
        });

    }

    private void updateWidget() {
        Intent intent = new Intent(getContext(), WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getActivity(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(intent);
    }


    @Override
    public void onItemClickListener(int noteId) {
        Intent intent = new Intent(getContext(), AddNoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        startActivity(intent);
        // [START custom_event]
        Bundle params = new Bundle();
        params.putInt("note_id", noteId);
        mFireBaseAnalytics.logEvent("note_id_intent", params);
        // [END custom_event]

    }

    /*
    public void toggleEmptyView(List<NoteEntry> noteEntries) {
        //set empty view on RecyclerView, so it shows when the list has 0 items
        if (noteEntries.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

    }
    */
}
