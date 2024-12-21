package com.app.homiefy;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.homiefy.contract.Contract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfirmContractActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String contractId;
    private String currentUserId;
    private Contract contract;

    private TextView tvRoomName, tvRoomAddress, tvRoomDeposit, tvRoomPrice;
    private TextView tvOwnerName, tvOwnerPhone, tvOwnerConfirmStatus;
    private TextView tvRenterName, tvRenterPhone, tvRenterConfirmStatus;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_contract);

        db = FirebaseFirestore.getInstance("homeify");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contractId = getIntent().getStringExtra("contractId");

        initViews();
        loadContractDetails();
    }

    private void initViews() {
        tvRoomName = findViewById(R.id.tvRoomName);
        tvRoomAddress = findViewById(R.id.tvRoomAddress);
        tvRoomDeposit = findViewById(R.id.tvRoomDeposit);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);

        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvOwnerPhone = findViewById(R.id.tvOwnerPhone);
        tvOwnerConfirmStatus = findViewById(R.id.tvOwnerConfirmStatus);

        tvRenterName = findViewById(R.id.tvRenterName);
        tvRenterPhone = findViewById(R.id.tvRenterPhone);
        tvRenterConfirmStatus = findViewById(R.id.tvRenterConfirmStatus);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> confirmContract());
    }

    private void loadContractDetails() {
        db.collection("deposits")
                .document(contractId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    contract = documentSnapshot.toObject(Contract.class);
                    if (contract != null) {
                        loadRoomDetails(contract.getRoomId());
                        loadUserDetails(contract.getOwner(), true);
                        loadUserDetails(contract.getUserId(), false);
                        updateConfirmationStatus();
                        updateConfirmButton();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading contract information", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadRoomDetails(String roomId) {
        if (roomId == null) return;

        db.collection("rooms")
                .document(roomId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        String roomName = documentSnapshot.getString("roomName");
                        String address = documentSnapshot.getString("address");
                        String depositStr = documentSnapshot.getString("deposit");
                        String priceStr = documentSnapshot.getString("price");

                        // Chuyển đổi string sang long an toàn
                        long deposit = 0;
                        long price = 0;

                        try {
                            if (depositStr != null && !depositStr.isEmpty()) {
                                deposit = Long.parseLong(depositStr);
                            }
                            if (priceStr != null && !priceStr.isEmpty()) {
                                price = Long.parseLong(priceStr);
                            }
                        } catch (NumberFormatException e) {
                            // Xử lý trường hợp chuỗi không phải số
                            Toast.makeText(ConfirmContractActivity.this,
                                    "Number format error", Toast.LENGTH_SHORT).show();
                        }

                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                        tvRoomName.setText("Room name: " + (roomName != null ? roomName : ""));
                        tvRoomAddress.setText("Address: " + (address != null ? address : ""));
                        tvRoomDeposit.setText("Deposit: " + currencyFormat.format(deposit));
                        tvRoomPrice.setText("Giá thuê: " + currencyFormat.format(price));

                    } catch (Exception e) {
                        Toast.makeText(ConfirmContractActivity.this,
                                "Error processing room information", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ConfirmContractActivity.this,
                                "Room information could not be loaded.", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadUserDetails(String userId, boolean isOwner) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("name");
                    String phone = documentSnapshot.getString("phone");

                    if (isOwner) {
                        tvOwnerName.setText("Full name: " + name);
                        tvOwnerPhone.setText("Phone number: " + phone);
                    } else {
                        tvRenterName.setText("Full name: " + name);
                        tvRenterPhone.setText("Phone number: " + phone);
                    }
                });
    }

    private void updateConfirmationStatus() {
        String ownerStatus = contract.isOwnerConfirmed() ? "Confirmed" : "Not confirmed";
        String renterStatus = contract.isRenterConfirmed() ? "Confirmed" : "Not confirmed";

        tvOwnerConfirmStatus.setText("Status: " + ownerStatus);
        tvRenterConfirmStatus.setText("Status: " + renterStatus);
    }

    private void updateConfirmButton() {
        boolean isOwner = currentUserId.equals(contract.getOwner());
        boolean hasConfirmed = isOwner ? contract.isOwnerConfirmed() : contract.isRenterConfirmed();

        if (hasConfirmed) {
            btnConfirm.setEnabled(false);
            btnConfirm.setText("Contract confirmed");
        } else {
            btnConfirm.setEnabled(true);
            btnConfirm.setText("Confirm contract");
        }
    }

    private void confirmContract() {
        boolean isOwner = currentUserId.equals(contract.getOwner());

        Map<String, Object> updates = new HashMap<>();
        if (isOwner) {
            updates.put("ownerConfirmed", true);
        } else {
            updates.put("renterConfirmed", true);
        }

        db.collection("deposits")
                .document(contractId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (contract.isOwnerConfirmed() && contract.isRenterConfirmed()) {
                        // Cập nhật trạng thái 'rented' của phòng
                        updateRoomStatusToRented();
                    }
                    Toast.makeText(this, "Contract confirmed successfully", Toast.LENGTH_SHORT).show();
                    loadContractDetails();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error while confirming contract", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateRoomStatusToRented() {
        // Lấy ID của phòng từ hợp đồng
        String roomId = contract.getRoomId();

        if (roomId != null) {
            // Cập nhật phòng với trạng thái rented = true
            db.collection("rooms")
                    .document(roomId)
                    .update("rented", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Room status updated to 'rented'", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating room status", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}