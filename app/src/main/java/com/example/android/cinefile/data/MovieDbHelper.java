package com.example.android.cinefile.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;
import static com.example.android.cinefile.data.MovieContract.MovieEntry;
import static com.example.android.cinefile.data.MovieContract.ReviewEntry;
import static com.example.android.cinefile.data.MovieContract.TrailerEntry;

/**
 * Created by Hendercine on 10/19/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL" +
                TrailerEntry.COLUMN_TRAILER_YT_ID + " TEXT UNIQUE NOT NULL" +
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL" +
                TrailerEntry.COLUMN_TRAILER_THUMB + " INTEGER NOT NULL" +
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_YT_ID + ", " +
                TrailerEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL" +
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL" +
                ReviewEntry.COLUMN_REVIEW_TEXT + " TEXT NOT NULL" +
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +
                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_AUTHOR + ", " +
                ReviewEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL" +
                MovieEntry.COLUMN_TRAILER_KEY + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_REVIEW_KEY + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL" +
                MovieEntry.COLUMN_MOVIE_POSTER + " INTEGER NOT NULL" +
                MovieEntry.COLUMN_MOVIE_BACKDROP + " INTEGER NOT NULL" +
                MovieEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL" +
                MovieEntry.COLUMN_MOVIE_AVG_VOTE + " TEXT NOT NULL" +

                " FOREIGN KEY (" + MovieEntry.COLUMN_TRAILER_KEY + ") REFERENCES " +
                TrailerEntry.TABLE_NAME + " (" + TrailerEntry._ID + "), " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ", " +
                MovieEntry.COLUMN_TRAILER_KEY + ") ON CONFLICT REPLACE);" +

                " FOREIGN KEY (" + MovieEntry.COLUMN_REVIEW_KEY + ") REFERENCES " +
                ReviewEntry.TABLE_NAME + " (" + ReviewEntry._ID + "), " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ", " +
                MovieEntry.COLUMN_REVIEW_KEY + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
