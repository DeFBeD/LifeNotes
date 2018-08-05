package com.example.jburgos.life_notes.data;

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
    @ColumnInfo(name = "date_updated_at")
    private Date dateView;
    @ColumnInfo(name = "is_favorite")
    private int isFavorite;

    @Ignore
    public NoteEntry(String description, Date dateView, int isFavorite){
        this.description = description;
        this.dateView = dateView;
        this.isFavorite = isFavorite;

    }

    public NoteEntry(int id, String description, Date dateView, int isFavorite){
        this.id = id;
        this.description = description;
        this.dateView = dateView;
        this.isFavorite = isFavorite;

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

    public Date getDateView() {
        return dateView;
    }

    public void setDateView(Date dateView) {
        this.dateView = dateView;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}

