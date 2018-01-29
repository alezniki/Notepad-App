package com.alezniki.notepad.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.alezniki.notepad.R;
import com.alezniki.notepad.activities.DetailActivity;
import com.alezniki.notepad.model.Note;

import java.util.ArrayList;
import java.util.List;

import static com.alezniki.notepad.activities.MainActivity.DISPLAY_GREED;

/**
 * Note Adapter
 * <p>
 * Created by nikola aleksic on 6/25/17.
 */
@SuppressWarnings("ALL")
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> implements Filterable {

    /**
     * Key id
     */
    public static final String KEY_ID = "key_id";

    /**
     * Original list of notes
     */
    private final List<Note> notes;

    /**
     * Filtered list of notes
     * <p>
     * Store filtered results
     */
    private List<Note> filteredList;

    /**
     * Context
     */
    private final Context context;

    /**
     * Constructor
     *
     * @param context contexr
     * @param notes   list of notes
     */
    public NotesAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.filteredList = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Create new views (invoked by the layout manager)

        //If User selects the Grid Layout
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean hasGrid = preferences.getBoolean(DISPLAY_GREED, false);

        int layoutID = hasGrid ? R.layout.grid_items : R.layout.list_items;

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(layoutID, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Replace the contents of a view (invoked by the layout manager)
        //This is where you supply data that you want to display to the user

        //Get element from your data set at this position
        final Note note = filteredList.get(position);

        //Replace the contents of the view with that element
        holder.tvTitle.setText(note.getNoteTitle());
        holder.tvText.setText(note.getNoteText());

        //Add click listener to the view
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Unique ID to pass between activities;
                int keyID = note.getNoteID();

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(KEY_ID, keyID);

                //Navigate to Detail Activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //Return the size of your data set (invoked by the layout manager)
        return filteredList.size();
    }

    /**
     * Clear from adapter
     */
    public void clearFromAdapter() {

        int size = this.filteredList.size();

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.filteredList.remove(0);
            }

            this.notifyItemRangeChanged(0, size);
        }
    }

    /**
     * Add to adapter
     *
     * @param notes list of notes
     */
    public void addToAdapter(List<Note> notes) {
        this.filteredList.addAll(notes);
        this.notifyItemRangeInserted(0, notes.size() - 1);

    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Get the searched text via SearchView widget

                //Search view text input
                String input = constraint.toString();

                if (input.isEmpty()) {
                    filteredList = notes;
                } else {
                    // Loop through original list of notes
                    List<Note> list = new ArrayList<>();
                    for (Note note : notes) {

                        // Check if search contains the string and add it to a filtered list
                        if (note.getNoteTitle().toLowerCase().contains(input.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(input.toLowerCase())) {
                            list.add(note);
                        }
                    }

                    filteredList = list;
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Convert the FilterResults object to List object and update the adapter
                filteredList = (List<Note>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * View holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        //Note title
        public final TextView tvTitle;

        //Note text
        public final TextView tvText;

        //View to handle user events
        public final View view;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_note_title);
            tvText = (TextView) itemView.findViewById(R.id.tv_note_text);

            //Now the view reference will be available to the rest of the adapter
            view = itemView;
        }
    }
}
