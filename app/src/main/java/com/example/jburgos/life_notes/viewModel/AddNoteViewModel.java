package com.example.jburgos.life_notes.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;

public class AddNoteViewModel extends ViewModel {

    private LiveData<NoteEntry> note;

    public AddNoteViewModel(AppDatabase database, int noteId) {
        note = database.noteDao().loadNoteById(noteId);
    }

    public LiveData<NoteEntry> getNote() {
        return note;
    }
}
