package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AreaInformation extends AppCompatActivity{

    private GoogleMap mMap;
    private LinearLayout surroundingInfo;
    private FirebaseFirestore db;

    private String roomId = "YOUR_ROOM_ID"; // Replace with the actual room ID passed via Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_information);

        surroundingInfo = findViewById(R.id.surroundingInfo);

        db = FirebaseFirestore.getInstance("homeify");

        // Fetch the roomId from the Intent
        roomId = getIntent().getStringExtra("roomId");
        if (roomId == null || roomId.isEmpty()) {
            Toast.makeText(this, "Room ID not provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch surrounding information from Firestore
        fetchSurroundingInfo();
        setupBackButton();
        setupMenuListeners();
    }

    private void fetchSurroundingInfo() {
        db.collection("rooms").document(roomId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                displaySurroundingInfo(documentSnapshot);
            } else {
                Toast.makeText(this, "No data found for this room.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void displaySurroundingInfo(DocumentSnapshot document) {
        Map<String, Object> surroundingInfoMap = (Map<String, Object>) document.get("surroundingInfo");
        if (surroundingInfoMap != null) {
            for (Map.Entry<String, Object> entry : surroundingInfoMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();

                // Dynamically add the surrounding info to the LinearLayout
                TextView infoTextView = new TextView(this);
                infoTextView.setText(String.format("%s: %s", capitalize(key), value));
                infoTextView.setTextSize(18);
                infoTextView.setPadding(0, 8, 0, 8);
                surroundingInfo.addView(infoTextView);
            }
        } else {
            Toast.makeText(this, "No surrounding information available.", Toast.LENGTH_SHORT).show();
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
