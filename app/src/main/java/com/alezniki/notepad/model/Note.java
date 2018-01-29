package com.alezniki.notepad.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Note entity
 * <p>
 * Created by nikola aleksic on 6/25/17.
 */
@SuppressWarnings("ALL")
@DatabaseTable(tableName = Note.TABLE_NAME_NOTES)
public class Note {

    /**
     * Note table name
     */
    public static final String TABLE_NAME_NOTES = "notes";

    /**
     * Table id field
     */
    private static final String FIELD_NAME_ID = "id";

    /**
     * Table title field
     */
    private static final String FIELD_NAME_TITLE = "title";

    /**
     * Table text field
     */
    private static final String FIELD_NAME_TEXT = "text";

    /**
     * Note id
     */
    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int noteID;

    /**
     * Note title
     */
    @DatabaseField(columnName = FIELD_NAME_TITLE)
    private String noteTitle;

    /**
     * Note text
     */
    @DatabaseField(columnName = FIELD_NAME_TEXT)
    private String noteText;

    /**
     * Empty constructore
     * <p>
     * Required for ORM Lite
     */
    public Note() {

    }

    /**
     * Get note id
     *
     * @return id
     */
    public int getNoteID() {
        return noteID;
    }

    /**
     * Get note title
     *
     * @return title
     */
    public String getNoteTitle() {
        return noteTitle;
    }

    /**
     * Set note title
     *
     * @param noteTitle note title
     */
    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    /**
     * Get note text
     *
     * @return text
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * Set note text
     *
     * @param noteText note text
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
