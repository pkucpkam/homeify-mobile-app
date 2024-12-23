package com.app.homiefy;

import android.Manifest;
import android.app.ProgressDialog;
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

import com.app.homiefy.notification.Notification;
import com.app.homiefy.notification_criteria.NotificationCriteria;
import com.app.homiefy.utils.DateValidator;
import com.app.homiefy.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostingRoom extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;
    private ProgressDialog progressDialog;

    private TextInputEditText etRoomName, etRentPrice, etArea, etAddress,
            etRules, etStartDate, etEndDate, etContactInfo,
            etSupermarket, etHospital, etTransport, etEducation;;
    private TextInputEditText etDeposit, etOtherFees, etDescription;
    private MaterialButton btnUploadImages, btnPostListing;
    private ChipGroup chipGroupAmenities;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting room...");
        progressDialog.setCancelable(false);

        // Initialize UI elements
        etRoomName = findViewById(R.id.etRoomName);
        etRentPrice = findViewById(R.id.etRentPrice);
        etArea = findViewById(R.id.etArea);
        etAddress = findViewById(R.id.etAddress);
        etRules = findViewById(R.id.etRules);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etContactInfo = findViewById(R.id.etContactInfo);
        etDeposit = findViewById(R.id.etDeposit);
        etOtherFees = findViewById(R.id.etOtherFees);
        etDescription = findViewById(R.id.etDescription);
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);
        btnUploadImages = findViewById(R.id.btnUploadImages);
        btnPostListing = findViewById(R.id.btnPostListing);
        etSupermarket = findViewById(R.id.etSupermarket);
        etHospital = findViewById(R.id.etHospital);
        etTransport = findViewById(R.id.etTransport);
        etEducation = findViewById(R.id.etEducation);

        // Set click listeners
        btnUploadImages.setOnClickListener(v -> checkPermissionAndOpenChooser());
        btnPostListing.setOnClickListener(v -> postRoom());

        setupBackButton();
        setupMenuListeners();
        setupDateInputs();
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageView ivRoomImage = findViewById(R.id.ivRoomImage);
            ivRoomImage.setImageURI(imageUri);
            Toast.makeText(this, "Image selected successfully",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private List<String> getSelectedAmenities() {
        List<String> selectedAmenities = new ArrayList<>();
        for (int i = 0; i < chipGroupAmenities.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupAmenities.getChildAt(i);
            if (chip.isChecked()) {
                selectedAmenities.add(chip.getText().toString());
            }
        }
        return selectedAmenities;
    }

    private void postRoom() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to post a room",
                    Toast.LENGTH_SHORT).show();
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
        String deposit = etDeposit.getText().toString().trim();
        String otherFees = etOtherFees.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Extract surrounding information
        String supermarket = etSupermarket.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String transport = etTransport.getText().toString().trim();
        String education = etEducation.getText().toString().trim();

        DateValidator.ValidationResult dateValidation = DateValidator.validateDates(startDate, endDate);
        if (!dateValidation.isValid) {
            Toast.makeText(this, dateValidation.errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> amenities = getSelectedAmenities();

        if (roomName.isEmpty() || rentPrice.isEmpty() || area.isEmpty() || address.isEmpty() ||
                contactInfo.isEmpty() || deposit.isEmpty() || description.isEmpty() ||
                imageUri == null || amenities.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and upload an image",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageData = baos.toByteArray();

            String fileName = "room_" + System.currentTimeMillis() + ".jpg";
            StorageReference fileRef = storageRef.child(fileName);

            fileRef.putBytes(imageData).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
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
                roomData.put("deposit", deposit);
                roomData.put("otherFees", otherFees);
                roomData.put("description", description);
                roomData.put("amenities", amenities);
                roomData.put("imageUrl", imageUrl);
                roomData.put("createdAt", System.currentTimeMillis());

                // Add surrounding information
                Map<String, Object> surroundingInfo = new HashMap<>();
                surroundingInfo.put("supermarket", supermarket);
                surroundingInfo.put("hospital", hospital);
                surroundingInfo.put("transport", transport);
                surroundingInfo.put("education", education);

                roomData.put("surroundingInfo", surroundingInfo);
                roomData.put("rented", false);
                roomData.put("deleted", false);

                String roomId = UUID.randomUUID().toString();

                db.collection("rooms").document(roomId).set(roomData)
                        .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Room posted successfully!",
                            Toast.LENGTH_SHORT).show();
                            checkNotificationCriteria(roomData);
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to post room: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Image upload failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to process image: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDateInputs() {
        TextInputEditText[] dateInputs = {etStartDate, etEndDate};

        for (TextInputEditText dateInput : dateInputs) {
            dateInput.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    Toast.makeText(this, "Please enter date in format dd/MM/yyyy",
                            Toast.LENGTH_SHORT).show();
                }
            });
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

    private void checkNotificationCriteria(Map<String, Object> roomData) {
        // Lấy các tiêu chí tìm kiếm đã lưu trong Firestore
        db.collection("notification_criteria").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NotificationCriteria criteria = document.toObject(NotificationCriteria.class);

                            // So sánh các tiêu chí tìm kiếm với thông tin phòng
                            if (criteria != null && isRoomMatchCriteria(roomData, criteria)) {
                                // Get the userId from the notification criteria (if saved in Firestore as part of the criteria)
                                String receiverId = criteria.getUserId();  // Assuming criteria contains userId of the person who set the notification criteria

                                // Send notification to the user who set the criteria
                                sendNotificationToUser(receiverId, criteria);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check notification criteria: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotificationToUser(String receiverId, NotificationCriteria criteria) {
        // Create the notification object
        String notificationId = UUID.randomUUID().toString();  // Generate a unique ID for the notification
        String title = "Room matches your criteria!";
        String content = "A new room listing matches your search criteria: " + criteria.getLocation();
        String timestamp = String.valueOf(System.currentTimeMillis());
        boolean isRead = false;  // Initially, the notification is unread

        // Create the Notification object
        Notification notification = new Notification(notificationId, title, content, timestamp, isRead, receiverId);

        // Save the notification to Firestore
        db.collection("notifications").document(notificationId).set(notification)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private boolean isRoomMatchCriteria(Map<String, Object> roomData, NotificationCriteria criteria) {
        // Ban đầu giả định không có sự phù hợp
        boolean matches = false;

        // Kiểm tra vị trí (Location)
        String roomAddress = (String) roomData.get("address");
        if (roomAddress != null && roomAddress.toLowerCase().contains(criteria.getLocation().toLowerCase())) {
            matches = true;  // Nếu vị trí thỏa mãn, coi như phù hợp
        }

        // Kiểm tra giá thuê (Rent Price)
        Double roomPrice = Double.parseDouble((String) roomData.get("rentPrice"));
        if (criteria.getMinPrice() != null && roomPrice >= criteria.getMinPrice()) {
            matches = true;  // Nếu giá thuê lớn hơn hoặc bằng mức giá tối thiểu, coi như phù hợp
        }
        if (criteria.getMaxPrice() != null && roomPrice <= criteria.getMaxPrice()) {
            matches = true;  // Nếu giá thuê nhỏ hơn hoặc bằng mức giá tối đa, coi như phù hợp
        }

        // Kiểm tra diện tích (Area)
        Double roomArea = Double.parseDouble((String) roomData.get("area"));
        if (criteria.getMinArea() != null && roomArea >= criteria.getMinArea()) {
            matches = true;  // Nếu diện tích lớn hơn hoặc bằng diện tích tối thiểu, coi như phù hợp
        }
        if (criteria.getMaxArea() != null && roomArea <= criteria.getMaxArea()) {
            matches = true;  // Nếu diện tích nhỏ hơn hoặc bằng diện tích tối đa, coi như phù hợp
        }

        // Kiểm tra tiện ích (Amenities)
        List<String> roomAmenities = (List<String>) roomData.get("amenities");
        if (roomAmenities != null && !roomAmenities.isEmpty() && roomAmenities.containsAll(criteria.getAmenities())) {
            matches = true;  // Nếu tiện ích phòng phù hợp, coi như phòng thỏa mãn
        }

        // Kiểm tra mô tả (Description)
        String roomDescription = (String) roomData.get("description");
        if (roomDescription != null && roomDescription.toLowerCase().contains(criteria.getOtherRequirements().toLowerCase())) {
            matches = true;  // Nếu mô tả phòng thỏa mãn, coi như phòng phù hợp
        }

        return matches;  // Trả về true nếu có điều kiện nào thỏa mãn
    }


}
