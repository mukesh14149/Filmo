package com.example.mukesh.filmo;

/**
 * Created by mukesh on 21/3/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Movie implements Parcelable {
    private String id;
    private String title;
    private String popularity;
    private String description;
    private String poster_path;
    private String release_date;
    private String vote_average;
    private String backdrop_path;
    private String videourl;
    private HashMap<String, String> review=new HashMap<String,String>();
    private int favourite=0;


    public Movie(String ref, String tit, String pop, String desc, String image, String vote, String release, String backdrop) {
        this.id = ref;
        this.title = tit;
        this.popularity = pop;
        this.description = desc;
        this.poster_path = image;
        this.release_date = release;
        this.vote_average = vote;
        this.backdrop_path = backdrop;
    }

    public Movie(String ref, String tit, String pop, String desc, String image, String vote, String release, String backdrop, int favourite) {
        this.id = ref;
        this.title = tit;
        this.popularity = pop;
        this.description = desc;
        this.poster_path = image;
        this.release_date = release;
        this.vote_average = vote;
        this.backdrop_path = backdrop;
        this.favourite=favourite;
    }

    public void addvideo(String video){
        this.videourl=video;
    }

    public void addreview(String name,String content){
        review.put(name,content);
    }

    public void setFavourite(int favourite){this.favourite=favourite;}

    private Movie(Parcel in){
        id = in.readString();
        title = in.readString();
        popularity = in.readString();
        description = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
        backdrop_path = in.readString();
        videourl=in.readString();
        review=(HashMap<String,String>) in.readSerializable();;
        favourite=in.readInt();
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }


    public String getRelease_date() {
        return release_date;
    }


    public String getVote_average() {
        return vote_average;
    }
    public String getPopularity(){return  popularity;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }


    public String getPoster_path() {
        return poster_path;
    }

    public String getVideourl() {
        return videourl;
    }

    public HashMap<String,String> getReview(){return review;}

    public int getFavourite(){return favourite;}
    @Override
    public int describeContents()
    {
        //For Parcelable.
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(popularity);
        out.writeString(description);
        out.writeString(poster_path);
        out.writeString(release_date);
        out.writeString(vote_average);
        out.writeString(backdrop_path);
        out.writeString(videourl);
        out.writeSerializable(review);
        out.writeInt(favourite);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {

        @Override
        public Movie createFromParcel(Parcel parcel)
        {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i)
        {
            return new Movie[i];
        }
    };
}
