package com.example.jburgos.life_notes.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.adapter.MainNoteListAdapter;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.utils.AppExecutors;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment implements MainNoteListAdapter.ItemClickListener {

    private MainNoteListAdapter mAdapter;
    private AppDatabase dataBase;
    private List<NoteEntry> notes;
    public static final String EXTRA_NOTE_ID = "extraNoteId";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.search)
    SearchView search;
    @BindView(R.id.empty_view_search)
    View emptyView;

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

        mRecyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);

        //initialize database and set up the ViewModel on the List of notes
        dataBase = AppDatabase.getInstance(getContext());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MainNoteListAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        search.setSubmitButtonEnabled(true);
        search.setQueryHint(getString(R.string.search_note_hint));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                searchDb(s);
                mAdapter.setNotes(notes);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String n) {
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                searchDb(n);
                mAdapter.setNotes(notes);
                return true;
            }
        });

        return rootView;
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

    @Override
    public void onItemClickListener(int noteId) {
        FragmentTransaction transaction = ((FragmentActivity) Objects.requireNonNull(getContext()))
                .getSupportFragmentManager()
                .beginTransaction();
        BottomSheetFragment.newInstance(EXTRA_NOTE_ID, noteId).show(transaction, "bottom_sheet");
    }
}
