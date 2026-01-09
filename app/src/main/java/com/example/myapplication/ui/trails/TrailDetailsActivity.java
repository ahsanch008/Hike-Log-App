package com.example.myapplication.ui.trails;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.HikeRepository;
import com.example.myapplication.data.repository.TrailRepository;
import com.example.myapplication.data.repository.TrailReviewRepository;
import com.example.myapplication.data.repository.PackingRepository;
import com.example.myapplication.model.Trail;
import com.example.myapplication.model.TrailReview;
import com.example.myapplication.model.PackingItem;

import java.util.List;

public class TrailDetailsActivity extends AppCompatActivity {
    private Trail trail;
    private long selectedDateMillis;
    private TrailReviewRepository reviewRepo;
    private ReviewAdapter reviewAdapter;
    private TrailReview userReview;
    private int currentRating = 0;
    private long userId;
    private PackingRepository packingRepo;
    private PackingAdapter packingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_details);
        long trailId = getIntent().getLongExtra("trail_id", -1);
        TrailRepository repo = new TrailRepository(this);
        for (Trail t : repo.getAll()) {
            if (t.getId() == trailId) { trail = t; break; }
        }
        if (trail == null) { finish(); return; }
        android.widget.ImageView hero = findViewById(R.id.detail_image);
        TextView name = findViewById(R.id.detail_name);
        TextView meta = findViewById(R.id.detail_meta);
        TextView desc = findViewById(R.id.detail_desc);
        TextView diffPill = findViewById(R.id.detail_difficulty);
        TextView statDist = findViewById(R.id.stat_distance);
        TextView statTime = findViewById(R.id.stat_time);
        TextView statElev = findViewById(R.id.stat_elev);
        ImageButton back = findViewById(R.id.btn_back);
        name.setText(trail.getName());
        meta.setText(trail.getLocation() + " • " + trail.getDifficulty() + " • " + trail.getDistanceKm() + " km • " + trail.getEstimatedTimeMin() + " min • " + trail.getElevationM() + " m");
        desc.setText(trail.getDescription());
        if (hero != null) {
            int resId = 0;
            String img = trail.getImageName();
            if (img != null && !img.isEmpty()) {
                resId = getResources().getIdentifier(img, "drawable", getPackageName());
            }
            if (resId != 0) hero.setImageResource(resId); else hero.setImageResource(R.drawable.ic_logo_mountain);
        }
        if (diffPill != null) {
            diffPill.setText(trail.getDifficulty());
            if ("Easy".equals(trail.getDifficulty())) diffPill.setBackgroundResource(R.drawable.bg_pill_easy); else if ("Medium".equals(trail.getDifficulty())) diffPill.setBackgroundResource(R.drawable.bg_pill_medium); else diffPill.setBackgroundResource(R.drawable.bg_pill_hard);
        }
        if (statDist != null) statDist.setText(String.format("%.1f km", trail.getDistanceKm()));
        int h = trail.getEstimatedTimeMin() / 60; int m = trail.getEstimatedTimeMin() % 60;
        if (statTime != null) statTime.setText(String.format("%dh %dm", h, m));
        if (statElev != null) statElev.setText(trail.getElevationM() + "m");
        if (back != null) back.setOnClickListener(v -> onBackPressed());
        View form = findViewById(R.id.log_form_container);
        Button logBtn = findViewById(R.id.btn_log_completed);
        Button saveBtn = findViewById(R.id.btn_save_log);
        Button cancelBtn = findViewById(R.id.btn_cancel_log);
        TextView dateValue = findViewById(R.id.log_date_value);
        ImageButton pickDate = findViewById(R.id.btn_pick_date_detail);
        EditText inputDur = findViewById(R.id.input_duration_detail);
        EditText inputNotes = findViewById(R.id.input_notes_detail);

        selectedDateMillis = System.currentTimeMillis();
        java.text.DateFormat df = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM);
        if (dateValue != null) dateValue.setText(df.format(new java.util.Date(selectedDateMillis)));
        if (inputDur != null) inputDur.setText(String.valueOf(trail.getEstimatedTimeMin()));

        logBtn.setOnClickListener(v -> {
            if (form != null) form.setVisibility(View.VISIBLE);
        });

        if (pickDate != null) pickDate.setOnClickListener(v -> {
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTimeInMillis(selectedDateMillis);
            DatePickerDialog dlg = new DatePickerDialog(this, (view1, y, mo, d) -> {
                java.util.Calendar cc = java.util.Calendar.getInstance();
                cc.set(y, mo, d, 0, 0, 0);
                cc.set(java.util.Calendar.MILLISECOND, 0);
                selectedDateMillis = cc.getTimeInMillis();
                if (dateValue != null) dateValue.setText(df.format(new java.util.Date(selectedDateMillis)));
            }, c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH), c.get(java.util.Calendar.DAY_OF_MONTH));
            dlg.show();
        });

        reviewRepo = new TrailReviewRepository(this);
        packingRepo = new PackingRepository(this);
        setupReviews();
        setupPackingList();
        loadReviews();
        loadPackingList();

        if (saveBtn != null) saveBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("hikelog", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1);
            if (userId <= 0) { Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show(); return; }
            String durStr = inputDur != null ? inputDur.getText().toString().trim() : "";
            if (durStr.isEmpty()) { Toast.makeText(this, "Enter duration", Toast.LENGTH_SHORT).show(); return; }
            int dur = Integer.parseInt(durStr);
            String notes = inputNotes != null ? inputNotes.getText().toString() : "";
            HikeRepository hikeRepo = new HikeRepository(this);
            long id = hikeRepo.logHike(userId, trail.getId(), trail.getName(), trail.getDifficulty(), selectedDateMillis, dur, notes);
            if (id > 0) { Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show(); finish(); } else { Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show(); }
        });

        if (cancelBtn != null) cancelBtn.setOnClickListener(v -> {
            if (form != null) form.setVisibility(View.GONE);
        });

        com.google.android.material.bottomnavigation.BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav != null) {
            nav.setOnItemSelectedListener(item -> {
                android.content.Intent i = new android.content.Intent(this, com.example.myapplication.MainActivity.class);
                i.putExtra("selected_tab_id", item.getItemId());
                startActivity(i);
                finish();
                return true;
            });
        }
    }

    private void setupReviews() {
        SharedPreferences prefs = getSharedPreferences("hikelog", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        RecyclerView reviewsList = findViewById(R.id.reviews_list);
        if (reviewsList != null) {
            reviewAdapter = new ReviewAdapter(review -> {
                int deleted = reviewRepo.deleteReview(review.getId());
                if (deleted > 0) {
                    Toast.makeText(this, "Review deleted", Toast.LENGTH_SHORT).show();
                    loadReviews();
                } else {
                    Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            });
            reviewsList.setAdapter(reviewAdapter);
        }

        Button writeReviewBtn = findViewById(R.id.btn_write_review);
        if (writeReviewBtn != null) {
            writeReviewBtn.setOnClickListener(v -> showReviewDialog());
        }
    }

    private void setupPackingList() {
        RecyclerView packingList = findViewById(R.id.packing_list);
        if (packingList != null) {
            packingAdapter = new PackingAdapter();
            packingList.setAdapter(packingAdapter);
        }
    }

    private void loadReviews() {
        if (trail == null) return;
        List<TrailReview> reviews = reviewRepo.getReviewsForTrail(trail.getId());
        reviewAdapter.submitList(reviews);

        TextView summary = findViewById(R.id.review_summary);
        TextView emptyState = findViewById(R.id.review_empty_state);
        Button writeBtn = findViewById(R.id.btn_write_review);
        double avg = reviewRepo.getAverageRating(trail.getId());
        int count = reviews.size();

        if (summary != null) {
            summary.setText(count + " review" + (count != 1 ? "s" : "") + " • " + String.format("%.1f", avg));
        }

        if (emptyState != null) {
            emptyState.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        }

        if (writeBtn != null) {
            userReview = reviewRepo.getReviewByUserAndTrail(userId, trail.getId());
            writeBtn.setText(userReview != null ? "Edit Your Review" : "Write a Review");
        }
    }

    private void loadPackingList() {
        if (trail == null) return;
        List<PackingItem> items = packingRepo.getItemsForTrail(trail.getId());
        packingAdapter.submitList(items);
    }

    private void showReviewDialog() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_review, null);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        LinearLayout starsContainer = dialogView.findViewById(R.id.rating_stars);
        ImageView[] stars = new ImageView[5];
        for (int i = 0; i < 5; i++) {
            stars[i] = (ImageView) starsContainer.getChildAt(i);
            final int starIndex = i + 1;
            stars[i].setOnClickListener(v -> {
                currentRating = starIndex;
                updateStarColors(stars, currentRating);
            });
        }

        EditText comment = dialogView.findViewById(R.id.review_comment);
        if (userReview != null) {
            currentRating = userReview.getRating();
            updateStarColors(stars, currentRating);
            comment.setText(userReview.getComment());
        }

        Button cancel = dialogView.findViewById(R.id.btn_cancel_review);
        Button submit = dialogView.findViewById(R.id.btn_submit_review);

        cancel.setOnClickListener(v -> dialog.dismiss());

        submit.setOnClickListener(v -> {
            if (currentRating == 0) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            String commentText = comment.getText().toString().trim();
            if (userReview != null) {
                reviewRepo.updateReview(userReview.getId(), currentRating, commentText);
            } else {
                reviewRepo.addReview(trail.getId(), userId, currentRating, commentText);
            }
            Toast.makeText(this, "Review saved", Toast.LENGTH_SHORT).show();
            loadReviews();
            dialog.dismiss();
        });
    }

    private void updateStarColors(ImageView[] stars, int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setColorFilter(0xFFF5B66E);
            } else {
                stars[i].setColorFilter(0xFF6B7C75);
            }
        }
    }
}
