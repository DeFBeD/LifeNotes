package com.example.jburgos.life_notes;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.activities.ActivityEditNote;
import com.example.jburgos.life_notes.activities.MainActivity;
import com.example.jburgos.life_notes.adapter.MainNoteListAdapter;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.fragments.BottomSheetFragment;
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
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.empty_view_main)
    View emptyView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getContext()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addNewNoteIntent = new Intent(getContext(), ActivityEditNote.class);
                startActivity(addNewNoteIntent);
            }
        });


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

    @Override
    public void onStart() {
        super.onStart();
        updateWidget();
    }

    @Override
    public void onStop() {
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
                mAdapter.setNotes(noteEntries);
                //set empty view on RecyclerView, so it shows when the list has 0 items
                toggleEmptyView(noteEntries);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivityForResult(settingsIntent, SETTINGS_INTENT_REPLY);
                return true;
            case R.id.deleteAll:
                if (mAdapter.getItemCount() == 0) {
                    Toast.makeText(getContext(), "Nothing to delete", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog dialog = AlertDialog();
                    dialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //Getting intent from settings and launching a desired layoutManager
    @Override
    public void onActivityResult(int settingsRequestCode, int settingsResultcode, Intent resultData) {
        super.onActivityResult(settingsRequestCode, settingsResultcode, resultData);

        if (settingsRequestCode == SETTINGS_INTENT_REPLY) {
            chooseLayout();
        }
    }

    private void updateWidget() {
        Intent intent = new Intent(getContext(), WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(Objects.requireNonNull(getActivity()), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onItemClickListener(int noteId) {
        FragmentTransaction transaction = ((FragmentActivity) Objects.requireNonNull(getContext()))
                .getSupportFragmentManager()
                .beginTransaction();
        BottomSheetFragment.newInstance(EXTRA_NOTE_ID, noteId).show(transaction, "bottom_sheet");

    }

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

    private AlertDialog AlertDialog() {
        return new AlertDialog.Builder(getContext())
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete All notes?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                dataBase.noteDao().deleteAllNote();
                            }
                        });
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .create();

    }

}
