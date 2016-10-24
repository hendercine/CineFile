package com.example.android.cinefile.sync;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.cinefile.R;
import com.example.android.cinefile.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Hendercine on 10/6/16.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super( context, 0, movies); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie currentMovie = getItem(position);
        String posterUrl = currentMovie.mMoviePoster;
        ImageView moviePosterView;
        View gridItemView = convertView;

        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        moviePosterView = (ImageView) gridItemView;
        moviePosterView.setAdjustViewBounds(true);

        Picasso.with(getContext()).load(posterUrl).into(moviePosterView);

        return moviePosterView;
    }
}
