package com.example.myapplication.ui.trails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.TrailReview;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<TrailReview> reviews = new ArrayList<>();
    private OnReviewDeleteListener deleteListener;

    public interface OnReviewDeleteListener {
        void onDelete(TrailReview review);
    }

    public ReviewAdapter(OnReviewDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void submitList(List<TrailReview> newReviews) {
        this.reviews = newReviews != null ? newReviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        TrailReview review = reviews.get(position);
        holder.bind(review, deleteListener);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView date;
        private TextView comment;
        private ViewGroup starsContainer;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.review_user_name);
            date = itemView.findViewById(R.id.review_date);
            comment = itemView.findViewById(R.id.review_comment);
            starsContainer = itemView.findViewById(R.id.review_rating_stars);
        }

        public void bind(TrailReview review, OnReviewDeleteListener deleteListener) {
            userName.setText(review.getUserName());
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            date.setText(df.format(new java.util.Date(review.getCreatedAt())));
            comment.setText(review.getComment() != null ? review.getComment() : "");

            starsContainer.removeAllViews();
            for (int i = 1; i <= 5; i++) {
                ImageView star = new ImageView(starsContainer.getContext());
                star.setLayoutParams(new ViewGroup.LayoutParams(16, 16));
                star.setImageResource(i <= review.getRating() ? android.R.drawable.star_on : android.R.drawable.star_off);
                star.setColorFilter(i <= review.getRating() ? 0xFFF5B66E : 0xFF6B7C75);
                starsContainer.addView(star);
            }
        }
    }
}
