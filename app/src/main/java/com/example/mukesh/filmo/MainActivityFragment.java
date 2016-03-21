package com.example.mukesh.filmo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public GridView gridview;
    private Movie[] movieList;          //contains all movie trailer.
    private String Preference;
    private View rootView;
    public MainActivityFragment() {
    }

    public boolean Is_Online(){
        ConnectivityManager connectivity = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String LOG_TAG = FetchMovieData.class.getSimpleName();
        PreferenceManager.setDefaultValues(getActivity().getApplication(), R.xml.pref_general, true);
        FetchMovieData fetchMovieData;

        rootView=inflater.inflate(R.layout.fragment_main, container, false);

        //check whether Movies key is present in sharedpref.
        if (savedInstanceState == null || !savedInstanceState.containsKey("Movies")) {
            updateMovie(getActivity(),rootView);
            gridview = (GridView) rootView.findViewById(R.id.gridview);
        }

        //if sharedpref already contain a key Movies
        else {
            movieList = (Movie[]) savedInstanceState.getParcelableArray("Movies");
            String[] movie_list = new String[movieList.length];     //Image path of movie poster.
            for (int i = 0; i < movieList.length; i++) {
                movie_list[i] = movieList[i].getPoster_path();
            }

            gridview = (GridView) rootView.findViewById(R.id.gridview);
            ImageAdapter adapter = new ImageAdapter(getActivity(), movie_list);
            gridview.setAdapter(adapter);
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie movie = movieList[position];
                Intent intent = new Intent(getActivity().getApplication(), Movie_detail.class);
                Bundle b = new Bundle();
                b.putParcelable("MOVIE", movie);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie(getActivity(),rootView);    //whenever activity start it will update the content.
    }

    @Override
    public void onSaveInstanceState(Bundle saving_State) {
        saving_State.putParcelableArray("Movies", movieList);
        saving_State.putString("Preference", Preference);
        super.onSaveInstanceState(saving_State);
    }
    private void updateMovie(Context context, View rootView){
        //If internet is available
        if(Is_Online()==true) {
            FetchMovieData updateMovies = new FetchMovieData(context,rootView);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Preference = sharedPref.getString(getString(R.string.pref_order), getString(R.string.pref_popularity));
            updateMovies.execute(Preference);
        }
        else
        {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.offline_message),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 10);
            toast.show();
        }
    }

    //Async class to fetch data from server.
    public class FetchMovieData extends AsyncTask<String, Void, Movie[]>{
        private Context mContext;
        private View rootView;

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();
        public  FetchMovieData(Context context, View rootView){
            this.mContext=context;
            this.rootView=rootView;
        }

        public Movie[]  get_movie_data(String movie_json) throws JSONException{
            //Fetch data in Json format and then fetch actual data.
            JSONObject movie_data = new JSONObject(movie_json);
            JSONArray results_array = movie_data.getJSONArray("results");
            Movie[] movieArray = new Movie[results_array.length()];

            for (int i = 0; i < results_array.length(); i++) {

                JSONObject actual_movie = results_array.getJSONObject(i);
                String id = actual_movie.getString("id");
                String title = actual_movie.getString("title");
                String release = actual_movie.getString("release_date");
                String popularity = actual_movie.getString("popularity");
                String overview = actual_movie.getString("overview");
                String vote_average = actual_movie.getString("vote_average");
                String poster_path = getString(R.string.poster_path)+actual_movie.getString("poster_path");
                String backdrop_path = getString(R.string.backdrop_path)+actual_movie.getString("backdrop_path");

                Log.e(LOG_TAG,poster_path+" adfkj yha ke"+ backdrop_path);

                //create every movie poster to an object of Movie class.
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
            movieList=movies;
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
        protected Movie[] doInBackground(String... params) {
            Movie[] movies=new Movie[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movie_json = null;


            try {
                Uri buildUri = Uri.parse(getString(R.string.movie_url)).buildUpon()
                        .appendQueryParameter(getString(R.string.sort_by), params[0])
                        .appendQueryParameter(getString(R.string.api_key), getString(R.string.api_value))
                        .build();
                Log.e(LOG_TAG,buildUri.toString());
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(getString(R.string.request_method));
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null)
                    movie_json= null;

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0)
                    movie_json = null;

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
                        e.printStackTrace();
                    }
                }
            }
            return movies;
        }
    }
}

