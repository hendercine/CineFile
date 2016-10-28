package com.example.android.cinefile.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.cinefile.R;
import com.example.android.cinefile.data.MovieContract;

/**
 * Created by Hendercine on 10/6/16.
 */

public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_item_movie_poster);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags, int loaderID) {
        super(context, c, flags);
        Log.d(LOG_TAG, "MovAdapter");
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.grid_item_movie;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "In bind View");

        int posterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        int poster = cursor.getInt(posterIndex);
        Log.i(LOG_TAG, "Image reference extracted: " + poster);

        viewHolder.imageView.setImageResource(poster);

    }
}
//public class MovieAdapter extends ArrayAdapter<Movie> {
//
//    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
//        super( context, 0, movies); }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//
//        Movie currentMovie = getItem(position);
//        assert currentMovie != null;
//        String posterUrl = currentMovie.mMoviePoster;
//        ImageView gridPosterView;
//        View gridItemView = convertView;
//
//        if (gridItemView == null) {
//            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
//        }
//
//        gridPosterView = (ImageView) gridItemView;
//        gridPosterView.setAdjustViewBounds(true);
//
//        Picasso.with(getContext()).load(posterUrl).into(gridPosterView);
//
//        return gridPosterView;
//    }
//}
