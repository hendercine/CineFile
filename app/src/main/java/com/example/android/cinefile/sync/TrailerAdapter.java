package com.example.android.cinefile.sync;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cinefile.R;
import com.example.android.cinefile.data.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Hendercine on 10/23/16.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static OnItemClickListener mItemClickListener;
    private ArrayList<Trailer> mTrailers;
    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextViewTitle;
        ImageView mImageViewThumbnail;

        ViewHolder(View v) {
            super(v);
            mImageViewThumbnail = (ImageView) v.findViewById(R.id.video_thumbnail);
            mTextViewTitle = (TextView) v.findViewById(R.id.video_title);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public TrailerAdapter(ArrayList<Trailer> trailers, Context context) {
        mTrailers = trailers;
        mContext = context;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Picasso.with(mContext).
                load("http://img.youtube.com/vi/" + mTrailers.get(position).getTrailerKey() + "/0.jpg").
                into(holder.mImageViewThumbnail);
        holder.mTextViewTitle.setText(mTrailers.get(position).getTrailerName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }
// TODO: Use or delete this method.
//    public void swap(ArrayList<Trailer> newTrailerList) {
//        mTrailers.clear();
//        mTrailers.addAll(newTrailerList);
//        notifyDataSetChanged();
//    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        TrailerAdapter.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}