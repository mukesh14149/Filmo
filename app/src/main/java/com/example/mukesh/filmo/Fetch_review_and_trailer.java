package com.example.mukesh.filmo;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mukesh on 17/5/16.
 */
public class Fetch_review_and_trailer extends AsyncTask<Movie, Void, Void> {

    Context mContext;
    String api_key;
    String api_value;
    String request_method;
    public Fetch_review_and_trailer(Context context, String api_key, String api_value, String request_method){
        mContext=context;
        this.api_key=api_key;
        this.api_value=api_value;
        this.request_method=request_method;
    }

    public void fetchtrailer(String trailer_json, Movie movie) throws JSONException {
        JSONObject movie_data = new JSONObject(trailer_json);
        JSONArray results_array = movie_data.getJSONArray("results");
        for (int i = 0; i < results_array.length(); i++) {
            JSONObject result_object = results_array.getJSONObject(i);
            movie.addvideo(result_object.getString("key")+" ");
        }
    }

    public void fetchreviews(String revies_json,Movie movie) throws JSONException{
        JSONObject movie_data = new JSONObject(revies_json);
        JSONArray results_array = movie_data.getJSONArray("results");
        for (int i = 0; i < results_array.length(); i++) {
            JSONObject result_object = results_array.getJSONObject(i);
            System.out.println(result_object.getString("author")+"I am serious"+result_object.getString("content"));
            movie.addreview(result_object.getString("author"),result_object.getString("content"));
        }
    }
    @Override
    protected Void doInBackground(Movie... movies) {
        Uri buildUri;
        URL url;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movie_json = null;
        InputStream inputStream ;
        String line;
        StringBuffer buffer = new StringBuffer();
        try {
            for (int i = 0; i < movies.length; i++) {
                buildUri = Uri.parse("http://api.themoviedb.org/3/movie" + "/" + movies[i].getId() + "/videos?").buildUpon()
                        .appendQueryParameter(api_key, api_value)
                        .build();
                url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(request_method);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();

                if (inputStream == null)
                    movie_json = null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0)
                    movie_json = null;

                movie_json = buffer.toString();
                fetchtrailer(movie_json, movies[i]);
                urlConnection.disconnect();
                reader.close();
                inputStream.close();
            }

            for (int i = 0; i < movies.length; i++) {
                buildUri = Uri.parse("http://api.themoviedb.org/3/movie" + "/" + movies[i].getId() + "/reviews?").buildUpon()
                        .appendQueryParameter(api_key, api_value)
                        .build();
                url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(request_method);
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();

                if (inputStream == null)
                    movie_json = null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0)
                    movie_json = null;

                movie_json = buffer.toString();
                fetchreviews(movie_json, movies[i]);

            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
