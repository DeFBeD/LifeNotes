package com.example.jburgos.life_notes.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;

import java.util.List;

public class DescendingViewModel extends AndroidViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<NoteEntry>> notes;

    public DescendingViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG,"Actively Retrieving all Notes from database");
        notes = database.noteDao().loadAllNotesDescendingOrder();
    }

    public LiveData<List<NoteEntry>> getDescendingNotes() {
        return notes;
    }
}
