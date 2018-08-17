package com.example.jburgos.life_notes.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.jburgos.life_notes.R;
import com.example.jburgos.life_notes.data.NoteEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainNoteListAdapter extends RecyclerView.Adapter<MainNoteListAdapter.NoteViewHolder> {

    // Constant for date format & Date formatter
    private static final String DATE_FORMAT = "MM/dd/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // variables for the List of notes and Context
    private List<NoteEntry> noteEntries;
    private Context mContext;

    //handle item clicks
    final private ItemClickListener mItemClickListener;


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
        String updatedAtDateView = dateFormat.format(noteEntry.getDateView());
        String imageUri = noteEntry.getImage();

        //Set values
        holder.noteContentsView.setText(description);
        holder.updateTheDateView.setText(updatedAtDateView);

        if(imageUri != null){
            Glide.with(mContext).load(imageUri).into(holder.imageCard);
        }

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
    public void setNotes(List<NoteEntry> notes) {
        noteEntries = notes;
        notifyDataSetChanged();
    }

    public List<NoteEntry> getNotes(){
        return noteEntries;
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
        //void deleteOnClick(int position);
    }

    // Inner class for creating ViewHolders
    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the note description and date TextViews
        TextView noteContentsView;
        TextView updateTheDateView;
        ImageView imageCard;
        //ImageButton delete;


        /**
         * Constructor for the NoteViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public NoteViewHolder(View itemView) {
            super(itemView);

            noteContentsView = itemView.findViewById(R.id.contentInNotes);
            updateTheDateView = itemView.findViewById(R.id.updateTheDate);
            imageCard = itemView.findViewById(R.id.image_card);
            /*
            delete = itemView.findViewById(R.id.deleteButton);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.deleteOnClick(getAdapterPosition());
                }
            });
            */
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = noteEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
