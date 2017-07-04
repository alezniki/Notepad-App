package com.alezniki.notepad.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by nikola on 6/25/17.
 */

@SuppressWarnings("ALL")
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Notes,Integer> notesDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Notes.class);
        } catch (SQLException e) {
            e.printStackTrace();
            //Log.e(DatabaseHelper.class.getName(),"UNABLE TO CREATE DATABASE",e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            try {
                TableUtils.dropTable(connectionSource, Notes.class, true);
            } catch (SQLException e) {
                e.printStackTrace();
              //  Log.e(DatabaseHelper.class.getName(),
              //          "UNABLE TO UPGRADE DATABASE FROM VERSION " + oldVersion + " TO NEW " + newVersion, e);
            }
        }
    }

    // Insert, delete, read, update everything will be happened through DAOs
    public Dao<Notes, Integer> getNotesDao() throws SQLException {
        if (notesDao == null) {
            notesDao = getDao(Notes.class);
        }

        return notesDao;
    }

    // Clear resources and close database
    @Override
    public void close() {
        super.close();

        notesDao = null;
    }
}
