package com.example.android.cinefile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cinefile.data.Movie;
import com.example.android.cinefile.sync.TrailerAdapter;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private Movie mMovie;
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View detailView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            //pass title
            if (intent != null && intent.hasExtra("MOVIE_TITLE")) {
                String movie_title = intent.getStringExtra("MOVIE_TITLE");
                ((TextView) detailView.findViewById(R.id.movie_title_text))
                        .setText(movie_title);
                // set activity title
                getActivity().setTitle(movie_title);
                //pass poster image
                String movie_poster = intent.getStringExtra("MOVIE_POSTER");
                Log.i(LOG_TAG, "poster URL: " + movie_poster);
                ImageView poster =
                        (ImageView) detailView
                        .findViewById(R.id.detail_poster_image_view);
                Picasso
                        .with(getActivity())
                        .load(movie_poster)
                        .placeholder(R.drawable.placeholder_portrait)
                        .error(R.drawable.placeholder_portrait)
                        .fit()
                        .into(poster);
                //pass backdrop image
                String movie_backdrop = intent.getStringExtra("MOVIE_BACKDROP");
                Log.i(LOG_TAG, "Backdrop URL: " + movie_backdrop);
                ImageView backdrop =
                        (ImageView) detailView.findViewById(R.id.backdrop_image_view);
                Picasso
                        .with(getContext())
                        .load(movie_backdrop)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .resize(200, 250)
                        .onlyScaleDown()
                        .centerInside()
                        .into(backdrop);
                //pass release date
                String movie_release = intent.getStringExtra("MOVIE_RELEASE");
                ((TextView) detailView.findViewById(R.id.movie_release_date_text))
                        .setText(movie_release);
                //pass vote average
                String movie_rating = intent.getStringExtra("MOVIE_VOTE_AVG");
                ((TextView) detailView.findViewById(R.id.movie_vote_avg_text))
                        .setText(movie_rating);
                //pass plot summary
                String movie_overview = intent.getStringExtra("MOVIE_PLOT_SUMMARY");
                ((TextView) detailView.findViewById(R.id.movie_summary_text))
                        .setText(movie_overview);
                //pass trailer
                final String movie_trailer = intent.getStringExtra("MOVIE_TRAILER");

                if (mMovie != null) {
                    if (mMovie.getMovieTrailer().size() != 0) {
                        RecyclerView trailerRecyclerView =
                                (RecyclerView) detailView.findViewById(R.id.trailers_recycler);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                                getActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false);
                        if (trailerRecyclerView != null) {
                            trailerRecyclerView.setHasFixedSize(false);
                            trailerRecyclerView.setLayoutManager(layoutManager);
                            TrailerAdapter trailerAdapter = new TrailerAdapter(
                                    mMovie.getMovieTrailer(),
                                    getActivity());
                            trailerRecyclerView.setAdapter(trailerAdapter);
                            trailerAdapter.setOnItemClickListener(new TrailerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent trailerIntent = new Intent(movie_trailer);
                                    trailerIntent.setData(Uri.parse("https://www.youtube.com/watch?v="
                                            + mMovie.getMovieTrailer().get(position).getmTrailerKey()));
                                    if (trailerIntent
                                            .resolveActivity(getActivity()
                                                    .getPackageManager()) != null)
                                        startActivity(trailerIntent);
                                    else
                                        Snackbar.make(getView().findViewById(R.id.parent_scrollView),
                                                R.string.no_browser_msg,
                                                Snackbar.LENGTH_LONG)
                                                .show();
                                }
                            });
                        } else
                            detailView.findViewById(R.id.noTrailers_textView).setVisibility(View.VISIBLE);
                    }
                }
            }
            return detailView;
        }
    }
}