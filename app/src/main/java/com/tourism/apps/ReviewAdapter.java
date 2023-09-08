package com.tourism.apps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviewList;
    private Context context;

    public ReviewAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // Set user data
        holder.usernameTextView.setText(review.getUsername());

        // Load place image using Glide library
        Glide.with(context)
                .load(review.getUserImgUrl())
                .into(holder.userImageView);

        // Set rating
        holder.ratingBar.setRating(review.getRating());

        // Set date submitted and review description
        holder.dateSubmittedTextView.setText(review.getDateSubmitted());
        holder.reviewDescriptionTextView.setText(review.getReviewDescription());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImageView;
        TextView usernameTextView;
        RatingBar ratingBar;
        TextView dateSubmittedTextView;
        TextView reviewDescriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userProfileImage);
            usernameTextView = itemView.findViewById(R.id.username);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            dateSubmittedTextView = itemView.findViewById(R.id.dateSubmitted);
            reviewDescriptionTextView = itemView.findViewById(R.id.reviewDescription);
        }
    }
}

