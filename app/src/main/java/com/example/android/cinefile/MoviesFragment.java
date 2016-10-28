package com.example.android.cinefile;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.cinefile.Adapters.MovieAdapter;
import com.example.android.cinefile.data.MovieContract;
import com.example.android.cinefile.sync.MovieSyncAdapter;

/**
 * Encapsulates fetching the movies and displaying it as a GridView layout.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private GridView mGridView;

    private static final String SELECTED_KEY = "selected_position";

    private static final int CURSOR_LOADER_ID = 0;

//    //Keys for Intent to detail activity
//    public static final String MOVIE_ID = "MOVIE_ID";
//    public static final String MOVIE_TITLE = "MOVIE_TITLE";
//    public static final String MOVIE_POSTER = "MOVIE_POSTER";
//    public static final String MOVIE_BACKDROP = "MOVIE_BACKDROP";
//    public static final String MOVIE_RELEASE = "MOVIE_RELEASE";
//    public static final String MOVIE_VOTE_AVG = "MOVIE_VOTE_AVG";
//    public static final String MOVIE_PLOT_SUMMARY = "MOVIE_PLOT_SUMMARY";
//    public static final String MOVIE_TRAILER = "MOVIE_TRAILER";

    // Specify columns
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_MOVIES + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_PLOT
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_BACKDROP = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_VOTE_AVERAGE = 5;
    static final int COL_MOVIE_PLOT = 6;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri movieUri);
    }

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Cursor cursor =
                getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);
        if (cursor != null && cursor.getCount() == 0) {
            updateMovies();
            cursor.close();
        }
        // Initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate fragment_main layout
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialize our MovieAdapter
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0, CURSOR_LOADER_ID);
        // Initialize mGridView to the GridView in fragment_main
        mGridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
        // Set mGridview adapter to our CursorAdapter
        mGridView.setAdapter(mMovieAdapter);

        // Make each item clickable
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String sortSetting = Utility.getSortOrder(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry
                                    .buildMovieUri(cursor.getLong(COL_MOVIE_POSTER)));
                }
                // append Id to uri
                Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,
                        COL_MOVIE_TITLE);
                // create fragment
                assert cursor != null;
                DetailFragment detailFragment = DetailFragment.newInstance(cursor.getPosition(), uri);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_fragment_container, detailFragment)
                        .addToBackStack(null).commit();
            }

        });

        return rootView;
    }

    private void updateMovies() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMovieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { mMovieAdapter.swapCursor(null); }
}