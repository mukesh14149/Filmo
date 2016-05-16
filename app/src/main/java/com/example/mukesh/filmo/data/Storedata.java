package com.example.mukesh.filmo.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mukesh.filmo.MainActivityFragment;
import com.example.mukesh.filmo.Movie;
import com.example.mukesh.filmo.R;

import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by mukesh on 15/5/16.
 */
public class Storedata extends AsyncTask<Movie, Void, Void> {
    Context mContext;

    public Storedata(Context context){
        mContext=context;
    }

    @Override
    protected Void doInBackground(Movie... params) {


        for (int i = 0; i < params.length; i++) {

            //JSONObject actual_movie = results_array.getJSONObject(i);
            Movie actual_movie=params[i];
            String id = actual_movie.getId();
            String title = actual_movie.getTitle();
            String release = actual_movie.getRelease_date();
            String popularity = actual_movie.getPopularity();
            String overview = actual_movie.getDescription();
            String vote_average = actual_movie.getVote_average();
            String poster_path = "http://image.tmdb.org/t/p/w342"+actual_movie.getPoster_path();
            String backdrop_path = "http://image.tmdb.org/t/p/original"+actual_movie.getBackdrop_path();
            String favourite="0";
            //System.out.println("Mujhe dekhna ha"+actual_movie.getString("poster_path"));
            // store_image(actual_movie.getString("poster_path"),poster_path)



            Vector<ContentValues> cVVector = new Vector<ContentValues>(10);
            ContentValues movie_value = new ContentValues();
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_ENTRY_ID,id);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_TITLE,title);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_RELEASEDATE,release);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_DESCRIPTION,overview);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_POPULARITY,popularity);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_POSTERPATH,poster_path);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE,favourite);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_BACKDROPPATH,backdrop_path);
            movie_value.put(Movie_Contract.Movie_Entry.COLUMN_NAME_VOTEAVERAGE,vote_average);

            cVVector.add(movie_value);


            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(Movie_Contract.Movie_Entry.CONTENT_URI, cvArray);
            }
            System.out.println("yo ho gya"+inserted);









        }










        return null;
    }
}
