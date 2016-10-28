package com.example.android.cinefile.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.cinefile.R;

/**
 * Created by Hendercine on 10/19/16.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = String.valueOf(R.string.content_authority);

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_TRAILERS;

        public static final String TABLE_NAME = "trailers";

        // The trailer ID string is the movie ID that will be sent to theMoviesDB
        // as the trailer query.
        public static final String COLUMN_TRAILER_ID = "trailer_id";

        public static final String COLUMN_TRAILER_NAME = "trailer_name";

        public static final String COLUMN_TRAILER_KEY = "trailer_key";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class MovieEntry implements BaseColumns {

        // Table Name
        public static final String TABLE_MOVIES = "movies";
        // Columns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_PLOT = "movie_plot";
        public static final String COLUMN_MOVIE_BACKDROP = "movie_backdrop";

        // Create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_MOVIES).build();
        // Create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieTrailer(String trailerId) {
            return CONTENT_URI.buildUpon().appendPath(trailerId).build();
        }

        public static Uri buildMovieTrailerWithKey(String trailerId, String trailerKey) {
            return CONTENT_URI.buildUpon().appendPath(trailerId).appendQueryParameter(COLUMN_MOVIE_ID, trailerKey).build();
        }

        public static String getTrailerIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTrailerKeyFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
}
