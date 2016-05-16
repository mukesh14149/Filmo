package com.example.mukesh.filmo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by mukesh on 6/5/16.
 */
public class Movie_Provider extends ContentProvider{

    private Movie_DbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper=new Movie_DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        cursor=mOpenHelper.getReadableDatabase().query(Movie_Contract.Movie_Entry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return Movie_Contract.Movie_Entry.CONTENT_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id=db.insert(Movie_Contract.Movie_Entry.TABLE_NAME,null,values);
        Uri returnUri;
        if ( _id > 0 )
            returnUri = Movie_Contract.Movie_Entry.buildMovieUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        rowsDeleted = db.delete(
                Movie_Contract.Movie_Entry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        rowsUpdated = db.update(Movie_Contract.Movie_Entry.TABLE_NAME, values, selection,
                selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
