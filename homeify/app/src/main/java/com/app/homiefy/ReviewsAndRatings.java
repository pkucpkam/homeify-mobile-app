package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.review.Review;
import com.app.homiefy.review.ReviewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAndRatings extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reviews_and_ratings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupMenuListeners();
        setupRecyclerView();

        // Fetch reviews from Firestore
        fetchReviewsFromFirestore();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvFeatured);  // Assuming your RecyclerView has this ID
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(reviewAdapter);
    }

    private void fetchReviewsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews") // Assuming the collection is named "reviews"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleFirestoreData(task.getResult());
                    } else {
                        handleFirestoreError(task.getException());
                    }
                });
    }

    private void handleFirestoreData(QuerySnapshot querySnapshot) {
        if (querySnapshot != null) {
            reviewList.clear(); // Clear old reviews if any
            for (QueryDocumentSnapshot document : querySnapshot) {
                Review review = document.toObject(Review.class);
                if (review != null) {
                    reviewList.add(review);
                }
            }
            reviewAdapter.notifyDataSetChanged(); // Notify the adapter that data has changed
        }
    }

    private void handleFirestoreError(Exception e) {
        Toast.makeText(this, "Error fetching reviews: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> navigateToOnlineSupport());

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> navigateToNotificationSettings());

        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> navigateToFavoriteRooms());

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToOnlineSupport() {
        // Add your navigation logic here
    }

    private void navigateToNotificationSettings() {
        // Add your navigation logic here
    }

    private void navigateToFavoriteRooms() {
        // Add your navigation logic here
    }

    private void navigateToProfile() {
        // Add your navigation logic here
    }
}