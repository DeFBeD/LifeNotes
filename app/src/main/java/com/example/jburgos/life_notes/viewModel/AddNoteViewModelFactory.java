package com.example.jburgos.life_notes.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.jburgos.life_notes.data.AppDatabase;

public class AddNoteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase dataBase;
    private final int noteId;

    public AddNoteViewModelFactory(AppDatabase dataBase, int noteId) {
        this.dataBase = dataBase;
        this.noteId = noteId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddNoteViewModel(dataBase, noteId);
    }
}
