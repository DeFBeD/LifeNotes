package com.example.jburgos.life_notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jburgos.life_notes.adapter.MainNoteListAdapter;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.utils.AppExecutors;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment implements MainNoteListAdapter.ItemClickListener {

    private MainNoteListAdapter mAdapter;
    private AppDatabase dataBase;
    private List<NoteEntry> notes;
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    private FirebaseAnalytics mFireBaseAnalytics;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.search)
    SearchView search;

    public SearchFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);

        //initialize database and set up the ViewModel on the List of notes
        dataBase = AppDatabase.getInstance(getContext());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MainNoteListAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        search.setSubmitButtonEnabled(true);
        search.setQueryHint("Search notes by date");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                searchDb(s);
                mAdapter.setNotes(notes);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String n) {
                searchDb(n);
                mAdapter.setNotes(notes);
                return true;
            }

            private void searchDb(String searchText) {
                searchText = "%" + searchText + "%";
                final String finalSearchText = searchText;
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        notes = dataBase.noteDao().loadNoteBySearch(finalSearchText);

                    }
                });
            }
        });

        return rootView;
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
}
