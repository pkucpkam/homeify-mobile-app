package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.favorite.FavoriteRoomsAdapter;
import com.app.homiefy.room.Room;
import com.app.homiefy.utils.SessionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRooms extends AppCompatActivity {

    private RecyclerView rvFavoriteRooms;
    private TextView tvEmptyList;
    private FirebaseFirestore db;
    private SessionManager sessionManager;
    private FavoriteRoomsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_rooms);

        // Initialize Firestore and Session Manager
        db = FirebaseFirestore.getInstance("homeify");
        sessionManager = new SessionManager(this);

        // Initialize UI components
        rvFavoriteRooms = findViewById(R.id.rvFavoriteRooms);
        tvEmptyList = findViewById(R.id.tvEmptyList);

        rvFavoriteRooms.setLayoutManager(new LinearLayoutManager(this));

        setupBackButton();
        setupMenuListeners();
        fetchFavoriteRooms();
    }

    private void fetchFavoriteRooms() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to view favorites!", Toast.LENGTH_SHORT).show();
            tvEmptyList.setVisibility(View.VISIBLE);
            rvFavoriteRooms.setVisibility(View.GONE);
            return;
        }

        db.collection("users").document(userId)
                .get(Source.CACHE)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("favorites")) {
                        List<String> favoriteRoomIds = (List<String>) documentSnapshot.get("favorites");

                        if (favoriteRoomIds != null && !favoriteRoomIds.isEmpty()) {
                            // Create adapter with room IDs
                            FavoriteRoomsAdapter adapter = new FavoriteRoomsAdapter(favoriteRoomIds, this);
                            rvFavoriteRooms.setAdapter(adapter);

                            tvEmptyList.setVisibility(View.GONE);
                            rvFavoriteRooms.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyList.setVisibility(View.VISIBLE);
                            rvFavoriteRooms.setVisibility(View.GONE);
                        }
                    } else {
                        tvEmptyList.setVisibility(View.VISIBLE);
                        rvFavoriteRooms.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvEmptyList.setVisibility(View.VISIBLE);
                    rvFavoriteRooms.setVisibility(View.GONE);
                });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRooms.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRooms.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRooms.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRooms.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRooms.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}