package com.example.android.cinefile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.cinefile.data.Movie;
import com.example.android.cinefile.sync.MovieAdapter;

import java.util.ArrayList;

/**
 * Encapsulates fetching the movies and displaying it as a GridView layout.
 */
public class MoviesFragment extends Fragment {

    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    //Keys for Intent to detail activity
    public static final String MOVIE_TITLE = "MOVIE_TITLE";
    public static final String MOVIE_POSTER = "MOVIE_POSTER";
    public static final String MOVIE_BACKDROP = "MOVIE_BACKDROP";
    public static final String MOVIE_RELEASE = "MOVIE_RELEASE";
    public static final String MOVIE_VOTE_AVG = "MOVIE_VOTE_AVG";
    public static final String MOVIE_PLOT_SUMMARY = "MOVIE_PLOT_SUMMARY";

    //ArrayList of movies
    public ArrayList<Movie> movies = new ArrayList<>();

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView= (GridView) rootView.findViewById(R.id.grid_view_posters);
        mMovieAdapter = new MovieAdapter(getActivity(), movies);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class).
                        putExtra(MOVIE_TITLE, movies.get(position).mMovieTitle).
                        putExtra(MOVIE_POSTER, movies.get(position).mMoviePoster).
                        putExtra(MOVIE_BACKDROP, movies.get(position).mMovieBackDrop).
                        putExtra(MOVIE_RELEASE, movies.get(position).mReleaseDate).
                        putExtra(MOVIE_VOTE_AVG, movies.get(position).mVoteAverage).
                        putExtra(MOVIE_PLOT_SUMMARY, movies.get(position).mMoviePlot);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask(this);
        moviesTask.setmMovieAdapter(mMovieAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        moviesTask.execute(sortOrder);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

}