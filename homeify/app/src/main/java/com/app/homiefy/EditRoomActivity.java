package com.app.homiefy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.homiefy.utils.DateValidator;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditRoomActivity extends AppCompatActivity {

    private TextInputEditText etRoomName, etRentPrice, etArea, etAddress, etDeposit, etOtherFees, etDescription, etRules, etStartDate, etEndDate, etContactInfo, etSupermarket, etHospital, etTransport, etEducation;
    private ChipGroup chipGroupAmenities;
    private MaterialButton btnSaveChanges, btnUploadNewImage;
    private ImageView ivRoomImage;
    private ProgressDialog progressDialog;

    private String roomId;
    private Uri newImageUri;
    private String currentImageUrl;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating room information...");
        progressDialog.setCancelable(false);

        // Thêm vào đầu onCreate
        db = FirebaseFirestore.getInstance("homeify");
        storageRef = FirebaseStorage.getInstance().getReference("room_images");

        // Bind views
        etRoomName = findViewById(R.id.etRoomName);
        etRentPrice = findViewById(R.id.etRentPrice);
        etArea = findViewById(R.id.etArea);
        etAddress = findViewById(R.id.etAddress);
        etDeposit = findViewById(R.id.etDeposit);
        etOtherFees = findViewById(R.id.etOtherFees);
        etDescription = findViewById(R.id.etDescription);
        etRules = findViewById(R.id.etRules);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etContactInfo = findViewById(R.id.etContactInfo);
        etSupermarket = findViewById(R.id.etSupermarket);
        etHospital = findViewById(R.id.etHospital);
        etTransport = findViewById(R.id.etTransport);
        etEducation = findViewById(R.id.etEducation);
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);
        btnSaveChanges = findViewById(R.id.btnPostListing);
        btnUploadNewImage = findViewById(R.id.btnUploadImages);
        ivRoomImage = findViewById(R.id.ivRoomImage);

        // Get data from Intent
        currentImageUrl = getIntent().getStringExtra("imageUrl");
        roomId = getIntent().getStringExtra("roomId");

        // Set data
        etRoomName.setText(getIntent().getStringExtra("roomName"));
        etRentPrice.setText(getIntent().getStringExtra("rentPrice"));
        etArea.setText(getIntent().getStringExtra("area"));
        etAddress.setText(getIntent().getStringExtra("address"));
        etDeposit.setText(getIntent().getStringExtra("deposit"));
        etOtherFees.setText(getIntent().getStringExtra("otherFees"));
        etDescription.setText(getIntent().getStringExtra("description"));
        etRules.setText(getIntent().getStringExtra("rules"));
        etStartDate.setText(getIntent().getStringExtra("startDate"));
        etEndDate.setText(getIntent().getStringExtra("endDate"));
        etContactInfo.setText(getIntent().getStringExtra("contactInfo"));
        etSupermarket.setText(getIntent().getStringExtra("supermarket"));
        etHospital.setText(getIntent().getStringExtra("hospital"));
        etTransport.setText(getIntent().getStringExtra("transport"));
        etEducation.setText(getIntent().getStringExtra("education"));

        // Load image
        // Sửa phần load image trong EditRoomActivity
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            try {
                Glide.with(this)
                        .load(currentImageUrl)
                        .placeholder(R.drawable.placeholder_image) // Thêm placeholder
                        .error(R.drawable.placeholder_image) // Thêm error image
                        .into(ivRoomImage);
            } catch (Exception e) {
                ivRoomImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            ivRoomImage.setImageResource(R.drawable.placeholder_image);
        }

        // Load amenities
        String[] amenities = getIntent().getStringArrayExtra("amenities");
        chipGroupAmenities.removeAllViews(); // Xóa các Chip cũ trước khi thêm mới
        if (amenities != null) {
            for (String amenity : amenities) {
                Chip chip = new Chip(this);
                chip.setText(amenity);
                chip.setCheckable(true);
                chip.setChecked(true);
                chipGroupAmenities.addView(chip);
            }
        }

        // Handle save changes
        btnSaveChanges.setText("Save Changes");
        btnSaveChanges.setOnClickListener(v -> saveRoomChanges());

        // Handle image upload
        btnUploadNewImage.setOnClickListener(v -> openFileChooser());

        setupBackButton();
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            ivRoomImage.setImageURI(newImageUri);
        }
    }

    private void saveRoomChanges() {
        // Validate all fields
        if (etRoomName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Room name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etRentPrice.getText().toString().isEmpty()) {
            Toast.makeText(this, "Rent price is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etArea.getText().toString().isEmpty()) {
            Toast.makeText(this, "Area is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etDeposit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Deposit amount is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etDescription.getText().toString().isEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etRules.getText().toString().isEmpty()) {
            Toast.makeText(this, "House rules are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etContactInfo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Contact information is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate dates
        String startDateStr = etStartDate.getText().toString();
        String endDateStr = etEndDate.getText().toString();
        DateValidator.ValidationResult validationResult = DateValidator.validateDates(startDateStr, endDateStr);
        if (!validationResult.isValid) {
            Toast.makeText(this, validationResult.errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate surrounding info
        if (etSupermarket.getText().toString().isEmpty() || etHospital.getText().toString().isEmpty() ||
                etTransport.getText().toString().isEmpty() || etEducation.getText().toString().isEmpty()) {
            Toast.makeText(this, "Surrounding information is required (supermarket, hospital, transport, school)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate amenities
        if (chipGroupAmenities.getChildCount() == 0) {
            Toast.makeText(this, "Please select at least one amenity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for saving
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("roomName", etRoomName.getText().toString());
        updatedData.put("rentPrice", etRentPrice.getText().toString());
        updatedData.put("area", etArea.getText().toString());
        updatedData.put("address", etAddress.getText().toString());
        updatedData.put("deposit", etDeposit.getText().toString());
        updatedData.put("otherFees", etOtherFees.getText().toString());
        updatedData.put("description", etDescription.getText().toString());
        updatedData.put("rules", etRules.getText().toString());
        updatedData.put("startDate", startDateStr);
        updatedData.put("endDate", endDateStr);
        updatedData.put("contactInfo", etContactInfo.getText().toString());

        // Surrounding information
        Map<String, String> surroundingInfo = new HashMap<>();
        surroundingInfo.put("supermarket", etSupermarket.getText().toString());
        surroundingInfo.put("hospital", etHospital.getText().toString());
        surroundingInfo.put("transport", etTransport.getText().toString());
        surroundingInfo.put("education", etEducation.getText().toString());
        updatedData.put("surroundingInfo", surroundingInfo);

        // Amenities
        List<String> amenities = new ArrayList<>();
        for (int i = 0; i < chipGroupAmenities.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupAmenities.getChildAt(i);
            if (chip.isChecked()) {
                amenities.add(chip.getText().toString());
            }
        }
        updatedData.put("amenities", amenities);

        // Upload image if needed
        if (newImageUri != null) {
            StorageReference fileRef = storageRef.child("room_" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(newImageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updatedData.put("imageUrl", uri.toString());
                        updateRoomData(updatedData);
                    })).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            updateRoomData(updatedData);
        }
    }


    private void updateRoomData(Map<String, Object> updatedData) {
        progressDialog.show(); // Hiện dialog loading

        db.collection("rooms").document(roomId).update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss(); // Ẩn dialog loading
                    Toast.makeText(this, "Room updated successfully!", Toast.LENGTH_SHORT).show();

                    // Set result để ManageRoomsActivity biết cần refresh
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Ẩn dialog loading khi lỗi
                    Toast.makeText(this, "Failed to update room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}
