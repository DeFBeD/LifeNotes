package com.example.jburgos.life_notes.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {NoteEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)//Shows Room How to deal with Date
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "lifeNotes";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {

        //sInstance makes sure I only have one Instance of this database, Singleton
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }

        return sInstance;
    }

    public abstract NoteDao noteDao();
}
