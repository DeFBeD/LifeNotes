package com.example.jburgos.life_notes.data;

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
    @Query("SELECT * FROM notes ORDER BY date_updated_at")
    //every time we need to know if there is change in the database we have to call this method
    //so we wrap with LiveData
    LiveData<List<NoteEntry>> loadAllNotes();

    //to query all entries (notes)
    @Query("SELECT * FROM notes ORDER BY date_updated_at DESC")
    //every time we need to know if there is change in the database we have to call this method
    //so we wrap with LiveData
    LiveData<List<NoteEntry>> loadAllNotesDecsendingOrder();

    //to query all task by favorite and supply the widget
    @Query("SELECT * FROM notes ORDER BY date_updated_at")
    List<NoteEntry> loadAllNotesForWidget();

    @Insert
    void insertNotes(NoteEntry noteEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNotes(NoteEntry noteEntry);

    @Delete
    void deleteNote(NoteEntry noteEntry);

    @Query("DELETE FROM notes")
    void deleteAllNote();

    //to query individual task by id
    @Query("SELECT * FROM notes WHERE id = :id")
    LiveData<NoteEntry> loadNoteById(int id);

    //to query with the searchView
    @Query("SELECT * FROM notes WHERE description LIKE :search")
    List<NoteEntry> loadNoteBySearch(String search);

    //to query all task by favorite
    @Query("SELECT * FROM notes WHERE is_favorite = 1")
    LiveData<List<NoteEntry>> loadAllFavorites();

}
