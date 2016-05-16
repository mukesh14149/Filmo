package com.example.mukesh.filmo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mukesh on 6/5/16.
 */
public final class Movie_Contract {

    public static final String CONTENT_AUTHORITY = "com.example.mukesh.filmo.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "Movie";

    public Movie_Contract() {}

    public static abstract class Movie_Entry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;



        public static String TABLE_NAME="Movie";
        public static String COLUMN_NAME_ENTRY_ID="id";
        public static String COLUMN_NAME_TITLE="title";
        public static String COLUMN_NAME_POPULARITY="popularity";
        public static String COLUMN_NAME_DESCRIPTION="description";
        public static String COLUMN_NAME_POSTERPATH="posterpath";
        public static String COLUMN_NAME_BACKDROPPATH="backdroppath";
        public static String COLUMN_NAME_VOTEAVERAGE="voteaverage";
        public static String COLUMN_NAME_RELEASEDATE="releasedate";
        public static String COLUMN_NAME_FAVOURITE="favourite";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

}
