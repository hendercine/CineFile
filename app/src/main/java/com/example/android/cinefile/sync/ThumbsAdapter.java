package com.example.android.cinefile.sync;

import android.app.Activity;
import android.support.v7.widget.CardView;
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
 * Created by Hendercine on 10/23/16.
 */

public class ThumbsAdapter extends ArrayAdapter<Movie> {

    public ThumbsAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie currentMovie = getItem(position);
        String posterUrl = currentMovie.mMoviePoster;
        CardView cardView;
        ImageView detailPosterView;
        View cardViewItem = convertView;

        if (cardViewItem == null) {
            cardViewItem = LayoutInflater.from(getContext()).inflate(R.layout.poster_layout, parent, false);
        }

        cardView = (CardView) cardViewItem;
        detailPosterView = (ImageView) cardView.findViewById(R.id.detail_poster_image_view);
        detailPosterView.setAdjustViewBounds(true);

        Picasso.with(getContext()).load(posterUrl).into(detailPosterView);

        return detailPosterView;
    }
}
