package com.example.android.cinefile.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.cinefile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * CineFile_Stage_2 created by Hendercine on 10/6/16.
 */

public class MainViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mPaths;

    public MainViewAdapter(Context context, ArrayList<String> paths) {
        mContext = context;
        this.mPaths = paths;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView gridPosterView;

        if (convertView == null)
            gridPosterView = new ImageView(mContext);
        else
            gridPosterView = (ImageView) convertView;

            gridPosterView.setAdjustViewBounds(true);
            gridPosterView.setScaleType(ImageView.ScaleType.FIT_XY);

            String posterUrl = "http://image.tmdb.org/t/p/w185/";
            Picasso.with(mContext).
                    load(posterUrl + mPaths.get(position)).
                    placeholder(R.drawable.placeholder_portrait).
                    into(gridPosterView);

        return gridPosterView;
    }
}
