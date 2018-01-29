package com.alezniki.notepad.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Database helper
 * <p>
 * Created by nikola aleksic on 6/25/17.
 */
@SuppressWarnings("ALL")
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /**
     * Database name
     */
    private static final String DATABASE_NAME = "notes.db";

    /**
     * Database version
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Note Dao
     */
    private Dao<Note, Integer> notes = null;

    /**
     * Constructor
     *
     * @param context context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, Note.class);
        } catch (SQLException e) {
            System.out.println("######## UNABLE TO CREATE DATABASE " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            try {
                TableUtils.dropTable(connectionSource, Note.class, true);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("######## UNABLE TO UPGRADE DATABASE " + oldVersion + " " + newVersion + " " + e);
            }
        }
    }

    /**
     * Get notes
     *
     * @return notes
     * @throws SQLException exception
     */
    public Dao<Note, Integer> getNotes() throws SQLException {

        //Insert, delete, read, update everything will be happened through DAOs
        if (notes == null) {
            notes = getDao(Note.class);
        }

        return notes;
    }

    @Override
    public void close() {
        super.close();

        //Clear resources and close database
        notes = null;
    }
}
