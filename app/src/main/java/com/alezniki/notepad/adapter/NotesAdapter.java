package com.alezniki.notepad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alezniki.notepad.R;
import com.alezniki.notepad.model.Notes;

import java.util.List;

/**
 * Created by nikola on 6/25/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<Notes> list; // list of items
    private Context context;

    public NotesAdapter(Context context, List<Notes> list) {
        this.context = context;
        this.list = list;
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
        final Notes note = list.get(position);

        // Replace the contents of the view with that element
        holder.tvTitle.setText(note.getNoteTitle());
        holder.tvText.setText(note.getNoteText());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clearFromAdapter() {
        int size = this.list.size();
//        this.list.clear();
//        notifyIte

        if (size > 0) {
            for (int i = 0; i < size ; i++) {
                this.list.remove(0);
            }
            this.notifyItemRangeChanged(0, size);
        }
    }

    public void addToAdapter(List<Notes> l) {
        this.list.addAll(l);
        this.notifyItemRangeInserted(0, l.size()-1);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvText;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_note_title);
            tvText = (TextView) itemView.findViewById(R.id.tv_note_text);
        }
    }
}
