package com.example.jburgos.life_notes.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {NoteEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)//Shows Room How to deal with Date
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "lifeNotes";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {

        //sInstance makes sure I only have one Instance of this database, Singleton
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new Database Instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting database Instance");
        return sInstance;
    }

    public abstract NoteDao noteDao();
}
