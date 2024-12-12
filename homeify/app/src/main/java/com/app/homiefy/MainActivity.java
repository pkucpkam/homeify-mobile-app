package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.homiefy.room.Room;
import com.app.homiefy.room.RoomAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup menu listeners
        setupMenuListeners();

        // Setup RecyclerView
        setupRecyclerView();

        // Fetch data from Firestore
        fetchRoomsFromFirestore();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvFeatured);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList);
        recyclerView.setAdapter(roomAdapter);
    }

    private void fetchRoomsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("rooms").get()
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
            roomList.clear();  // Ensure fresh data is shown, not accumulating old data
            for (DocumentSnapshot document : querySnapshot) {
                Room room = document.toObject(Room.class);
                if (room != null) {
                    roomList.add(room);
                }
            }
            roomAdapter.notifyDataSetChanged();  // Notify adapter of data change
        }
    }

    private void handleFirestoreError(Exception e) {
        Log.e("FirestoreError", "Error getting documents: ", e);
    }

    private void setupMenuListeners() {
        findViewById(R.id.btnGoToSearch).setOnClickListener(v -> navigateToSearch());

        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> navigateToOnlineSupport());

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> navigateToNotificationSettings());

        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> navigateToFavoriteRooms());

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToSearch() {
        startActivity(new Intent(MainActivity.this, SearchRoom.class));
    }

    private void navigateToOnlineSupport() {
        startActivity(new Intent(MainActivity.this, OnlineSupport.class));
    }

    private void navigateToNotificationSettings() {
        startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
    }

    private void navigateToFavoriteRooms() {
        startActivity(new Intent(MainActivity.this, FavoriteRooms.class));
    }

    private void navigateToProfile() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }
}
