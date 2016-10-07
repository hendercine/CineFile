package com.example.android.cinefile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hendercine on 10/6/16.
 */

public class Movie implements Parcelable {

    private String mMovieTitle;
    private String mMoviePoster;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mMoviePlot;

    public Movie(String movieTitle, String moviePoster, String releaseDate, String voteAverage, String moviePlot) {
        mMovieTitle = movieTitle;
        mMoviePoster = moviePoster;
        mReleaseDate = releaseDate;
        mVoteAverage = voteAverage;
        mMoviePlot = moviePlot;
    }

    private Movie(Parcel in) {
        mMovieTitle = in.readString();
        mMoviePoster = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = in.readString();
        mMoviePlot = in.readString();
    }

    public String getmMovieTitle() { return mMovieTitle; }

    public String getmMoviePoster() { return mMoviePoster; }

    public String getmReleaseDate() { return mReleaseDate; }

    public String getmVoteAverage() { return mVoteAverage; }

    public String getmMoviePlot() { return mMoviePlot; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mMovieTitle);
        out.writeString(mMoviePoster);
        out.writeString(mReleaseDate);
        out.writeString(mVoteAverage);
        out.writeString(mMoviePlot);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) { return new Movie(in); }

        public Movie[] newArray(int size) { return new Movie[size]; }
    };
}
