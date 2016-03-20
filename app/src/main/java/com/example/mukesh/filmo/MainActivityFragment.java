package com.example.mukesh.filmo;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public static  GridView gridview;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String LOG_TAG = FetchMovieData.class.getSimpleName();

        FetchMovieData fetchMovieData;

        View rootView=inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        new FetchMovieData(getActivity(),rootView).execute();

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }


}

class FetchMovieData extends AsyncTask<Void, Void, Movie[]>{
    private Context mContext;
    private View rootView;
    private Movie[] movieList;
    private final String LOG_TAG = FetchMovieData.class.getSimpleName();
    public  FetchMovieData(Context context, View rootView){
        this.mContext=context;
        this.rootView=rootView;
    }

    public Movie[]  get_movie_data(String movie_json) throws JSONException{

        JSONObject movie_data = new JSONObject(movie_json);
        JSONArray results_array = movie_data.getJSONArray("results");
        Movie[] movieArray = new Movie[results_array.length()];

        for (int i = 0; i < results_array.length(); i++) {

            JSONObject actual_movie = results_array.getJSONObject(i);

            String id = actual_movie.getString("id");
            String title = actual_movie.getString("title");
            String poster_path = "http://image.tmdb.org/t/p/w342"+actual_movie.getString("poster_path");
            String backdrop_path = "http://image.tmdb.org/t/p/original"+actual_movie.getString("backdrop_path");
            String release = actual_movie.getString("release_date");

            String popularity = actual_movie.getString("popularity");

            String overview = actual_movie.getString("overview");

            String vote_average = actual_movie.getString("vote_average");
            Log.e(LOG_TAG,id+" "+title+" "+poster_path+" "+backdrop_path+" "+popularity+" "+release+overview+vote_average+" "+"dekhlo");
            Movie movies = new Movie(id, title, popularity, overview, poster_path,
                    vote_average, release,backdrop_path);
            movieArray[i] = movies;

        }
        return movieArray;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        String[] image_url = new String[movies.length];
        if (movies != null) {
            for (int i = 0; i < movies.length; i++) {
                image_url[i] = movies[i].getPoster_path();
            }
            GridView  gridview = (GridView) rootView.findViewById(R.id.gridview);
            ImageAdapter adapter = new ImageAdapter(mContext,  image_url);
            gridview.setAdapter(adapter);
        }



    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        ArrayList<String> result=null;
        Movie[] movies=new Movie[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movie_json = null;


        try {
            Uri buildUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                    .appendQueryParameter("sort_by","pref_order")
                    .appendQueryParameter("api_key", "Your_api_key")
                    .build();
            Log.e(LOG_TAG,buildUri.toString());
            URL url = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
            {
                movie_json= null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                movie_json = null;
            }
            movie_json = buffer.toString();
            movies=get_movie_data(movie_json);


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {

                }
            }
        }
        return movies;

    }
}