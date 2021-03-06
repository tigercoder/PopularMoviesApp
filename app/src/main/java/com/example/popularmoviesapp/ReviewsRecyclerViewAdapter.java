package com.example.popularmoviesapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ReviewsRecyclerViewAdapterViewHolder> {
    private ArrayList<String> mAuthors;
    private ArrayList<String> mReviews;

    ReviewsRecyclerViewAdapter(ArrayList<String> authors, ArrayList<String> reviews) {
        mAuthors = authors;
        mReviews = reviews;
    }

    @NonNull
    @Override
    public ReviewsRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewsRecyclerViewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsRecyclerViewAdapterViewHolder holder, int position) {
        holder.reviewerTextView.setText(mAuthors.get(position));
        holder.reviewTextView.setText(mReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mAuthors.size();
    }

    class ReviewsRecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView reviewerTextView;
        final TextView reviewTextView;

        ReviewsRecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
            reviewerTextView = itemView.findViewById(R.id.reviewer);
            reviewTextView = itemView.findViewById(R.id.review);
        }
    }
}
