package com.example.jburgos.life_notes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.jburgos.life_notes.adapter.MainNoteListAdapter;
import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;
import com.example.jburgos.life_notes.viewModel.FavoriteViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteActivity extends AppCompatActivity implements MainNoteListAdapter.ItemClickListener {

    public static final String EXTRA_NOTE_ID = "extraNoteId";

    private MainNoteListAdapter mAdapter;
    private AppDatabase dataBase;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_favorite);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.archive_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new MainNoteListAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        dataBase = AppDatabase.getInstance(getApplicationContext());
        setUpViewModel();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //Back Button to navigate back to the main screen
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
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
        Intent intent = new Intent(FavoriteActivity.this, AddNoteActivity.class);
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
