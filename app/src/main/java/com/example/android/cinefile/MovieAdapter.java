package com.example.android.cinefile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Hendercine on 10/6/16.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context context;

    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
        this.context = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        final Movie currentMovie = getItem(position);

        ImageView moviePosterIv = (ImageView) listItemView.findViewById(R.id.list_item_movie_poster);
        String poster_base_url = "http://image.tmdb.org/t/p/w185/";
        assert currentMovie != null;
        String poster_url = currentMovie.getmMoviePoster();
        String full_poster_url = poster_base_url + poster_url;

        Picasso.with(context).load(full_poster_url).into(moviePosterIv);

        return listItemView;
    }
}
