package com.example.jburgos.life_notes;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.jburgos.life_notes.Data.NoteEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This MainNoteListAdapter creates and binds ViewHolders, that hold the description and date of a note,
 * to a RecyclerView to efficiently display data.
 */
public class MainNoteListAdapter extends RecyclerView.Adapter<MainNoteListAdapter.NoteViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds note data and the Context
    private List<NoteEntry> noteEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the MainNoteListAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public MainNoteListAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new NoteViewHolder that holds the view for each note entry
     */
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the note_card_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_card_layout, parent, false);

        return new NoteViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        // Determine the values of the wanted data
        NoteEntry noteEntry = noteEntries.get(position);
        String description = noteEntry.getDescription();
        String updatedAtDateView = dateFormat.format(noteEntry.getUpdatedAtDateView());

        //Set values
        holder.taskDescriptionView.setText(description);
        holder.updatedAtDateView.setText(updatedAtDateView);

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (noteEntries == null) {
            return 0;
        }
        return noteEntries.size();
    }

    /**
     * When data changes, this method updates the list of noteEntries
     * and notifies the adapter to use the new values on it
     */
    public void setNotes(List<NoteEntry> noteEntries) {
        noteEntries = noteEntries;
        notifyDataSetChanged();
    }

    public List<NoteEntry> getNotes(){
        return noteEntries;
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the note description and date TextViews
        TextView taskDescriptionView;
        TextView updatedAtDateView;


        /**
         * Constructor for the NoteViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public NoteViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtDateView = itemView.findViewById(R.id.taskUpdatedAtDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = noteEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
