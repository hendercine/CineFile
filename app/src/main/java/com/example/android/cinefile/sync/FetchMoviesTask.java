package com.example.android.cinefile.sync;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.cinefile.BuildConfig;
import com.example.android.cinefile.DetailFragment;
import com.example.android.cinefile.MoviesFragment;
import com.example.android.cinefile.R;
import com.example.android.cinefile.Utility;
import com.example.android.cinefile.data.Movie;
import com.example.android.cinefile.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hendercine on 10/8/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private static final String API_KEY = BuildConfig.THE_MOVIES_DB_API_KEY;
    private MoviesFragment moviesFragment;
    private DetailFragment detailFragment;
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private final String MOVIE_BASE_URL =
            "http://api.themoviedb.org/3/movie/";
    private final String TRAILER_BASE_URL =
            "//api.themoviedb.org/3/movie/";
    final String APPID_PARAM = "api_key";

    public FetchMoviesTask(MoviesFragment moviesFragment) {
        this.moviesFragment = moviesFragment;
    }

    public void setMovieAdapter(MovieAdapter movieAdapter) {
        this.mMovieAdapter = movieAdapter;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException, IOException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = moviesFragment.getString(R.string.json_results);
        final String TMDB_TITLE = moviesFragment.getString(R.string.json_title);
        final String TMDB_RELEASE = moviesFragment.getString(R.string.json_release_date);
        final String TMDB_VOTE_AVG = moviesFragment.getString(R.string.json_vote_avg);
        final String TMDB_POSTER = moviesFragment.getString(R.string.json_poster_path);
        final String TMDB_PLOT_SYNOPSIS = moviesFragment.getString(R.string.json_plot_synop);
        final String TMDB_BACKDROP = moviesFragment.getString(R.string.json_backdrop_path);
        final String TMDB_ID = moviesFragment.getString(R.string.api_movie_id);
        final String TMDB_TRAILER = moviesFragment.getString(R.string.trailer_path);

        String posterBaseUrl = moviesFragment.getString(R.string.poster_base_url);
        String backdropBaseUrl = moviesFragment.getString(R.string.backdrop_base_url);

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

        ArrayList<Movie> movieResults = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {
            String movieId;
            String title;
            String moviePlot;
            String releaseDate;
            String voteAverage;
            String posterPath;
            String backDrop;
            String trailerPath;

            // Get the JSON object representing the movie
            JSONObject currentMovie = movieArray.getJSONObject(i);
            movieId = currentMovie.getString(TMDB_ID);
            title = currentMovie.getString(TMDB_TITLE);
            moviePlot = currentMovie.getString(TMDB_PLOT_SYNOPSIS);
            releaseDate = currentMovie.getString(TMDB_RELEASE);
            voteAverage = currentMovie.getString(TMDB_VOTE_AVG);
            posterPath = posterBaseUrl + currentMovie.getString(TMDB_POSTER);
            backDrop = backdropBaseUrl + currentMovie.getString(TMDB_BACKDROP);
            trailerPath = getTrailerDataFromJson(movieId).toString();

            Movie movie =
                    new Movie(movieId, title, posterPath, releaseDate, voteAverage, moviePlot, backDrop, trailerPath);
            movieResults.add(movie);
        }

        return movieResults;

    }

    private ArrayList<Trailer> getTrailerDataFromJson(String movieId)
            throws JSONException, IOException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_TRAILER = moviesFragment.getString(R.string.trailer_path);
        final String TMDB_TRAILER_KEY = moviesFragment.getString(R.string.trailer_key);
        final String TMDB_TRAILER_NAME = moviesFragment.getString(R.string.trailer_name);


        String trailerBaseUrl = MOVIE_BASE_URL + movieId + TMDB_TRAILER + APPID_PARAM + "=" + API_KEY;
        String trailerJson = new Utility().requestConnection(trailerBaseUrl);

        JSONObject trailersObj = new JSONObject(trailerJson);
        JSONArray trailersArray = trailersObj.getJSONArray("results");

        ArrayList<Trailer> trailerResults = new ArrayList<>();

        for (int i = 0; i < trailersArray.length(); i++) {
            String trailerKey;
            String trailerName;

            // Get the JSON object representing the trailer
            JSONObject currentTrailer = trailersArray.getJSONObject(i);
            trailerKey = currentTrailer.getString(TMDB_TRAILER_KEY);
            trailerName = currentTrailer.getString(TMDB_TRAILER_NAME);

            Trailer trailer =
                    new Trailer(trailerKey, trailerName);
            trailerResults.add(trailer);
        }

        return trailerResults;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        // Will contain the raw JSON response as a string.
        String moviesJsonStr;

        //Sort order passed in from Settings Activity
        String sortOrder;
        sortOrder = params[0];

        // Construct the URL for the TheMovieDB query
        final String SORT_PARAM = sortOrder;
        final String LANG_PARAM = "language";

        Uri builtMovieUri = Uri.parse(MOVIE_BASE_URL + SORT_PARAM).buildUpon()
                .appendQueryParameter(LANG_PARAM, "en-US")
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build();

        URL movieUrl = null;
        try {
            movieUrl = new URL(builtMovieUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert movieUrl != null;
        moviesJsonStr = movieUrl.toString();
        //TODO: Remove verbose log.
        Log.v(LOG_TAG, "Built Movies URL: " + movieUrl);

        try {
            String movieBufferStr = new Utility().requestConnection(moviesJsonStr);
            return getMovieDataFromJson(movieBufferStr);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> result) {
        if (result != null) {
            mMovieAdapter.clear();

            for (Movie item : result) {
                mMovieAdapter.add(item);
            }
        }
    }
}
