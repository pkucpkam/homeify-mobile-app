package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RoomViewingAppointment extends AppCompatActivity {

    private static final String TAG = "RoomViewingAppointment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvAddress, tvPrice;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextInputEditText edtDateTime;
    private EditText edtName, edtPhone, edtEmail;
    private Button btnConfirmAppointment;
    private LinearLayout dateTimePickerContainer;
    private Calendar selectedDateTime;
    private String roomId, ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_viewing_appointment);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("homeify");

        // Initialize Calendar
        selectedDateTime = Calendar.getInstance();

        // Get roomId
        roomId = getIntent().getStringExtra("roomId");
        ownerId = getIntent().getStringExtra("ownerId");

        // Find views
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        edtDateTime = findViewById(R.id.edtDateTime);
        dateTimePickerContainer = findViewById(R.id.dateTimePickerContainer);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnConfirmAppointment = findViewById(R.id.btnConfirmAppointment);
        tvAddress = findViewById(R.id.tvAddress);
        tvPrice = findViewById(R.id.tvPrice);

        // Set room information
        tvAddress.setText(getIntent().getStringExtra("roomAddress"));
        tvPrice.setText(getIntent().getStringExtra("roomRentPrice"));

        // Setup DateTime picker
        setupDateTimePicker();

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

    private void setupDateTimePicker() {
        // Set minimum date to today
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        // Use 24 hour view for TimePicker
        timePicker.setIs24HourView(true);

        // Initialize with current date and time
        updateDateTimeDisplay();

        // Add listeners for date and time changes
        datePicker.init(selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, monthOfYear);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                });

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);
            updateDateTimeDisplay();
        });

        // Setup click listener for datetime field
        edtDateTime.setOnClickListener(v -> {
            if (dateTimePickerContainer.getVisibility() == View.VISIBLE) {
                dateTimePickerContainer.setVisibility(View.GONE);
            } else {
                dateTimePickerContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveSelectedDateTime() {
        selectedDateTime.set(Calendar.YEAR, datePicker.getYear());
        selectedDateTime.set(Calendar.MONTH, datePicker.getMonth());
        selectedDateTime.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        selectedDateTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        selectedDateTime.set(Calendar.MINUTE, timePicker.getMinute());

        updateDateTimeDisplay();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        edtDateTime.setText(sdf.format(selectedDateTime.getTime()));
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
        String dateTime = edtDateTime.getText().toString().trim();

        // Validate input
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || dateTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare appointment data
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("name", name);
        appointment.put("phone", phone);
        appointment.put("email", email);
        appointment.put("dateTime", dateTime);
        appointment.put("roomId", roomId);
        appointment.put("ownerId", ownerId);
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
                    edtDateTime.setText("");
                    selectedDateTime = Calendar.getInstance();
                    updateDateTimeDisplay();
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