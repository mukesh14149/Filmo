package com.example.mukesh.filmo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Bundle b = getActivity().getIntent().getExtras();
        Movie movie = b.getParcelable("MOVIE");
        ImageView view_Poster = (ImageView) rootView.findViewById(R.id.mPoster);
        Picasso.with(getActivity()).load(movie.getPoster_path()).resize(10, 10).centerCrop().into(view_Poster);
        ((TextView) rootView.findViewById(R.id.mSynopsis))
                .setText(movie.getDescription());

        ((TextView) rootView.findViewById(R.id.mTitle))
                .setText(movie.getTitle());

        ((TextView) rootView.findViewById(R.id.mRating))
                .setText(movie.getVote_average());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ConvertToDate(movie.getRelease_date()));

        ((TextView) rootView.findViewById(R.id.mRelease))
                .setText(String.valueOf(calendar.get(Calendar.YEAR)));;
        return rootView;
    }
}
