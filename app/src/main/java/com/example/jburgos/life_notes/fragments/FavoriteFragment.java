package com.example.jburgos.life_notes.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.activities.ActivityEditNote;
import com.example.jburgos.life_notes.adapter.MainNoteListAdapter;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.viewModel.FavoriteViewModel;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteFragment extends Fragment implements MainNoteListAdapter.ItemClickListener {

    public static final String EXTRA_NOTE_ID = "extraNoteId";

    private MainNoteListAdapter mAdapter;
    private AppDatabase dataBase;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    View emptyView;


    public FavoriteFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MainNoteListAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        dataBase = AppDatabase.getInstance(getContext());
        setUpViewModel();

        return rootView;
    }

    //sets all the notes from the database as a viewModel
    public void setUpViewModel() {
        final FavoriteViewModel viewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<NoteEntry>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntry> noteEntries) {
                mAdapter.setNotes(noteEntries);
                toggleEmptyView(noteEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int noteId) {
        Intent intent = new Intent(getContext(), ActivityEditNote.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        startActivity(intent);
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
}
