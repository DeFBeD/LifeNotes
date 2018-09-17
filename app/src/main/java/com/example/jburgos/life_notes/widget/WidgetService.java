package com.example.jburgos.life_notes.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.jburgos.life_notes.data.AppDatabase;
import com.example.jburgos.life_notes.data.NoteEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.example.jburgos.life_notes.R;


public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewFactory(this.getApplicationContext());
    }
}

class WidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<NoteEntry> notes;
    private AppDatabase database;
    private static final String DATE_FORMAT = "MM/dd/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public WidgetRemoteViewFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        //initialize database
        database = AppDatabase.getInstance(mContext);
    }

    @Override
    public void onDataSetChanged() {
        notes = database.noteDao().loadAllNotesForWidget();
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

        String date = dateFormat.format(savedNotes.getDateView());
        String isFavorite = String.valueOf(savedNotes.getIsFavorite());
        int favorite = Integer.parseInt(isFavorite);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget);

        views.setTextViewText(R.id.widget_date_TextView, date);

        if (favorite == 1) {
            views.setImageViewResource(R.id.widget_bookmark, R.drawable.ic_bookmark_black_24dp);
        }

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
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


