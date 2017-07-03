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
import com.alezniki.notepad.model.Notes;

import java.util.ArrayList;
import java.util.List;

import static com.alezniki.notepad.activities.MainActivity.DISPLAY_GREED;

/**
 * Created by nikola on 6/25/17.
 */

@SuppressWarnings("ALL")
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> implements Filterable {
    public static final String KEY_ID = "key_id";

    private final List<Notes> listOfNotes; // original list of items
    private List<Notes> filteredList; // store filtered results
    private final Context context;

    public NotesAdapter(Context context, List<Notes> list) {
        this.context = context;
        this.listOfNotes = list;
        this.filteredList = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // If User selects the Grid Layout
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean grid = preferences.getBoolean(DISPLAY_GREED, false);

        int layoutID = grid ? R.layout.grid_items : R.layout.list_items;

        LayoutInflater inflater = LayoutInflater.from(context);
//        View itemView = inflater.inflate(R.layout.list_items, parent, false);
        View itemView = inflater.inflate(layoutID, parent, false);

        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // This is where you supply data that you want to display to the user
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get element from your data set at this position
        final Notes note = filteredList.get(position);

        // Replace the contents of the view with that element
//        holder.tvTitle.setText(note.getNoteTitle());
        holder.tvTitle.setText(filteredList.get(position).getNoteTitle());
//        holder.tvText.setText(note.getNoteText());
        holder.tvText.setText(filteredList.get(position).getNoteText());

        // Add click listener to the view
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "You selected: " + note.getNoteTitle(), Toast.LENGTH_SHORT).show();

                // Navigate to Detail Activity
                int keyID = note.getNoteID(); // Unique ID to pass between activities;
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(KEY_ID, keyID);
                context.startActivity(intent);
            }
        });

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void clearFromAdapter() {
        int size = this.filteredList.size();

        if (size > 0) {
            for (int i = 0; i < size ; i++) {
                this.filteredList.remove(0);
            }
            this.notifyItemRangeChanged(0, size);
        }
    }

    public void addToAdapter(List<Notes> list) {
        this.filteredList.addAll(list);
        this.notifyItemRangeInserted(0, list.size()-1);

    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            // Get the searched text via SearchView widget
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Search View text input
                String str = constraint.toString();

                if (str.isEmpty()) {
                   filteredList = listOfNotes;
                } else {
                    // Loop through original list of notes
                    List<Notes> list = new ArrayList<>();
                    for (Notes note : listOfNotes) {

                        // Check if search contains the string and add it to a filtered list
                        if (note.getNoteTitle().toLowerCase().contains(str.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(str.toLowerCase())) {
                            list.add(note);
                        }
                    }

                    filteredList = list;
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            // Convert the FilterResults object to List object and update the adapter
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Notes>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tvTitle;
        public final TextView tvText;

        // Handle user events in a RecyclerView
        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_note_title);
            tvText = (TextView) itemView.findViewById(R.id.tv_note_text);

            // Now the view reference will be available to the rest of the adapter
            view = itemView;
        }
    }
}
