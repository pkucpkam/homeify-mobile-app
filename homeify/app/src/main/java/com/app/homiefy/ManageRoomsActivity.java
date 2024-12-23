package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.homiefy.room.ManageRoomAdapter;
import com.app.homiefy.room.Room;
import com.app.homiefy.utils.SessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ManageRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRooms;
    private ManageRoomAdapter manageRoomAdapter;
    private List<Room> roomList;
    private TextView tvEmptyList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int EDIT_ROOM_REQUEST = 100;


    private FirebaseFirestore db; // Firestore instance
    private SessionManager sessionManager; // SessionManager for user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        recyclerViewRooms = findViewById(R.id.rvViewRooms);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchRoomsFromFirestore();
        });


        // Setup RecyclerView
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));
        roomList = new ArrayList<>();
        manageRoomAdapter = new ManageRoomAdapter(roomList);
        recyclerViewRooms.setAdapter(manageRoomAdapter);

        // Initialize Firebase Firestore and SessionManager
        db = FirebaseFirestore.getInstance("homeify");
        sessionManager = new SessionManager(this);

        // Fetch rooms for the current user
        fetchRoomsFromFirestore();

        // Setup buttons
        setupBackButton();
        setupAddRoomButton();
    }

    private void fetchRoomsFromFirestore() {
        String userId = sessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID is not available!", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        db.collection("rooms")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        handleRoomFetchSuccess(task.getResult());
                    } else {
                        handleFirestoreError(task.getException());
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ROOM_REQUEST && resultCode == RESULT_OK) {
            fetchRoomsFromFirestore();
        }
    }


    private void handleRoomFetchSuccess(QuerySnapshot querySnapshot) {
        if (querySnapshot != null) {
            roomList.clear(); // Clear the current list

            for (DocumentSnapshot document : querySnapshot) {
                Room room = document.toObject(Room.class);
                if (room != null) {
                    room.setId(document.getId()); // Set Firestore document ID
                    roomList.add(room);
                }
            }

            manageRoomAdapter.notifyDataSetChanged();
            updateEmptyListMessage();

            if (roomList.isEmpty()) {
                Log.d("ManageRoomsActivity", "No rooms found for the current user.");
            }
        }
    }

    private void handleFirestoreError(Exception e) {
        Log.e("FirestoreError", "Error fetching rooms: ", e);
        Toast.makeText(this, "Failed to fetch rooms. Please try again later.", Toast.LENGTH_SHORT).show();
    }

    private void updateEmptyListMessage() {
        if (roomList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            recyclerViewRooms.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            recyclerViewRooms.setVisibility(View.VISIBLE);
        }
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupAddRoomButton() {
        ImageButton btnAddRoom = findViewById(R.id.btnAddRoom);
        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ManageRoomsActivity.this, PostingRoom.class);
            startActivity(intent);
        });
    }
}
