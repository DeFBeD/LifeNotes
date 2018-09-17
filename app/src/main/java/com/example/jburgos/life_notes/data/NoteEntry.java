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
    @ColumnInfo(name = "edited_date_updated_at")
    private Date editedDateView;
    @ColumnInfo(name = "is_favorite")
    private int isFavorite;
    @ColumnInfo(name = "photo_path")
    private String image;

    @Ignore
    public NoteEntry(String description, Date dateView, int isFavorite, String image, Date editedDateView) {
        this.description = description;
        this.dateView = dateView;
        this.isFavorite = isFavorite;
        this.image = image;
        this.editedDateView = editedDateView;

    }

    public NoteEntry(int id, String description, Date dateView, int isFavorite, String image, Date editedDateView) {
        this.id = id;
        this.description = description;
        this.dateView = dateView;
        this.isFavorite = isFavorite;
        this.image = image;
        this.editedDateView = editedDateView;

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

    public Date getEditedDateView() {
        return editedDateView;
    }

    public void setEditedDateView(Date editedDateView) {
        this.editedDateView = editedDateView;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

