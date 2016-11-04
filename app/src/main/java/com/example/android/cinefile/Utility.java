package com.example.android.cinefile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.cinefile.objects.Movie;
import com.example.android.cinefile.objects.Review;
import com.example.android.cinefile.objects.Trailer;

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
 * Created by Hendercine on 10/19/16.
 */

public class Utility {
    private static String API_KEY = BuildConfig.THE_MOVIES_DB_API_KEY;

    public ArrayList<String> getPosterPaths(boolean sortByPop) throws JSONException {
        String moviesJSON = sortMovieData(sortByPop);
        JSONArray moviesArray = new JSONObject(moviesJSON).getJSONArray("results");
        ArrayList<String> posterPaths = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++)
            posterPaths.add(moviesArray.getJSONObject(i).getString("poster_path"));
        return posterPaths;
    }

    public Movie getMovieData(boolean sortByPop, int position) throws JSONException {
        String moviesJSON = sortMovieData(sortByPop);
        JSONArray moviesArray = new JSONObject(moviesJSON).getJSONArray("results");
        Movie movie = new Movie();

        movie.setMovieId(moviesArray.getJSONObject(position).getString("id"));
        movie.setMovieTitle(moviesArray.getJSONObject(position).getString("original_title"));
        movie.setReleaseDate(moviesArray.getJSONObject(position).getString("release_date"));
        movie.setBackdropPath(moviesArray.getJSONObject(position).getString("backdrop_path"));
        movie.setPosterPath(moviesArray.getJSONObject(position).getString("poster_path"));
        movie.setVoteAverage(moviesArray.getJSONObject(position).getString("vote_average"));
        movie.setPlot(moviesArray.getJSONObject(position).getString("overview"));
        movie.setTrailers(getTrailerData(movie.getMovieId()));
        movie.setReviews(getReviewData(movie.getMovieId()));
        return movie;
    }

    public ArrayList<Trailer> getTrailerData(String id) throws JSONException {
        JSONArray trailerArray = new JSONObject(getJsonData(
                "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + API_KEY))
                .getJSONArray("results");
        ArrayList<Trailer> trailers = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {
            if (trailerArray.getJSONObject(i).getString("site").equals("YouTube")) {
                Trailer trailer = new Trailer();
                trailer.setTrailerName(trailerArray.getJSONObject(i).getString("name"));
                trailer.setKey(trailerArray.getJSONObject(i).getString("key"));
                trailers.add(trailer);
            }
        }
        return trailers;
    }

    public ArrayList<Review> getReviewData(String id) throws JSONException {
        JSONArray reviewArray = new JSONObject(getJsonData(
                "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + API_KEY))
                .getJSONArray("results");
        ArrayList<Review> reviews = new ArrayList<>();

        for (int i = 0; i < reviewArray.length(); i++) {
            Review review = new Review();
            review.setAuthor(reviewArray.getJSONObject(i).getString("author"));
            review.setContent(reviewArray.getJSONObject(i).getString("content"));
            reviews.add(review);
        }
        return reviews;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public String sortMovieData(boolean sortByPop) {
        String urlString;
        if (sortByPop) {
            urlString = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
        } else {
            urlString = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
        }
        return getJsonData(urlString);
    }

    public String getJsonData(String urlString) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }
}
