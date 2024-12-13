package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportIssue extends AppCompatActivity {

    private Spinner spinnerIssueType;
    private EditText etIssueDescription;
    private Button btnSubmitReport;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_issue);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        // Get current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to submit a report", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String issueType = spinnerIssueType.getSelectedItem().toString();
        String issueDescription = etIssueDescription.getText().toString().trim();

        // Validate input
        if (issueDescription.isEmpty()) {
            Toast.makeText(this, "Please provide a detailed description of the issue.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare issue report data
        Map<String, Object> issueReport = new HashMap<>();
        issueReport.put("userId", currentUser.getUid());
        issueReport.put("userEmail", currentUser.getEmail());
        issueReport.put("issueType", issueType);
        issueReport.put("description", issueDescription);
        issueReport.put("status", "OPEN");
        issueReport.put("createdAt", System.currentTimeMillis());
        issueReport.put("updatedAt", System.currentTimeMillis());

        // Generate a unique report ID
        String reportId = UUID.randomUUID().toString();

        // Submit to Firestore
        db.collection("issue_reports").document(reportId)
                .set(issueReport)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ReportIssue.this, "Issue report submitted successfully.", Toast.LENGTH_SHORT).show();
                        // Reset form
                        etIssueDescription.setText("");
                        spinnerIssueType.setSelection(0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReportIssue.this, "Failed to submit issue report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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