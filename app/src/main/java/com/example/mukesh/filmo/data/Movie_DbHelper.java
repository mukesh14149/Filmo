package com.example.mukesh.filmo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mukesh on 6/5/16.
 */
public class Movie_DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Filmo.db";

    public Movie_DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         final String SQL_CREATE_ENTRIES ="CREATE TABLE " + Movie_Contract.Movie_Entry.TABLE_NAME + " (" +
                Movie_Contract.Movie_Entry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                Movie_Contract.Movie_Entry.COLUMN_NAME_ENTRY_ID +  " TEXT NOT NULL, " +
                Movie_Contract.Movie_Entry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                Movie_Contract.Movie_Entry.COLUMN_NAME_POPULARITY +  " TEXT NOT NULL, " +
                Movie_Contract.Movie_Entry.COLUMN_NAME_DESCRIPTION +  " TEXT NOT NULL, " +
                Movie_Contract.Movie_Entry.COLUMN_NAME_POSTERPATH + " TEXT NOT NULL, " +
                Movie_Contract.Movie_Entry.COLUMN_NAME_RELEASEDATE +  " TEXT NOT NULL, " +
                 Movie_Contract.Movie_Entry.COLUMN_NAME_BACKDROPPATH +  " TEXT NOT NULL, " +
                 Movie_Contract.Movie_Entry.COLUMN_NAME_VOTEAVERAGE +  " TEXT NOT NULL, " +
                 Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE +  " TEXT NOT NULL " +
                " )";

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Movie_Contract.Movie_Entry.TABLE_NAME);
        onCreate(db);
    }
}
