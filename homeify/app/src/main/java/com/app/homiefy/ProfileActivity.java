package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.homiefy.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvFullName, tvEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("homeify");
        sessionManager = new SessionManager(this);

        // Initialize TextViews
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Get UID from session
        String uid = sessionManager.getUserId();

        // Load user profile data
        loadUserProfile(uid);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        setupMenuListeners();
        setupBackButton();
        setupLogoutButton();
    }

    private void loadUserProfile(String uid) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        // Update UI with user data
                        tvFullName.setText(name);
                        tvEmail.setText(email);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    tvFullName.setText("Error loading data");
                    tvEmail.setText("Error loading data");
                });
    }

    private void setupLogoutButton() {
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupMenuListeners() {

        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Navigate to Favorites list
        LinearLayout layoutFavorites = findViewById(R.id.layoutManageProperties);
        layoutFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteRooms.class);
            startActivity(intent);
        });

        // Navigate to Contract management
        LinearLayout layoutContracts = findViewById(R.id.layoutContracts);
        layoutContracts.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ContractManagement.class);
            startActivity(intent);
        });

        // Navigate to Appointment schedule
        LinearLayout layoutAppointments = findViewById(R.id.layoutAppointments);
        layoutAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AppointmentListActivity.class);
            startActivity(intent);
        });

        // Navigate to Report a problem
        LinearLayout layoutReportIssue = findViewById(R.id.layoutReportIssue);
        layoutReportIssue.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ReportIssue.class);
            startActivity(intent);
        });

        // Navigate to Support
        LinearLayout layoutSupport = findViewById(R.id.layoutSupport);
        layoutSupport.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OnlineSupport.class);
            startActivity(intent);
        });

    }
}
