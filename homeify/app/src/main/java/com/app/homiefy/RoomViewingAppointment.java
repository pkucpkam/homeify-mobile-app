package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomViewingAppointment extends AppCompatActivity {

    private static final String TAG = "RoomViewingAppointment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText edtName, edtPhone, edtEmail;
    private Button btnConfirmAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_viewing_appointment);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("homeify");

        // Find views
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnConfirmAppointment = findViewById(R.id.btnConfirmAppointment);

        // Set up Confirm Appointment button
        btnConfirmAppointment.setOnClickListener(v -> submitAppointment());

        // Adjust system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupMenuListeners();

        setupBackButton();
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void submitAppointment() {
        // Retrieve input values
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is 0-indexed
        int year = datePicker.getYear();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Validate input
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare appointment data
        String date = day + "/" + month + "/" + year;
        String time = String.format("%02d:%02d", hour, minute);

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("name", name);
        appointment.put("phone", phone);
        appointment.put("email", email);
        appointment.put("date", date);
        appointment.put("time", time);
        appointment.put("userId", mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "guest");
        appointment.put("createdAt", System.currentTimeMillis());

        // Generate a unique appointment ID
        String appointmentId = UUID.randomUUID().toString();

        // Save to Firestore
        db.collection("room_appointments").document(appointmentId)
                .set(appointment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RoomViewingAppointment.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Appointment booked successfully with ID: " + appointmentId);

                    // Clear input fields
                    edtName.setText("");
                    edtPhone.setText("");
                    edtEmail.setText("");
                    timePicker.setHour(0);
                    timePicker.setMinute(0);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RoomViewingAppointment.this, "Failed to book appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error booking appointment", e);
                });
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(RoomViewingAppointment.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(RoomViewingAppointment.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(RoomViewingAppointment.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(RoomViewingAppointment.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(RoomViewingAppointment.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
