package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewsAndRatings extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText edtReview;
    private Button btnSubmitReview;

    private FirebaseFirestore db;
    private String roomId; // Assuming you pass the room ID to identify reviews for a specific room

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance("homeify");
        roomId = getIntent().getStringExtra("roomId"); // Get the room ID from intent

        if (roomId == null || roomId.isEmpty()) {
            Toast.makeText(this, "Room ID not provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        ratingBar = findViewById(R.id.ratingBar);
        edtReview = findViewById(R.id.edtReview);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);

        btnSubmitReview.setOnClickListener(v -> submitReview());

        setupMenuListeners();
        setupBackButton();

    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void handleFirestoreError(Exception e) {
        Toast.makeText(this, "Error fetching reviews: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String reviewText = edtReview.getText().toString().trim();

        if (rating < 1 || reviewText.isEmpty()) {
            Toast.makeText(this, "Please provide a valid rating and review.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user ID (assuming you have FirebaseAuth or SessionManager)
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest"; // Fallback if no user is logged in

        // Prepare review data
        Map<String, Object> review = new HashMap<>();
        review.put("roomId", roomId);
        review.put("rating", rating);
        review.put("review", reviewText);
        review.put("createdAt", System.currentTimeMillis());
        review.put("userId", userId); // Add the user ID of the reviewer

        // Save review to Firestore
        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    ratingBar.setRating(0);
                    edtReview.setText("");
                    Intent intent = new Intent(ReviewsAndRatings.this, RoomDetails.class);
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewsAndRatings.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewsAndRatings.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewsAndRatings.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewsAndRatings.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewsAndRatings.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
