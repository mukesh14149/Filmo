package com.example.mukesh.filmo;

/**
 * Created by mukesh on 21/3/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String id;
    private String title;
    private String popularity;
    private String description;
    private String poster_path;
    private String release_date;
    private String vote_average;
    private String backdrop_path;

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

    private Movie(Parcel in){
        id = in.readString();
        title = in.readString();
        popularity = in.readString();
        description = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
        backdrop_path = in.readString();
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
