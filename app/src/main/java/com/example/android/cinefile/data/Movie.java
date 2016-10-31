package com.example.android.cinefile.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hendercine on 10/6/16.
 */

public class Movie implements Parcelable {

    public String mMovieTitle;
    public String mMoviePoster;
    public String mReleaseDate;
    public String mVoteAverage;
    public String mMoviePlot;
    public String mMovieBackDrop;
    public String mTrailerPath;
//    public ArrayList<Trailer> mTrailerArrayList = new ArrayList<>();

    public Movie(String movieTitle, String moviePoster, String releaseDate, String voteAverage, String moviePlot, String backDrop, String trailerPath) {
        this.mMovieTitle = movieTitle;
        this.mMoviePoster = moviePoster;
        this.mReleaseDate = releaseDate;
        this.mVoteAverage = voteAverage;
        this.mMoviePlot = moviePlot;
        this.mMovieBackDrop = backDrop;
        this.mTrailerPath = trailerPath;
    }

//    public Movie(ArrayList<Trailer> trailerArrayList) {
//        this.mTrailerArrayList = trailerArrayList;
//    }

    private Movie(Parcel in) {
        mMovieTitle = in.readString();
        mMoviePoster = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = in.readString();
        mMoviePlot = in.readString();
        mMovieBackDrop = in.readString();
        mTrailerPath = in.readString();
//        in.readTypedList(mTrailerArrayList, Trailer.CREATOR);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mMovieTitle);
        out.writeString(mMoviePoster);
        out.writeString(mReleaseDate);
        out.writeString(mVoteAverage);
        out.writeString(mMoviePlot);
        out.writeString(mMovieBackDrop);
        out.writeString(mTrailerPath);
//        out.writeTypedList(mTrailerArrayList);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) { return new Movie(in); }

        public Movie[] newArray(int size) { return new Movie[size]; }
    };

//    public ArrayList<Trailer> getMovieTrailer() {
//        return mTrailerArrayList;
//    }
}
