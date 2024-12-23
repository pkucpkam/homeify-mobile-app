package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DepositSystem extends AppCompatActivity {
    private static final String TAG = "DepositSystem";

    private String roomId, owner;
    private String selectedPaymentMethod;
    private FirebaseFirestore db;

    private TextView tvNameRoomDetail, tvPriceDetail, tvAmenitiesDetail, tvDepositAmountDetail;
    private EditText etRenterName, etRenterPhone;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_system);

        initializeViews();
        db = FirebaseFirestore.getInstance("homeify");

        // Retrieve roomId from Intent
        roomId = getIntent().getStringExtra("roomId");
        if (TextUtils.isEmpty(roomId)) {
            Toast.makeText(this, "Room ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get owner
        owner = getIntent().getStringExtra("ownerId");

        setupBackButton();
        setupPaymentMethodSpinner();
        fetchRoomDetailsFromDatabase();
        fetchUserInformation();
    }

    private void initializeViews() {
        tvNameRoomDetail = findViewById(R.id.tvNameRoomDetail);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        tvAmenitiesDetail = findViewById(R.id.tvAmenitiesDetail);
        tvDepositAmountDetail = findViewById(R.id.tvDepositAmountDetail);

        etRenterName = findViewById(R.id.etRenterName);
        etRenterPhone = findViewById(R.id.etRenterPhone);
    }

    private void setupPaymentMethodSpinner() {
        Spinner spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        // Các View thanh toán
        final View cardPaymentViews = findViewById(R.id.cardPaymentViews);
        final View momoPaymentViews = findViewById(R.id.momoImg);
        final View bankPaymentViews = findViewById(R.id.bankImg);

        // Ẩn tất cả các View thanh toán ban đầu
        cardPaymentViews.setVisibility(View.GONE);
        momoPaymentViews.setVisibility(View.GONE);
        bankPaymentViews.setVisibility(View.GONE);

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentMethod = position > 0 ? parent.getItemAtPosition(position).toString() : null;

                // Ẩn tất cả các View thanh toán
                cardPaymentViews.setVisibility(View.GONE);
                momoPaymentViews.setVisibility(View.GONE);
                bankPaymentViews.setVisibility(View.GONE);

                // Hiển thị View tương ứng với phương thức thanh toán đã chọn
                if (selectedPaymentMethod != null) {
                    switch (selectedPaymentMethod) {
                        case "Credit Card":
                            cardPaymentViews.setVisibility(View.VISIBLE);
                            break;
                        case "MoMo":
                            momoPaymentViews.setVisibility(View.VISIBLE);
                            break;
                        case "Bank Transfer":
                            bankPaymentViews.setVisibility(View.VISIBLE);
                            break;
                        default:
                            // Nếu không có phương thức phù hợp
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Đảm bảo không có phương thức thanh toán nào được chọn
                selectedPaymentMethod = null;
            }
        });

        findViewById(R.id.btnConfirmDeposit).setOnClickListener(v -> confirmDeposit());
    }


    private void fetchRoomDetailsFromDatabase() {
        db.collection("rooms").document(roomId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Fetch and update room details
                        updateRoomDetails(document);
                    } else {
                        Toast.makeText(this, "Room details not found.", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity if the room does not exist
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch room details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching room details", e);
                    finish(); // Close the activity on failure
                });
    }

    private void fetchUserInformation() {
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Get username and phone from document
                        String username = document.getString("username");
                        String phone = document.getString("phone");

                        etRenterName.setText(username);
                        etRenterPhone.setText(phone);

                        // Check if fields exist
                        if (username != null && phone != null) {

                        } else {
                            Toast.makeText(this, "User information incomplete", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user information", e);
                    finish();
                });
    }

    private void updateRoomDetails(DocumentSnapshot document) {
        // Room name
        String name = document.getString("roomName");
        tvNameRoomDetail.setText(name != null ? name : "N/A");

        // Room price
        String price = document.getString("rentPrice");
        tvPriceDetail.setText(price != null ? formatPrice(price) : "N/A");

        // Amenities (Handle as a List or String)
        Object amenitiesObject = document.get("amenities");
        if (amenitiesObject instanceof List) {
            List<String> amenitiesList = (List<String>) amenitiesObject;
            tvAmenitiesDetail.setText(TextUtils.join(", ", amenitiesList));
        } else if (amenitiesObject instanceof String) {
            tvAmenitiesDetail.setText((String) amenitiesObject);
        } else {
            tvAmenitiesDetail.setText("N/A");
        }

        // Deposit amount
        String depositAmount = document.getString("deposit");
        tvDepositAmountDetail.setText(depositAmount != null ? formatPrice(depositAmount) : "N/A");
    }

    private String formatPrice(String price) {
        try {
            // Parse price string into a number
            double amount = Double.parseDouble(price);

            // Format the number with proper grouping (e.g., 100.000)
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            return formatter.format(amount) + " VND";
        } catch (NumberFormatException e) {
            // If parsing fails, return the original price
            return price + " VND";
        }
    }


    private void confirmDeposit() {
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to confirm the deposit.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate renter information
        String renterName = etRenterName.getText().toString().trim();
        String renterPhone = etRenterPhone.getText().toString().trim();

        // Check if all fields are filled
        if (TextUtils.isEmpty(renterName)) {
            etRenterName.setError("Please enter your full name");
            return;
        }

        if (TextUtils.isEmpty(renterPhone)) {
            etRenterPhone.setError("Please enter your phone number");
            return;
        }


        if (TextUtils.isEmpty(selectedPaymentMethod)) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> deposit = new HashMap<>();
        deposit.put("roomId", roomId);
        deposit.put("userId", currentUser.getUid());
        deposit.put("renterName", renterName);
        deposit.put("renterPhone", renterPhone);
        deposit.put("owner",owner);
        deposit.put("paymentMethod", selectedPaymentMethod);
        deposit.put("depositAmount", tvDepositAmountDetail.getText().toString());
        deposit.put("createdAt", System.currentTimeMillis());
        deposit.put("renterConfirmed", false); // Người thuê đã xác nhận
        deposit.put("ownerConfirmed", false); // Chờ chủ nhà xác nhận
        deposit.put("status", "pending");

        db.collection("deposits")
                .add(deposit)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Deposit confirmed successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

}
