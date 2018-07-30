package com.example.jburgos.life_notes.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notes")
public class NoteEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    @ColumnInfo(name = "updated_at")
    private Date updatedAtDateView;

    @Ignore
    public NoteEntry(String description, Date updatedAtDateView){
        this.description = description;
        this.updatedAtDateView = updatedAtDateView;

    }

    public NoteEntry(int id, String description, Date updatedAtDateView){
        this.id = id;
        this.description = description;
        this.updatedAtDateView = updatedAtDateView;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedAtDateView() {
        return updatedAtDateView;
    }

    public void setUpdatedAtDateView(Date updatedAtDateView) {
        this.updatedAtDateView = updatedAtDateView;
    }
}

