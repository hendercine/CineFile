package com.example.android.cinefile.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;

/**
 * Created by Hendercine on 10/20/16.
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_ID = 101;
    static final int TRAILER_KEY = 107;
    static final int REVIEW_KEY = 108;

    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;

    static{
        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();

        sMovieByIdQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
        MovieContract.TrailerEntry.TABLE_NAME +
        " ON " + MovieContract.MovieEntry.TABLE_NAME +
        "." + MovieContract.MovieEntry.COLUMN_TRAILER_KEY +
        "=" + MovieContract.TrailerEntry.TABLE_NAME +
        "." + MovieContract.MovieEntry._ID);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
