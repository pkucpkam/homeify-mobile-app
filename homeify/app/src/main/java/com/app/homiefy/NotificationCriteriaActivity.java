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

import com.app.homiefy.notification_criteria.NotificationCriteria;
import com.app.homiefy.room.Room;
import com.app.homiefy.room.RoomAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationCriteriaActivity extends AppCompatActivity {
    private EditText etLocation, etMinPrice, etMaxPrice, etMinArea, etMaxArea, etOtherRequirements;
    private CheckBox cbWifi, cbAC, cbWashingMachine, cbFridge, cbParking;
    private Button btnSearch;
    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;  // FirebaseAuth để lấy userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance("homeify");
        mAuth = FirebaseAuth.getInstance(); // Firebase Auth

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
        // Lấy các tiêu chí tìm kiếm từ UI
        String location = etLocation.getText().toString().trim().toLowerCase();
        String minPriceText = etMinPrice.getText().toString().trim();
        String maxPriceText = etMaxPrice.getText().toString().trim();
        String minAreaText = etMinArea.getText().toString().trim();
        String maxAreaText = etMaxArea.getText().toString().trim();
        String otherRequirements = etOtherRequirements.getText().toString().trim().toLowerCase();

        // Chuyển đổi giá trị min/max thành các kiểu dữ liệu số
        Double minPrice = !TextUtils.isEmpty(minPriceText) ? Double.parseDouble(minPriceText) : null;
        Double maxPrice = !TextUtils.isEmpty(maxPriceText) ? Double.parseDouble(maxPriceText) : null;
        Double minArea = !TextUtils.isEmpty(minAreaText) ? Double.parseDouble(minAreaText) : null;
        Double maxArea = !TextUtils.isEmpty(maxAreaText) ? Double.parseDouble(maxAreaText) : null;

        // Lấy các tiện ích đã chọn
        List<String> selectedAmenities = new ArrayList<>();
        if (cbWifi.isChecked()) selectedAmenities.add("Wi-Fi");
        if (cbAC.isChecked()) selectedAmenities.add("Air Conditioner");
        if (cbWashingMachine.isChecked()) selectedAmenities.add("Washing Machine");
        if (cbFridge.isChecked()) selectedAmenities.add("Fridge");
        if (cbParking.isChecked()) selectedAmenities.add("Parking");

        // Lấy userId từ Firebase Authentication
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "You must be logged in to set up notifications.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một đối tượng chứa các tiêu chí tìm kiếm
        NotificationCriteria criteria = new NotificationCriteria(
                userId, location, minPrice, maxPrice, minArea, maxArea, selectedAmenities, otherRequirements
        );

        // Lưu vào Firestore trong collection "notification_criteria"
        db.collection("notification_criteria")
                .add(criteria)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NotificationCriteriaActivity.this, "Search criteria saved successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotificationCriteriaActivity.this, "Failed to save criteria: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
