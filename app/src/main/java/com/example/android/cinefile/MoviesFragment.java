package com.example.android.cinefile;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
 * Encapsulates fetching the movies and displaying it as a {@link ListView} layout.
 */
public class MoviesFragment extends Fragment {

    private MovieAdapter adapter;
    private ArrayList<Movie> movies;
    private ArrayAdapter<String> mMoviesAdapter;

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
        movies = new ArrayList<>();
        adapter = new MovieAdapter(getActivity(), movies);
        adapter.clear();
        mMoviesAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_movie,
                R.id.list_item_movie_poster,
                new ArrayList<String>());
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_posters);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String movieDetail = mMoviesAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), DetailActivity.class).
                        putExtra(Intent.EXTRA_TEXT, movieDetail);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //           TODO: Remove or reuse the folded code below.

//        // The ArrayAdapter will take data from a source and
//        // use it to populate the ListView it's attached to.
//        mMoviesAdapter =
//                new ArrayAdapter<>(
//                        getActivity(), // The current context (this activity)
//                        R.layout.list_item_movie, // The name of the layout ID.
//                        R.id.list_item_movie_poster, // The ID of the textview to populate.
//                        new ArrayList<String>());
//
//        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//
//        // Get a reference to the ListView, and attach this adapter to it.
//        ListView listView = (ListView) rootView.findViewById(R.id.list_view_posters);
//        listView.setAdapter(mMoviesAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String moviePoster = mMoviesAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, moviePoster);
//                startActivity(intent);
//            }
//        });
//
//        return rootView;
//    }
//
//    private void updateMovies() {
//        FetchMoviesTask moviesTask = new FetchMoviesTask();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String sortOrder = prefs.getString(getString(R.string.pref_sort_key),
//                getString(R.string.pref_sort_label_popular));
//        moviesTask.execute(sortOrder);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        updateMovies();
//    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = getString(R.string.json_results);
            final String TMDB_TITLE = getString(R.string.json_title);
            final String TMDB_RELEASE = getString(R.string.json_release_date);
            final String TMDB_VOTE_AVG = getString(R.string.json_vote_avg);
            final String TMDB_POSTER = getString(R.string.json_poster_path);
            final String TMDB_SYNOPSIS = getString(R.string.json_plot_synop);
            movies = new ArrayList<>();

            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

            String[] resultStrs = new String[movieArray.length()];

            //TODO: Remove or move SharePrefs.
//            SharedPreferences sharedPrefs =
//                    PreferenceManager.getDefaultSharedPreferences(getActivity());
//            String sortType = sharedPrefs.getString(getString(
//                    R.string.pref_sort_key),
//                    getString(R.string.pref_sort_popular));

            for(int i = 0; i < movieArray.length(); i++) {
                String title;
                String synopsis;
                String releaseDate;
                String vote_average;
                String poster_path;

                // Get the JSON object representing the movie
                JSONObject resultsObject = movieArray.getJSONObject(i);
                title = resultsObject.getString(TMDB_TITLE);
                synopsis = resultsObject.getString(TMDB_SYNOPSIS);
                releaseDate = resultsObject.getString(TMDB_RELEASE);
                vote_average = resultsObject.getString(TMDB_VOTE_AVG);
                poster_path = resultsObject.getString(TMDB_POSTER);

                movies.add(i, new Movie(title, poster_path, releaseDate, vote_average, synopsis));
            }

            return movies;

        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String sortOrder = "popular";

            try {
                // Construct the URL for the TheMovieDB query
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String SORT_PARAM = "popular";
                final String APPID_PARAM = "api_key";
                final String LANG_PARAM = "language";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL + SORT_PARAM).buildUpon()
                        .appendQueryParameter(LANG_PARAM, "en-US")
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIES_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URL: " + url);

                // Create the request to OpenWeatherMap, and open the connection
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                adapter.addAll(movies);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", movies);
        super.onSaveInstanceState(outState);
    }
}