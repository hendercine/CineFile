//package com.example.android.cinefile;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Hendercine on 10/2/16.
// */
//
//public class ImageAdapter extends ArrayAdapter {
//
//    private Context context;
//    private List<String> mMovieData = null;
//    private int resource = R.layout.list_item_movie;
//    final String TMDB_RESULTS = "results";
//    static final String TMDB_TITLE = "title";
//    final String TMDB_RELEASE = "release_date";
//    static final String TMDB_VOTE_AVG = "vote_average";
//    static final String TMDB_POSTER = "poster_path";
//    final String TMDB_SYNOPSIS = "overview";
//
//    public ImageAdapter(Context context, int resource, List mMovieData) {
//        super(context, resource, mMovieData);
//        this.context = context;
//        this.mMovieData = mMovieData;
//    }
//
//    @Override
//    public int getCount() {
//        return mMovieData.size();
//    }
//
//    @Nullable
//    @Override
//    public Object getItem(int position) {
//        if (mMovieData != null && mMovieData.size() > 0) {
//            return mMovieData.get(position);
//        }
//
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    class ViewHolder {
//        ImageView imageView;
//
//        ViewHolder(View view) {
//            imageView = (ImageView) view.findViewById(R.id.list_item_movie_poster);
//        }
//    }
//
//    public void updateData(ArrayList<String> movieData) {
//        this.mMovieData = movieData;
//        notifyDataSetChanged();
//    }
//
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        if (android.os.Debug.isDebuggerConnected())
//            android.os.Debug.waitForDebugger();
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            convertView = inflater.inflate(resource, parent, false);
//            viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        if (mMovieData != null || mMovieData.size() > 0) {
//            String poster_path = mMovieData.get(position).get(ImageAdapter.TMDB_POSTER);
//            String poster_base_path = "http://image.tmdb.org/t/p/w185/";
//            String poster_final_path = poster_base_path + poster_path;
//            Log.d(getClass().toString(), "AdapterClass" + mMovieData.get(position).get(ImageAdapter.TMDB_TITLE));
//            Log.d(getClass().toString(), "Image Complete path" + poster_final_path);
//            Log.d(getClass().toString(), "Rating bar value" + mMovieData.get(position).get(ImageAdapter.TMDB_VOTE_AVG));
//
//            Picasso.with(context).load(poster_final_path).into(viewHolder.imageView);
//        } else {
//            Log.d(getClass().toString(), "mMovieData is empty or null");
//        }
//
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //ON click on the movie poster... pass the intent to new activity
//                Intent detailsIntent = new Intent(context, DetailActivity.class);
//                detailsIntent.putExtra("MovieDetailData",  mMovieData.get(position));
//                context.startActivity(detailsIntent);
//            }
//        });
//        return convertView;
//    }
//}