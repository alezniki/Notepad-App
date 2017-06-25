package com.alezniki.notepad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alezniki.notepad.R;
import com.alezniki.notepad.model.Notes;

import java.util.List;

/**
 * Created by nikola on 6/25/17.
 */

public class NotesAdapter extends ArrayAdapter<Notes> {
    // Using ArrayAdapter to improve ListView performances with ViewHolder pattern

    public NotesAdapter(Context context, List<Notes> list) {
        super(context, 0, list);
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvText;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        Notes pos = getItem(position);
        ViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_items,parent,false);

            // Look up for data population
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_note_title);
            viewHolder.tvText = (TextView) view.findViewById(R.id.tv_note_text);

            // Cache the viewHolder object inside the fresh view
            view.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) view.getTag();
        }

        // Populate the data from the data object via the viewHolder object into the template view.
        viewHolder.tvTitle.setText(pos.getNoteTitle());
        viewHolder.tvText.setText(pos.getNoteText());

        // Return the completed view to render on screen
        return view;
    }
}
