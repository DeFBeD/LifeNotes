package com.example.jburgos.life_notes.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;

import java.util.List;

public class DescendingViewModel extends AndroidViewModel {

    private LiveData<List<NoteEntry>> notes;

    public DescendingViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        notes = database.noteDao().loadAllNotesDescendingOrder();
    }

    public LiveData<List<NoteEntry>> getDescendingNotes() {
        return notes;
    }
}
