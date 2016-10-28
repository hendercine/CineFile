package com.example.android.cinefile.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.cinefile.BuildConfig;
import com.example.android.cinefile.Adapters.MovieAdapter;
import com.example.android.cinefile.MoviesFragment;
import com.example.android.cinefile.R;
import com.example.android.cinefile.Utility;
import com.example.android.cinefile.Objects.Movie;
import com.example.android.cinefile.Objects.Trailer;

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
 * CineFile_Stage_2 created by Hendercine on 10/20/16.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    private MoviesFragment moviesFragment;
    private MovieAdapter mMovieAdapter;

    private final String TMDB_TRAILER = String.valueOf(R.string.trailer_path);

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        //Enable periodic sync
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");
        String sortOrder = Utility.getSortOrder(getContext());

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr;

        try {
            // Construct the URL for the TheMovieDB query

            final String APPID_PARAM = "api_key";
            final String LANG_PARAM = "language";

            String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            Uri builtUri = Uri.parse(MOVIE_BASE_URL + sortOrder).buildUpon()
                    .appendQueryParameter(LANG_PARAM, "en-US")
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIES_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            //TODO: Remove verbose logs.
            Log.v(LOG_TAG, "Built URL: " + url);

            // Create the request to TheMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            moviesJsonStr = buffer.toString();
            getMovieDataFromJson(moviesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in
            // attempting to parse it.
        } catch (JSONException e) {
            e.printStackTrace();
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
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = String.valueOf(R.string.json_results);
        final String TMDB_TITLE = String.valueOf(R.string.json_title);
        final String TMDB_RELEASE = String.valueOf(R.string.json_release_date);
        final String TMDB_VOTE_AVG = String.valueOf(R.string.json_vote_avg);
        final String TMDB_POSTER = String.valueOf(R.string.json_poster_path);
        final String TMDB_PLOT_SYNOPSIS = String.valueOf(R.string.json_plot_synop);
        final String TMDB_BACKDROP = String.valueOf(R.string.json_backdrop_path);
        final String TMDB_ID = String.valueOf(R.string.api_movie_id);

        String posterBaseUrl = String.valueOf(R.string.poster_base_url);
        String backdropBaseUrl = String.valueOf(R.string.backdrop_base_url);

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
            ArrayList<Trailer> trailerArrayList;

            // Get the JSON object representing the movie
            JSONObject currentMovie = movieArray.getJSONObject(i);
            movieId = currentMovie.getString(TMDB_ID);
            title = currentMovie.getString(TMDB_TITLE);
            moviePlot = currentMovie.getString(TMDB_PLOT_SYNOPSIS);
            releaseDate = currentMovie.getString(TMDB_RELEASE);
            voteAverage = currentMovie.getString(TMDB_VOTE_AVG);
            posterPath = posterBaseUrl + currentMovie.getString(TMDB_POSTER);
            backDrop = backdropBaseUrl + currentMovie.getString(TMDB_BACKDROP);
            trailerArrayList = getTrailerDataFromJson(movieId);

            Movie movie =
                    new Movie(movieId, title, posterPath, releaseDate, voteAverage, moviePlot, backDrop, trailerArrayList);
            movieResults.add(movie);
        }

        return movieResults;

    }

    public ArrayList<Trailer> getTrailerDataFromJson(String movieID) throws JSONException {

        String baseTrailerJsonURL = movieID + TMDB_TRAILER;
        JSONObject trailersJson = new JSONObject(baseTrailerJsonURL);
        JSONArray trailerArray = trailersJson.getJSONArray("results");
        ArrayList<Trailer> trailerResults = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject currentTrailer = trailerArray.getJSONObject(i);
            if (currentTrailer.getString("site").equals("YouTube")) {
                Trailer trailer = (Trailer) Trailer.CREATOR;
                trailer.setmTrailerKey(currentTrailer.getString("key"));
                trailer.setmTrailerName(currentTrailer.getString("name"));
                trailerResults.add(trailer);
            }

        }

        return trailerResults;

    }

    /**
     * Helper method to sync the SyncAdapter immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type)
        );

        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

        }

        return newAccount ;
    }
}
