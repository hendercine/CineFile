package com.example.android.cinefile;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.cinefile.data.Movie;
import com.example.android.cinefile.sync.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hendercine on 10/8/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private MoviesFragment moviesFragment;
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private MovieAdapter mMovieAdapter;

    public FetchMoviesTask(MoviesFragment moviesFragment) {
        this.moviesFragment = moviesFragment;
    }

    public void setmMovieAdapter(MovieAdapter movieAdapter) {
        this.mMovieAdapter = movieAdapter;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

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
            String title;
            String moviePlot;
            String releaseDate;
            String voteAverage;
            String posterPath;
            String backDrop;
            String trailerPath;

            // Get the JSON object representing the movie
            JSONObject currentMovie = movieArray.getJSONObject(i);
            title = currentMovie.getString(TMDB_TITLE);
            moviePlot = currentMovie.getString(TMDB_PLOT_SYNOPSIS);
            releaseDate = currentMovie.getString(TMDB_RELEASE);
            voteAverage = currentMovie.getString(TMDB_VOTE_AVG);
            posterPath = posterBaseUrl + currentMovie.getString(TMDB_POSTER);
            backDrop = backdropBaseUrl + currentMovie.getString(TMDB_BACKDROP);
            trailerPath = currentMovie.getString(TMDB_ID) + "/" + currentMovie.getString(TMDB_TRAILER);

            Movie movie =
                    new Movie(title, posterPath, releaseDate, voteAverage, moviePlot, backDrop, trailerPath);
            movieResults.add(movie);
        }

        return movieResults;

    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        //Sort order passed in from Settings Activity
        String sortOrder;
        sortOrder = params[0];

        try {
            // Construct the URL for the TheMovieDB query
            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String SORT_PARAM = sortOrder;
            final String APPID_PARAM = "api_key";
            final String LANG_PARAM = "language";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL + SORT_PARAM).buildUpon()
                    .appendQueryParameter(LANG_PARAM, "en-US")
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIES_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URL: " + url);
            // Create the request to TheMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in
            // attempting to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
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
