package com.example.android.cinefile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.cinefile.R;
import com.example.android.cinefile.objects.Review;

import java.util.ArrayList;

/**
 * CineFile_Stage_2 created by Hendercine on 11/3/16.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private ArrayList<Review> mReviewArrayList;

    public ReviewAdapter(ArrayList<Review> reviewArrayList) {
        this.mReviewArrayList = reviewArrayList;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTextViewAuthor.setText(mReviewArrayList.get(position).getAuthor());
        holder.mTextViewContent.setText(mReviewArrayList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewArrayList.size();
    }

    public void swap(ArrayList<Review> newReviewArrayList) {
        mReviewArrayList.clear();
        mReviewArrayList.addAll(newReviewArrayList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewAuthor, mTextViewContent;

        public ViewHolder(View v) {
            super(v);
            mTextViewAuthor = (TextView) v.findViewById(R.id.author_textView);
            mTextViewContent = (TextView) v.findViewById(R.id.content_textView);
        }
    }

}
