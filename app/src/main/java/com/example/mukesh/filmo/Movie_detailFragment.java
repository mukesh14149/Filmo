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

        int backdropWidth = Util.getScreenWidth(getActivity());
        int backdropHeight = getResources().getDimensionPixelSize(R.dimen.details_backdrop_height);
        ImageView view_Backdrop = (ImageView) rootView.findViewById(R.id.backdrop_image);
        if(movie.getBackdrop_path().equals("empty"))
            Picasso.with(getActivity()).load(R.drawable.posternotfound).into(view_Backdrop);
        else
            Picasso.with(getActivity()).load(movie.getBackdrop_path()).resize(backdropWidth, backdropHeight).centerCrop().into(view_Backdrop);

        int posterWidth = getResources().getDimensionPixelSize(R.dimen.details_poster_width);
        int posterHeight = getResources().getDimensionPixelSize(R.dimen.details_poster_height);
        ImageView view_Poster = (ImageView) rootView.findViewById(R.id.poster_image);
        if(movie.getPoster_path().equals("empty"))
            Picasso.with(getActivity()).load(R.drawable.posternotfound).into(view_Backdrop);
        else
            Picasso.with(getActivity()).load(movie.getPoster_path()).resize(posterWidth ,posterHeight).centerCrop().into(view_Poster);


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

        return rootView;
    }
}
