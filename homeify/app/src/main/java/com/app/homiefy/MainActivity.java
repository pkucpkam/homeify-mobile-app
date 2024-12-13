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

        // Setup UI and RecyclerView
        setupMenuListeners();
        setupRecyclerViews();

        sessionManager = new SessionManager(this);

        // Fetch data from Firestore
        fetchRoomsFromFirestore();
    }

    private void setupRecyclerViews() {
        rvFeatured = findViewById(R.id.rvFeatured);
        rvFavorites = findViewById(R.id.rvFavorites);

        rvFeatured.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        rvFavorites.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList);

        rvFeatured.setAdapter(roomAdapter);
        rvFavorites.setAdapter(roomAdapter);
    }

    private void fetchRoomsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance("homeify");

        // Lấy phòng nổi bật (ví dụ: 5 phòng mới nhất)
        db.collection("rooms")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleFeaturedRooms(task.getResult());
                    } else {
                        handleFirestoreError(task.getException());
                    }
                });

        // Lấy phòng yêu thích (có thể là phòng của người dùng hiện tại)
        db.collection("rooms")
                .whereEqualTo("userId", sessionManager.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleFavoritesRooms(task.getResult());
                    } else {
                        handleFirestoreError(task.getException());
                    }
                });
    }

    private void handleFeaturedRooms(QuerySnapshot querySnapshot) {
        if (querySnapshot != null) {
            // Làm mới danh sách phòng nổi bật
            List<Room> featuredRooms = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot) {
                Room room = document.toObject(Room.class);
                if (room != null) {
                    // Đảm bảo set ID của document
                    room.setId(document.getId());
                    featuredRooms.add(room);
                }
            }

            // Cập nhật adapter cho RecyclerView nổi bật
            RoomAdapter featuredAdapter = new RoomAdapter(featuredRooms);
            rvFeatured.setAdapter(featuredAdapter);
            featuredAdapter.notifyDataSetChanged();
        }
    }

    private void handleFirestoreError(Exception e) {
        Log.e("FirestoreError", "Error fetching rooms: ", e);
    }

    private void handleFavoritesRooms(QuerySnapshot querySnapshot) {
        if (querySnapshot != null) {
            // Làm mới danh sách phòng yêu thích
            List<Room> favoriteRooms = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot) {
                Room room = document.toObject(Room.class);
                if (room != null) {
                    // Đảm bảo set ID của document
                    room.setId(document.getId());
                    favoriteRooms.add(room);
                }
            }

            // Cập nhật adapter cho RecyclerView yêu thích
            RoomAdapter favoritesAdapter = new RoomAdapter(favoriteRooms);
            rvFavorites.setAdapter(favoritesAdapter);
            favoritesAdapter.notifyDataSetChanged();
        }
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
    }
}
