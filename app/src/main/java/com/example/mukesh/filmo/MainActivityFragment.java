package com.example.mukesh.filmo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mukesh.filmo.data.Movie_Contract;
import com.example.mukesh.filmo.data.Storedata;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public GridView gridview;
    private Movie[] movieList;          //contains all movie trailer.
    private String Preference;
    private View rootView;
    private static final int REQUEST_WRITE_STORAGE = 112;
    public MainActivityFragment() {
    }

    public interface Callback {
        void onItemSelected(Movie movie);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    //Toast.makeText(parentActivity, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }

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
            ImageAdapter adapter = new ImageAdapter(getActivity(), movie_list,1);
            gridview.setAdapter(adapter);
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieList[position];
                ((Callback) getActivity()).onItemSelected(movie);
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Preference = sharedPref.getString(getString(R.string.pref_order), getString(R.string.pref_popularity));
        if(Is_Online()==true) {
            FetchMovieData updateMovies = new FetchMovieData(context,rootView);
            System.out.println("check out"+Preference);

            updateMovies.execute(Preference);
        }
        else
        {
            Cursor movieCursor;
            if(Preference.equals("favourite")) {
                movieCursor = getContext().getContentResolver().query(
                        Movie_Contract.Movie_Entry.CONTENT_URI,
                        null,
                        Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE + " = ?",
                        new String[]{"1"},
                        null);
            }
            else {
                movieCursor = getContext().getContentResolver().query(
                        Movie_Contract.Movie_Entry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            }
            System.out.println("aj ka vijaar"+movieCursor.getColumnCount());
            String[] movie_list = new String[movieCursor.getCount()];
            Movie movies;
            final Movie[] movieArray = new Movie[movieCursor.getCount()];

            int j=0;
            if (movieCursor.moveToFirst()) {
                // int locationIdIndex =
                do {
                    String id=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_ENTRY_ID));
                    String title=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_TITLE));
                    String popularity=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_POPULARITY));
                    String overview=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_DESCRIPTION));
                    String poster_path=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_POSTERPATH));
                    String release=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_RELEASEDATE));
                    String favourite=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE));

                    String backdrop_path=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_BACKDROPPATH));
                    String vote_average=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_VOTEAVERAGE));



                    movies = new Movie(id, title, popularity, overview, poster_path,
                            vote_average, release,backdrop_path,Integer.parseInt(favourite));
                    movieArray[j]=movies;
                    movie_list[j]=Environment.getExternalStorageDirectory().getAbsolutePath()+(movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_POSTERPATH)).replace("http://image.tmdb.org/t/p/w342",""));
                    j++;

                    System.out.println(favourite);

                      System.out.println("chkkk"+Environment.getExternalStorageDirectory().getAbsolutePath()+(movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_POSTERPATH)).replace("http://image.tmdb.org/t/p/w342","")));
                }while (movieCursor.moveToNext());
            }
            movieCursor.close();

            for(int i=0;i<movie_list.length;i++){
                System.out.println(movie_list[i]);
            }
            gridview = (GridView) rootView.findViewById(R.id.gridview);
            ImageAdapter adapter = new ImageAdapter(getActivity(), movie_list,0);
            gridview.setAdapter(adapter);


            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.offline_message),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 10);
                    toast.show();


                    Movie movie = movieArray[position];
                    Intent intent = new Intent(getActivity().getApplication(), Movie_detail.class);
                    Bundle b = new Bundle();
                    b.putParcelable("MOVIE", movie);
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });

        }
    }

    //Async class to fetch data from server.
    public class FetchMovieData extends AsyncTask<String, Void, Movie[]>{
        private Context mContext;
        private View rootView;
        public String preference;

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
                String favourite="0";
                //System.out.println("Mujhe dekhna ha"+actual_movie.getString("poster_path"));
               // store_image(actual_movie.getString("poster_path"),poster_path)




               // Log.d(LOG_TAG, " Complete. " + inserted + " Inserted");


                    Movie movies = new Movie(id, title, popularity, overview, poster_path,
                            vote_average, release, backdrop_path, 0);
                    movieArray[i] = movies;




               // Log.e(LOG_TAG,poster_path+" adfkj yha ke"+ backdrop_path);

                //create every movie poster to an object of Movie class.


            }
            return movieArray;
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
                ImageAdapter adapter = new ImageAdapter(mContext,  image_url,1);
                gridview.setAdapter(adapter);
                if(preference.equals("favourite")==false) {
                    Storedata storedata = new Storedata(mContext);
                    storedata.execute(movies);
                }
            }

        }

        @Override
        protected Movie[] doInBackground(String... params) {
            Movie[] movies=new Movie[0];
            preference=params[0];
            if(params[0].equals("favourite")){
                Cursor movieCursor = getContext().getContentResolver().query(
                        Movie_Contract.Movie_Entry.CONTENT_URI,
                        null,
                        Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE+ " = ?",
                        new String[]{"1"},
                        null);


             //   Movie[] moviearr = new Movie[movieCursor.getCount()];
                movies=new Movie[movieCursor.getCount()];
               int t=0;
                if(movieCursor==null){
                    System.out.println("jjjjjjjjjjjjjjj");
                }
                else
                    System.out.println(movieCursor.getCount()+"mmmmmmmmm");
                while(movieCursor.moveToNext()) {
                    String id=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_ENTRY_ID));
                    String title=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_TITLE));
                    String popularity=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_POPULARITY));
                    String overview=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_DESCRIPTION));
                    String poster_path=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_POSTERPATH));
                    String release=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_RELEASEDATE));
                    String favourite=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE));
                    String backdrop_path=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_BACKDROPPATH));
                    String vote_average=movieCursor.getString(movieCursor.getColumnIndex(Movie_Contract.Movie_Entry.COLUMN_NAME_VOTEAVERAGE));
                    System.out.println("aaa"+id+title+popularity+overview+poster_path+release+favourite);

                    Movie movies1 = new Movie(id, title, popularity, overview, poster_path,
                            vote_average, release, backdrop_path, 0);
                    movies[t]=movies1;
                    t++;
                }
               // System.out.println(movieCursor.getString(0)+"codechef");

                //movies=moviearr;
                movieCursor.close();
            }

            else {


                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movie_json = null;
                System.out.println(params[0] + "aaaaaaaaaaaaaaa");

                try {
                    Uri buildUri = Uri.parse(getString(R.string.movie_url) + "?").buildUpon()
                            .appendQueryParameter(getString(R.string.sort_by), params[0])
                            .appendQueryParameter(getString(R.string.api_key), getString(R.string.api_value))
                            .build();
                    Log.e(LOG_TAG, buildUri.toString());
                    URL url = new URL(buildUri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(getString(R.string.request_method));
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (inputStream == null)
                        movie_json = null;

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0)
                        movie_json = null;

                    movie_json = buffer.toString();
                   if(movies==null);
                    movies = get_movie_data(movie_json);
                    urlConnection.disconnect();
                    reader.close();
                    inputStream.close();

                    for (int i = 0; i < movies.length; i++) {


                        buildUri = Uri.parse("http://api.themoviedb.org/3/movie" + "/" + movies[i].getId() + "/videos?").buildUpon()
                                .appendQueryParameter(getString(R.string.api_key), getString(R.string.api_value))
                                .build();
                        Log.e(LOG_TAG, buildUri.toString());
                        url = new URL(buildUri.toString());
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod(getString(R.string.request_method));
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
                                .appendQueryParameter(getString(R.string.api_key), getString(R.string.api_value))
                                .build();
                        Log.e(LOG_TAG, buildUri.toString());
                        url = new URL(buildUri.toString());
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod(getString(R.string.request_method));
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


                } catch (Exception e) {
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
            }
            return movies;
        }
    }
}

