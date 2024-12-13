package com.app.homiefy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.homiefy.room.Room;
import com.app.homiefy.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostingRoom extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;

    private TextInputEditText etRoomName, etRentPrice, etArea, etAddress, etRules, etStartDate, etEndDate, etContactInfo;
    private MaterialButton btnUploadImages, btnPostListing;

    private Uri imageUri;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_room);

        // Initialize Firebase Firestore and Storage
        storageRef = FirebaseStorage.getInstance().getReference("room_images");
        db = FirebaseFirestore.getInstance("homeify");

        // Initialize Session Manager
        sessionManager = new SessionManager(this);

        // Initialize UI elements
        etRoomName = findViewById(R.id.etRoomName);
        etRentPrice = findViewById(R.id.etRentPrice);
        etArea = findViewById(R.id.etArea);
        etAddress = findViewById(R.id.etAddress);
        etRules = findViewById(R.id.etRules);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etContactInfo = findViewById(R.id.etContactInfo);
        btnUploadImages = findViewById(R.id.btnUploadImages);
        btnPostListing = findViewById(R.id.btnPostListing);

        // Set click listeners
        btnUploadImages.setOnClickListener(v -> checkPermissionAndOpenChooser());
        btnPostListing.setOnClickListener(v -> postRoom());

        setupBackButton();
        setupMenuListeners();
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void checkPermissionAndOpenChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            openFileChooser();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void postRoom() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to post a room", Toast.LENGTH_SHORT).show();
            return;
        }

        String roomName = etRoomName.getText().toString().trim();
        String rentPrice = etRentPrice.getText().toString().trim();
        String area = etArea.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String rules = etRules.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String contactInfo = etContactInfo.getText().toString().trim();

        if (roomName.isEmpty() || rentPrice.isEmpty() || area.isEmpty() || address.isEmpty() || contactInfo.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageData = baos.toByteArray();

            String fileName = "room_" + System.currentTimeMillis() + ".jpg";
            StorageReference fileRef = storageRef.child(fileName);

            fileRef.putBytes(imageData).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                Map<String, Object> roomData = new HashMap<>();
                roomData.put("userId", userId);
                roomData.put("roomName", roomName);
                roomData.put("rentPrice", rentPrice);
                roomData.put("area", area);
                roomData.put("address", address);
                roomData.put("rules", rules);
                roomData.put("startDate", startDate);
                roomData.put("endDate", endDate);
                roomData.put("contactInfo", contactInfo);
                roomData.put("imageUrl", imageUrl);
                roomData.put("createdAt", System.currentTimeMillis());

                String roomId = UUID.randomUUID().toString();

                db.collection("rooms").document(roomId).set(roomData).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Room posted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(this, "Failed to post room: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })).addOnFailureListener(e -> Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(PostingRoom.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(PostingRoom.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(PostingRoom.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(PostingRoom.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(PostingRoom.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
