package com.example.mukesh.filmo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mukesh.filmo.data.Movie_Contract;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class Movie_detailFragment extends Fragment {

    public Movie_detailFragment() {
    }

    private Date ConvertToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
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
        View rootView =inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Bundle b = getArguments();
        Movie movie=null;
        if(b!=null)
           movie = b.getParcelable("MOVIE");

        if (movie != null) {
            rootView.findViewById(R.id.detailfragment).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.detailfragment).setVisibility(View.INVISIBLE);
        }

        if(movie!=null){
            if(movie.getVideourl()==null&& Is_Online()){
                Fetch_review_and_trailer review_and_trailer=new Fetch_review_and_trailer(getContext(),getString(R.string.api_key),getString(R.string.api_value),getString(R.string.request_method));
                review_and_trailer.execute(movie);
            }

            int backdropWidth = Util.getScreenWidth(getActivity());
            int backdropHeight = getResources().getDimensionPixelSize(R.dimen.details_backdrop_height);

            ImageView view_Backdrop = (ImageView) rootView.findViewById(R.id.backdrop_image);

            try {
                if (movie.getBackdrop_path().equals("empty"))
                    Picasso.with(getActivity()).load(R.drawable.posternotfound).into(view_Backdrop);
                else
                    Picasso.with(getActivity()).load(movie.getBackdrop_path()).resize(backdropWidth, backdropHeight).centerCrop().into(view_Backdrop);
            }catch (Exception e){

            }

            int posterWidth = getResources().getDimensionPixelSize(R.dimen.details_poster_width);
            int posterHeight = getResources().getDimensionPixelSize(R.dimen.details_poster_height);
            ImageView view_Poster = (ImageView) rootView.findViewById(R.id.poster_image);
            if(movie.getPoster_path().equals("empty")) {
                Picasso.with(getActivity()).load(R.drawable.posternotfound).into(view_Backdrop);
            }
            else {
                Picasso.with(getActivity()).load(movie.getPoster_path()).resize(posterWidth, posterHeight).centerCrop().into(view_Poster);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ConvertToDate(movie.getRelease_date()));
            ((TextView) rootView.findViewById(R.id.realease_date))
                    .setText(String.valueOf(calendar.get(Calendar.YEAR)));;

            ((TextView) rootView.findViewById(R.id.description))
                    .setText(movie.getDescription());

            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(movie.getTitle());

            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(movie.getVote_average());

            int total=0;
            List<String> review_key = new ArrayList<String>();
            List<String> review_value = new ArrayList<String>();
            for(Map.Entry key:movie.getReview().entrySet()){
                review_key.add(key.getKey().toString());
                review_value.add(key.getValue().toString());
                if(total==2)
                    break;
                total++;
            }

            ArrayAdapter<String> reviews1,reviews2;
            reviews1 =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.reviews_list, // The name of the layout ID.
                            R.id.reviews, // The ID of the textview to populate.
                            review_value);

            ListView listView = (ListView) rootView.findViewById(R.id.reviews);
            listView.setAdapter(reviews1);

            final ContentValues values = new ContentValues();
            values.put(Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE, "1");
            final String[] selectionArgs = { String.valueOf(movie.getId()) };

            String favourite="0";
            Cursor movieCursor = getContext().getContentResolver().query(
                    Movie_Contract.Movie_Entry.CONTENT_URI,
                    new String[]{Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE},
                    Movie_Contract.Movie_Entry.COLUMN_NAME_ENTRY_ID+ " = ?",
                    new String[]{movie.getId()},
                    null);

            if(movieCursor!=null) {
                while(movieCursor.moveToNext()) {
                    movie.setFavourite(Integer.parseInt(movieCursor.getString(0)));
                    favourite=movieCursor.getString(0);
                    break;
                }
            }

            final Button fav_button=((Button) rootView.findViewById(R.id.fav));;
            final Movie movief=movie;
            if(favourite.equals("1"))
                fav_button.setText("FAVOURITE");

            if(favourite.equals("0"))
                fav_button.setText("MARK FAVOURITE");

            fav_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    int key=movief.getFavourite();
                    if(key==0){
                        fav_button.setText("Favourite");
                        movief.setFavourite(1);
                        values.put(Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE, "1");
                        getContext().getContentResolver().update(
                                Movie_Contract.Movie_Entry.CONTENT_URI,values,Movie_Contract.Movie_Entry.COLUMN_NAME_ENTRY_ID+" LIKE ?",selectionArgs);
                    }

                    if(key==1){
                        fav_button.setText("Mark Favourite");
                        movief.setFavourite(0);
                        values.put(Movie_Contract.Movie_Entry.COLUMN_NAME_FAVOURITE, "0");
                        getContext().getContentResolver().update(
                                Movie_Contract.Movie_Entry.CONTENT_URI,values,Movie_Contract.Movie_Entry.COLUMN_NAME_ENTRY_ID+" LIKE ?",selectionArgs);
                    }
                }
            });

            HashMap<String,String> hashMap=movie.getReview();
            for (String name: hashMap.keySet()){
                String key =name.toString();
                String value = hashMap.get(name).toString();
            }

            final String videourl[];
            try {
                videourl=movie.getVideourl().split(" ");
                Button button=(Button) rootView.findViewById(R.id.youtube);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), FragmentDemoActivity.class);
                        intent.putExtra(Intent.EXTRA_TEXT,videourl[0]);
                        startActivity(intent);
                    }
                });
            }catch (Exception e){
                Button button=(Button) rootView.findViewById(R.id.youtube);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Trailer_message),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 10);
                        toast.show();
                    }
                });
            }
            movieCursor.close();
        }

        else{
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Tap on any Movie",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 10);
            toast.show();
        }
        return rootView;
    }
}
