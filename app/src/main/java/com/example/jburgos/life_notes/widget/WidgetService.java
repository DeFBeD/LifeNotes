package com.example.jburgos.life_notes.widget;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.jburgos.life_notes.data.NoteEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.example.jburgos.life_notes.R;


public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewFactory(this.getApplicationContext());
    }
}

class WidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<NoteEntry> notes;

    public WidgetRemoteViewFactory(Context applicationContext) {
        mContext = applicationContext;
        notes = new ArrayList<>();

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("MyObject", "");


        Type type = new TypeToken<List<NoteEntry>>() {
        }.getType();
        notes = gson.fromJson(json, type);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {

        return (notes == null) ? 0 : notes.size();

    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (notes == null || notes.size() == 0) return null;

        NoteEntry savedNotes = notes.get(position);

        String date = String.valueOf(savedNotes.getDateView());
        String isFavorite = String.valueOf(savedNotes.getIsFavorite());
        int favorite = Integer.parseInt(isFavorite);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget);

        views.setTextViewText(R.id.widget_date_TextView, date);


        if (favorite == 1) {
            views.setImageViewResource(R.id.widget_bookmark, R.drawable.ic_bookmark_black_24dp);
        } else {
            views.setImageViewResource(R.id.widget_bookmark, R.drawable.ic_bookmark_border_black_24dp);
        }

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
    return 2;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}


