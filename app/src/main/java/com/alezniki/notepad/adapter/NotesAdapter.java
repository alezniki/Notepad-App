package com.alezniki.notepad.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alezniki.notepad.R;
import com.alezniki.notepad.activities.DetailActivity;
import com.alezniki.notepad.model.Notes;

import java.util.List;

/**
 * Created by nikola on 6/25/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    public static final String KEY_ID = "key_id";

    private List<Notes> listOfNotes; // list of items
    private Context context;

    public NotesAdapter(Context context, List<Notes> list) {
        this.context = context;
        this.listOfNotes = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    // This is where you supply data that you want to display to the user
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get element from your dataset at this position
        final Notes note = listOfNotes.get(position);

        // Replace the contents of the view with that element
        holder.tvTitle.setText(note.getNoteTitle());
        holder.tvText.setText(note.getNoteText());

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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listOfNotes.size();
    }

    public void clearFromAdapter() {
        int size = this.listOfNotes.size();

        if (size > 0) {
            for (int i = 0; i < size ; i++) {
                this.listOfNotes.remove(0);
            }
            this.notifyItemRangeChanged(0, size);
        }
    }

    public void addToAdapter(List<Notes> list) {
        this.listOfNotes.addAll(list);
        this.notifyItemRangeInserted(0, list.size()-1);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvText;

        // Handle user events in a RecyclerView
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_note_title);
            tvText = (TextView) itemView.findViewById(R.id.tv_note_text);

            // Now the view reference will be available to the rest of the adapter
            view = itemView;
        }
    }
}
