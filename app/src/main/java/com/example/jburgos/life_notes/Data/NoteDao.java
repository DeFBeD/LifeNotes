package com.example.jburgos.life_notes.Data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    //to query all entries (notes)
    @Query("SELECT * FROM notes ORDER BY updatedAtDateView")
    //every time we need to know if there is change in the database we have to call this method
    //so we wrap with LiveData
    LiveData<List<NoteEntry>> loadAllNotes();

    @Insert
    void insertNotes(NoteEntry noteEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNotes(NoteEntry noteEntry);

    @Delete
    void deleteNote(NoteEntry noteEntry);

    //to query individual task by id
    @Query("SELECT * FROM notes WHERE id = :id")
    LiveData<NoteEntry> loadNoteById(int id);
}
