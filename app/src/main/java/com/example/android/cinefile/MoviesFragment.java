package com.example.android.cinefile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Encapsulates fetching the movies and displaying it as a GridView layout.
 */
public class MoviesFragment extends Fragment {

    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    //Keys for Intent to detail activity
    public static final String MOVIE_TITLE = "MOVIE_TITLE";
    public static final String MOVIE_POSTER = "MOVIE_POSTER";
    public static final String MOVIE_BACKDROP = "MOVIE_BACKDROP";
    public static final String MOVIE_RELEASE = "MOVIE_RELEASE";
    public static final String MOVIE_VOTE_AVG = "MOVIE_VOTE_AVG";
    public static final String MOVIE_PLOT_SUMMARY = "MOVIE_PLOT_SUMMARY";

    //ArrayList of movies
    public ArrayList<Movie> movies = new ArrayList<>();

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView= (GridView) rootView.findViewById(R.id.grid_view_posters);
        mMovieAdapter = new MovieAdapter(getActivity(), movies);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class).
                        putExtra(MOVIE_TITLE, movies.get(position).mMovieTitle).
                        putExtra(MOVIE_POSTER, movies.get(position).mMoviePoster).
                        putExtra(MOVIE_BACKDROP, movies.get(position).mMovieBackDrop).
                        putExtra(MOVIE_RELEASE, movies.get(position).mReleaseDate).
                        putExtra(MOVIE_VOTE_AVG, movies.get(position).mVoteAverage).
                        putExtra(MOVIE_PLOT_SUMMARY, movies.get(position).mMoviePlot);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.setmMovieAdapter(mMovieAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        moviesTask.execute(sortOrder);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private MovieAdapter mMovieAdapter;

        public void setmMovieAdapter(MovieAdapter movieAdapter) {
            this.mMovieAdapter = movieAdapter;
        }

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = getString(R.string.json_results);
            final String TMDB_TITLE = getString(R.string.json_title);
            final String TMDB_RELEASE = getString(R.string.json_release_date);
            final String TMDB_VOTE_AVG = getString(R.string.json_vote_avg);
            final String TMDB_POSTER = getString(R.string.json_poster_path);
            final String TMDB_PLOT_SYNOPSIS = getString(R.string.json_plot_synop);
            final String TMDB_BACKDROP = getString(R.string.json_backdrop_path);

            String posterBaseUrl = getString(R.string.poster_base_url);
            String backdropBaseUrl = getString(R.string.backdrop_base_url);

            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

            ArrayList<Movie> movieResults = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                String title;
                String moviePlot;
                String releaseDate;
                String voteAverage;
                String posterPath;
                String backDrop;

                // Get the JSON object representing the movie
                JSONObject currentMovie = movieArray.getJSONObject(i);
                title = currentMovie.getString(TMDB_TITLE);
                moviePlot = currentMovie.getString(TMDB_PLOT_SYNOPSIS);
                releaseDate = currentMovie.getString(TMDB_RELEASE);
                voteAverage = currentMovie.getString(TMDB_VOTE_AVG);
                posterPath = posterBaseUrl + currentMovie.getString(TMDB_POSTER);
                backDrop = backdropBaseUrl + currentMovie.getString(TMDB_BACKDROP);

                Movie movie =
                        new Movie(title, posterPath, releaseDate, voteAverage, moviePlot, backDrop);
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
}