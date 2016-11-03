package com.example.android.cinefile.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Hendercine on 10/19/16.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.cinefile.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";

    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_TRAILER_YT_ID = "trailer_yt_key";

        public static final String COLUMN_TRAILER_NAME = "trailer_name";

        public static final String COLUMN_TRAILER_THUMB = "trailer_thumb";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_REVIEW_TEXT = "review_text";

        public static final String COLUMN_REVIEW_AUTHOR = "review_author";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        public static final String COLUMN_MOVIE_POSTER = "movie_poster";

        public static final String COLUMN_MOVIE_BACKDROP = "movie_backdrop";

        public static final String COLUMN_MOVIE_DATE = "movie_date";

        public static final String COLUMN_MOVIE_AVG_VOTE = "movie_avg_vote";

        public static final String COLUMN_TRAILER_KEY = "trailer_id";

        public static final String COLUMN_REVIEW_KEY = "review_id";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieSorted(String sortSetting) {
            return null;
        }

        public static Uri buildMovieTrailer(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId)
                    .appendQueryParameter(COLUMN_TRAILER_KEY, COLUMN_MOVIE_ID).build();
        }

        public static Uri buildMovieReview(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId)
                    .appendQueryParameter(COLUMN_REVIEW_KEY, COLUMN_MOVIE_ID).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTrailerKeyFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getReviewKeyFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
    }
}
