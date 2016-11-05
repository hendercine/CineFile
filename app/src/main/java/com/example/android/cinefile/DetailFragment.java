package com.example.android.cinefile;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.cinefile.adapters.ReviewAdapter;
import com.example.android.cinefile.adapters.TrailerAdapter;
import com.example.android.cinefile.data.MovieDbHandler;
import com.example.android.cinefile.objects.Movie;
import com.example.android.cinefile.objects.Review;
import com.example.android.cinefile.objects.Trailer;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * CineFile_Stage_2 created by Hendercine on 10/30/16.
 */
public class DetailFragment extends Fragment {

    private Movie mMovie = new Movie();
    private Review mReview = new Review();
    private ShareActionProvider mShareActionProvider;
    private CheckBox mFavCheckBox;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mMovie = Parcels.unwrap(getArguments().getParcelable("movie"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Pass Title text
        TextView title = (TextView) detailView.findViewById(R.id.movie_title_text);
        title.setText(mMovie.getMovieTitle());

        //Pass Poster thumbnail
        Picasso.with(getContext()).
                load("http://image.tmdb.org/t/p/w185/" + mMovie.getPosterPath()).
                placeholder(R.drawable.placeholder_portrait).
                into((ImageView) detailView.findViewById(R.id.detail_poster_image_view));

        //Pass Backdrop Image
        Picasso.with(getContext()).
                load("http://image.tmdb.org/t/p/w185/" + mMovie.getBackdropPath()).
                placeholder(R.drawable.placeholder).
                into((ImageView) detailView.findViewById(R.id.backdrop_image_view));

        //Pass Release Date
        SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        try {
            ((TextView) detailView.findViewById(R.id.movie_release_date_text)).
                    setText(DateFormat.getDateInstance(DateFormat.SHORT).
                            format(input.parse(mMovie.getReleaseDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Pass Vote Average
        ((TextView) detailView.findViewById(R.id.movie_vote_avg_text)).
                setText(String.valueOf(mMovie.getVoteAverage()));

        //Pass Plot Summary
        ((TextView) detailView.findViewById(R.id.movie_summary_text)).setText(mMovie.getPlot());

        //Setup Favorites Checkbox
        mFavCheckBox = (CheckBox) detailView.findViewById(R.id.fav_btn);
        new DbTask().execute("=");
        //Use onClick() to avoid CheckedStateChange(caused by above statement) to invoke onCheckedStateChange()
        mFavCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String operation;
                if (mFavCheckBox.isChecked())
                    operation = "+";
                else operation = "-";
                new DbTask().execute(operation);
            }
        });

        //Pass Trailers
        if (mMovie.getTrailers().size() != 0) {
            RecyclerView trailerRecyclerView = (RecyclerView) detailView.findViewById(R.id.trailers_recycler);
            RecyclerView.LayoutManager llm = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            if (trailerRecyclerView != null) {
                trailerRecyclerView.setHasFixedSize(false);
                trailerRecyclerView.setLayoutManager(llm);
                TrailerAdapter trailerAdapter = new TrailerAdapter(mMovie.getTrailers(), getActivity());
                trailerRecyclerView.setAdapter(trailerAdapter);
                trailerAdapter.setOnItemClickListener(new TrailerAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent yIntent = new Intent(Intent.ACTION_VIEW);
                        yIntent.setData(Uri.parse("https://www.youtube.com/watch?v="
                                + mMovie.getTrailers().get(position).getKey()));
                        if (yIntent.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(yIntent);
                        else Snackbar.make(view.findViewById(R.id.parent_scrollView),
                                R.string.no_browser_msg,
                                Snackbar.LENGTH_LONG).
                                show();
                    }
                });
            }
        } else detailView.findViewById(R.id.noTrailers_textView).setVisibility(View.VISIBLE);

        //Pass Reviews
        if (mMovie.getReviews().size() != 0) {
            TextView authorView = (TextView) detailView.findViewById(R.id.reviews_author);
            authorView.setText(mReview.getAuthor());

            RecyclerView reviewRecyclerView = (RecyclerView) detailView.findViewById(R.id.reviews_recycler);
            if (reviewRecyclerView != null) {
                reviewRecyclerView.setHasFixedSize(false);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
                    @Override
                    // This will avoid RecyclerView from interfering with it's parent ScrollView
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
                reviewRecyclerView.setLayoutManager(layoutManager);
                reviewRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.
                        Builder(getActivity()).
                        size(5).
                        build());
                ReviewAdapter reviewAdapter = new ReviewAdapter(mMovie.getReviews());
                reviewRecyclerView.setAdapter(reviewAdapter);
            }
        } else detailView.findViewById(R.id.noReviews_textView).setVisibility(View.VISIBLE);

        return detailView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ScrollView parent = (ScrollView) getActivity().findViewById(R.id.parent_scrollView);
        outState.putIntArray("DETAILS_PARENT_SCROLL_STATE",
                new int[]{parent.getScrollX(), parent.getScrollY()});
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            final int[] parentPosition = savedInstanceState.getIntArray("DETAILS_PARENT_SCROLL_STATE");
//            final int[] childPosition = savedInstanceState.getIntArray("DETAILS_CHILD_SCROLL_STATE");
            ScrollView parent = (ScrollView) getActivity().findViewById(R.id.parent_scrollView);

            assert parentPosition != null;
            parent.scrollTo(parentPosition[0], parentPosition[1]);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mMovie.getTrailers().size() != 0)
            setShareIntent(mMovie.getTrailers().get(0));
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setShareIntent(Trailer trailer) {
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.getMovieTitle());
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, mMovie.getMovieTitle() +
                    ": " + trailer.getTrailerName() + ": " +
                    "https://www.youtube.com/watch?v=" +
                    trailer.getKey());
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public class DbTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            MovieDbHandler db = new MovieDbHandler(getActivity().getApplicationContext());
            if (params[0].equals("="))
                return db.isFav(mMovie.getMovieId());
            if (params[0].equals("+"))
                db.addFavMovie(mMovie);
            if (params[0].equals("-"))
                db.deleteFavMovie(mMovie.getMovieId());
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null)
                if (result)
                    mFavCheckBox.setChecked(true);
        }

    }

}