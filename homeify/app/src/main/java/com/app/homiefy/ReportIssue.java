package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.homiefy.utils.SessionManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportIssue extends AppCompatActivity {

    private static final String TAG = "ReportIssue";

    private Spinner spinnerIssueType;
    private EditText etIssueDescription;
    private Button btnSubmitReport;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_issue);

        // Initialize Firebase Firestore and Session Manager
        db = FirebaseFirestore.getInstance("homeify");
        sessionManager = new SessionManager(this);

        // Initialize UI elements
        spinnerIssueType = findViewById(R.id.spinnerIssueType);
        etIssueDescription = findViewById(R.id.etIssueDescription);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnBack = findViewById(R.id.btnBack);

        // Set up Spinner with issue types from resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.issue_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIssueType.setAdapter(adapter);

        // Set up back button
        btnBack.setOnClickListener(v -> finish());

        // Handle the submit report button click event
        btnSubmitReport.setOnClickListener(v -> submitIssueReport());

        setupMenuListeners();

        // Adjust system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void submitIssueReport() {
        // Get current logged-in user from session
        String userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "Please log in to submit a report", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User session not found.");
            return;
        }

        // Get input values
        String issueType = spinnerIssueType.getSelectedItem().toString();
        String issueDescription = etIssueDescription.getText().toString().trim();

        // Validate input
        if (issueDescription.isEmpty()) {
            Toast.makeText(this, "Please provide a detailed description of the issue.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Issue description is empty.");
            return;
        }

        // Prepare issue report data
        Map<String, Object> issueReport = new HashMap<>();
        issueReport.put("userId", userId);
        issueReport.put("issueType", issueType);
        issueReport.put("description", issueDescription);
        issueReport.put("status", "OPEN");
        issueReport.put("createdAt", System.currentTimeMillis());
        issueReport.put("updatedAt", System.currentTimeMillis());

        // Generate a unique report ID
        String reportId = UUID.randomUUID().toString();

        Log.d(TAG, "Submitting issue report with ID: " + reportId);

        // Submit to Firestore
        db.collection("issue_reports").document(reportId)
                .set(issueReport)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Issue report submitted successfully.");
                    Toast.makeText(ReportIssue.this, "Issue report submitted successfully.", Toast.LENGTH_SHORT).show();
                    // Reset form
                    etIssueDescription.setText("");
                    spinnerIssueType.setSelection(0);
                    Intent intent = new Intent(ReportIssue.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to submit issue report.", e);
                    Toast.makeText(ReportIssue.this, "Failed to submit issue report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(ReportIssue.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(ReportIssue.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(ReportIssue.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ReportIssue.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ReportIssue.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
