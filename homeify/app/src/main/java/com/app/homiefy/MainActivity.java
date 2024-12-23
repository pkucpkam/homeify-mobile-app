package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.homiefy.room.Room;
import com.app.homiefy.room.RoomAdapter;
import com.app.homiefy.utils.SessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFeatured, rvFavorites;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        setupMenuListeners();
        setupRecyclerViews();
        fetchRoomsFromFirestore();
    }

    private void setupRecyclerViews() {
        rvFeatured = findViewById(R.id.rvFeatured);

        rvFeatured.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList);

        rvFeatured.setAdapter(roomAdapter);
    }

    private void fetchRoomsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance("homeify");

        // Query to fetch the latest 3 featured rooms
        db.collection("rooms")
                .orderBy("createdAt", Query.Direction.DESCENDING) // Sort by newest
                .limit(3) // Limit to 3 rooms
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleFeaturedRooms(task.getResult());
                    } else {
                        handleFirestoreError(task.getException());
                    }
                });
    }


    private void handleFeaturedRooms(QuerySnapshot querySnapshot) {
        String currentUserId = sessionManager.getUserId();
        if (querySnapshot != null) {
            // Clear the current list before adding new rooms
            roomList.clear();

            // Iterate through Firestore documents and add to list
            for (DocumentSnapshot document : querySnapshot) {
                Room room = document.toObject(Room.class);
                if (room != null) {
                    room.setId(document.getId()); // Set Firestore document ID

                    Boolean isDeleted = document.getBoolean("deleted");
                    if (isDeleted != null && isDeleted) {
                        continue; // Skip this room if it's deleted
                    }

                    // Kiểm tra nếu phòng không phải do người dùng hiện tại thuê
                    if (!currentUserId.equals(room.getUserId())) {
                        roomList.add(room);
                    }
                }
            }

            // Notify the adapter to update the RecyclerView
            roomAdapter.notifyDataSetChanged();

            // Log if no rooms found
            if (roomList.isEmpty()) {
                Log.d("MainActivity", "No featured rooms found.");
            }
        }
    }


    private void handleFirestoreError(Exception e) {
        Log.e("FirestoreError", "Error fetching rooms: ", e);
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    ProfileActivity.class);
            startActivity(intent);
        });

        Button btnGoToSearch = findViewById(R.id.btnGoToSearch);
        btnGoToSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchRoom.class);
            startActivity(intent);
        });

        Button btnRegisterNoti = findViewById(R.id.btnRegisterNoti);
        btnRegisterNoti.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationCriteriaActivity.class);
            startActivity(intent);
        });
    }
}
