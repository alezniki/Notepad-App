package com.alezniki.notepad.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by nikola on 6/25/17.
 */

@DatabaseTable(tableName = Notes.TABLE_NAME_NOTES)
public class Notes {

    // Table Name
    public static final String TABLE_NAME_NOTES = "notes";

    // Column Names
    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_TITLE = "title";
    public static final String FIELD_NAME_TEXT = "text";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int noteID;
    @DatabaseField(columnName = FIELD_NAME_TITLE)
    private String noteTitle;
    @DatabaseField(columnName = FIELD_NAME_TEXT)
    private String noteText;

    public Notes(){
        // Empty Constructor
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }


}
