package com.example.android.cinefile;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cinefile.Objects.Movie;
import com.example.android.cinefile.Objects.Trailer;
import com.example.android.cinefile.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    // Specify columns
    private static final String[] DETAIL_COLUMNS = {
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

    private Movie mMovie;
    private Trailer mTrailer;
    private Cursor mDetailCursor;
    private View mRootView;
    private int mPosition;
    private ImageView mPosterView;
    private ImageView mBackdropView;
    private TextView mMovieTitleView;
    private TextView mReleaseDateView;
    private TextView mVoteAverageView;
    private TextView mPlotSummaryView;
    private RecyclerView mTrailerView;
    private RecyclerView mReviewView;
    private Uri mUri;
    private static final int CURSOR_LOADER_ID = 0;


    public static DetailFragment newInstance(int position, Uri uri) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.mPosition = position;
        fragment.mUri = uri;
        args.putInt("id", position);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMovieTitleView = (TextView) detailView.findViewById(R.id.movie_title_text);
        mPosterView = (ImageView) detailView.findViewById(R.id.detail_poster_image_view);
        mBackdropView = (ImageView) detailView.findViewById(R.id.backdrop_image_view);
        mReleaseDateView = (TextView) detailView.findViewById(R.id.movie_release_date_text);
        mVoteAverageView = (TextView) detailView.findViewById(R.id.movie_vote_avg_text);
        mPlotSummaryView = (TextView) detailView.findViewById(R.id.movie_overview_text);
        mTrailerView = (RecyclerView) detailView.findViewById(R.id.trailers_recycler);
        mReviewView = (RecyclerView) detailView.findViewById(R.id.reviews_recycler);
//            //pass trailers
//            String movie_trailer = intent.getStringExtra("MOVIE_TRAILER");
//            Log.i(LOG_TAG, "Trailer URL: " + movie_trailer);
//            if (mMovie.getMovieTrailer().size() != 0) {
//                RecyclerView trailerRecyclerView =
//                        (RecyclerView) detailView.findViewById(R.id.trailers_recycler);
//                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
//                        getActivity(),
//                        LinearLayoutManager.HORIZONTAL,
//                        false);
//                if (trailerRecyclerView != null) {
//                    trailerRecyclerView.setHasFixedSize(false);
//                    trailerRecyclerView.setLayoutManager(layoutManager);
//                    TrailerAdapter trailerAdapter = new TrailerAdapter(
//                            mMovie.getMovieTrailer(),
//                            getActivity());
//                    trailerRecyclerView.setAdapter(trailerAdapter);
//                    trailerAdapter.setOnItemClickListener(new TrailerAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
//                            trailerIntent.setData(Uri.parse("https://www.youtube.com/watch?v="
//                                    + mMovie.getMovieTrailer().get(position).getmTrailerKey()));
//                            if (trailerIntent
//                                    .resolveActivity(getActivity()
//                                            .getPackageManager()) != null)
//                                startActivity(trailerIntent);
//                            else Snackbar.make(getView().findViewById(R.id.parent_scrollView),
//                                    R.string.no_browser_msg,
//                                    Snackbar.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//                } else detailView.findViewById(R.id.noTrailers_textView).setVisibility(View.VISIBLE);
//            }
        return detailView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
        }
        return new CursorLoader(getActivity(),
                mUri,
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Set the cursor in our CursorAdapter once the Cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDetailCursor = data;
        mDetailCursor.moveToFirst();
        DatabaseUtils.dumpCursor(data);
        mMovieTitleView.setText(mDetailCursor.getString(COL_MOVIE_TITLE));
        Picasso
                .with(getActivity())
                .load(mDetailCursor.getInt(COL_MOVIE_POSTER))
                .placeholder(R.drawable.placeholder_portrait)
                .error(R.drawable.placeholder_portrait)
                .fit()
                .into(mPosterView);
        Picasso
                .with(getContext())
                .load(mDetailCursor.getInt(COL_MOVIE_BACKDROP))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .resize(200, 250)
                .onlyScaleDown()
                .centerInside()
                .into(mBackdropView);
        mReleaseDateView.setText(mDetailCursor.getString(COL_RELEASE_DATE));
        mVoteAverageView.setText(mDetailCursor.getString(COL_VOTE_AVERAGE));
        mPlotSummaryView.setText(mDetailCursor.getString(COL_MOVIE_PLOT));
    }

    // Reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { mDetailCursor = null; }
}
