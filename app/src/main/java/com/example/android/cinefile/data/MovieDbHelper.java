package com.example.android.cinefile.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Hendercine on 10/19/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "
                + MovieContract.MovieEntry.TABLE_MOVIES + "(" + MovieContract.MovieEntry._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_PLOT + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ". OLD DATA WILL BE DESTROYED.");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE SEQUENCE WHERE NAME = '"
        + MovieContract.MovieEntry.TABLE_MOVIES + "'");

        // Recreate database
        onCreate(sqLiteDatabase);

    }
}
