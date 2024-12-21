package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.room.Room;
import com.app.homiefy.room.RoomAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchRoom extends AppCompatActivity {
    private EditText etLocation, etMinPrice, etMaxPrice, etMinArea, etMaxArea, etOtherRequirements;
    private CheckBox cbWifi, cbAC, cbWashingMachine, cbFridge, cbParking;
    private Button btnSearch;
    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance("homeify");

        // Initialize UI components
        initializeComponents();

        // Setup listeners
        setupMenuListeners();
        setupBackButton();
        setupSearchButton();
    }

    private void initializeComponents() {
        // Search input fields
        etLocation = findViewById(R.id.etLocation);
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);
        etMinArea = findViewById(R.id.etMinArea);
        etMaxArea = findViewById(R.id.etMaxArea);
        etOtherRequirements = findViewById(R.id.etOtherRequirements);

        // Amenities checkboxes
        cbWifi = findViewById(R.id.cbWifi);
        cbAC = findViewById(R.id.cbAC);
        cbWashingMachine = findViewById(R.id.cbWashingMachine);
        cbFridge = findViewById(R.id.cbFridge);
        cbParking = findViewById(R.id.cbParking);

        // Search button
        btnSearch = findViewById(R.id.btnSearch);

        // RecyclerView for search results
        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));

        // Initialize room list and adapter
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList);
        recyclerViewRooms.setAdapter(roomAdapter);
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        // Clear previous search results
        roomList.clear();

        // Get all search criteria
        String location = etLocation.getText().toString().trim().toLowerCase();
        String minPriceText = etMinPrice.getText().toString().trim();
        String maxPriceText = etMaxPrice.getText().toString().trim();
        String minAreaText = etMinArea.getText().toString().trim();
        String maxAreaText = etMaxArea.getText().toString().trim();
        String otherRequirements = etOtherRequirements.getText().toString().trim().toLowerCase();

        // Convert price ranges
        Double minPrice = !TextUtils.isEmpty(minPriceText) ? Double.parseDouble(minPriceText) : null;
        Double maxPrice = !TextUtils.isEmpty(maxPriceText) ? Double.parseDouble(maxPriceText) : null;
        Double minArea = !TextUtils.isEmpty(minAreaText) ? Double.parseDouble(minAreaText) : null;
        Double maxArea = !TextUtils.isEmpty(maxAreaText) ? Double.parseDouble(maxAreaText) : null;

        // Get selected amenities
        List<String> selectedAmenities = new ArrayList<>();
        if (cbWifi.isChecked()) selectedAmenities.add("Wi-Fi");
        if (cbAC.isChecked()) selectedAmenities.add("Air Conditioner");
        if (cbWashingMachine.isChecked()) selectedAmenities.add("Washing Machine");
        if (cbFridge.isChecked()) selectedAmenities.add("Fridge");
        if (cbParking.isChecked()) selectedAmenities.add("Parking");

        // Fetch rooms from Firestore
        db.collection("rooms").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Room room = document.toObject(Room.class);
                            boolean matchesCriteria = true;

                            // Add Firestore document ID to room
                            if (room != null) {
                                room.setId(document.getId());
                            } else {
                                continue;
                            }

                            // Location filter
                            if (!TextUtils.isEmpty(location)) {
                                if (room.getAddress() == null || !room.getAddress().toLowerCase().contains(location)) {
                                    matchesCriteria = false;
                                }
                            }

                            // Price range filter
                            if (minPrice != null) {
                                try {
                                    double roomPrice = Double.parseDouble(room.getRentPrice());
                                    if (roomPrice < minPrice) {
                                        matchesCriteria = false;
                                    }
                                } catch (NumberFormatException e) {
                                    matchesCriteria = false;
                                }
                            }
                            if (maxPrice != null) {
                                try {
                                    double roomPrice = Double.parseDouble(room.getRentPrice());
                                    if (roomPrice > maxPrice) {
                                        matchesCriteria = false;
                                    }
                                } catch (NumberFormatException e) {
                                    matchesCriteria = false;
                                }
                            }

                            // Area range filter
                            if (minArea != null) {
                                try {
                                    double roomArea = Double.parseDouble(room.getArea());
                                    if (roomArea < minArea) {
                                        matchesCriteria = false;
                                    }
                                } catch (NumberFormatException e) {
                                    matchesCriteria = false;
                                }
                            }
                            if (maxArea != null) {
                                try {
                                    double roomArea = Double.parseDouble(room.getArea());
                                    if (roomArea > maxArea) {
                                        matchesCriteria = false;
                                    }
                                } catch (NumberFormatException e) {
                                    matchesCriteria = false;
                                }
                            }

                            // Amenities filter
                            if (!selectedAmenities.isEmpty()) {
                                List<String> roomAmenities = room.getAmenities();
                                if (roomAmenities == null || !roomAmenities.containsAll(selectedAmenities)) {
                                    matchesCriteria = false;
                                }
                            }

                            // Other requirements filter
                            if (!TextUtils.isEmpty(otherRequirements)) {
                                if (room.getDescription() == null || !room.getDescription().toLowerCase().contains(otherRequirements)) {
                                    matchesCriteria = false;
                                }
                            }

                            // Add room if it matches all criteria
                            if (matchesCriteria) {
                                roomList.add(room);
                            }
                        }

                        // Update UI
                        roomAdapter.notifyDataSetChanged();

                        // Show appropriate message
                        if (roomList.isEmpty()) {
                            Toast.makeText(SearchRoom.this, "No rooms found matching your criteria.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SearchRoom.this, roomList.size() + " rooms found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SearchRoom.this, "Search failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> startActivity(new Intent(this, ChatListActivity.class)));

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> startActivity(new Intent(this, PostingRoom.class)));

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}
