package com.app.homiefy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReportIssue extends AppCompatActivity {

    private Spinner spinnerIssueType;
    private EditText etIssueDescription;
    private Button btnSubmitReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        // Initialize UI elements
        spinnerIssueType = findViewById(R.id.spinnerIssueType);
        etIssueDescription = findViewById(R.id.etIssueDescription);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        // Set up Spinner for issue type selection
        spinnerIssueType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Logic to display/hide additional fields (if needed)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Handle the submit report button click event
        btnSubmitReport.setOnClickListener(v -> {
            String issueType = spinnerIssueType.getSelectedItem().toString();
            String issueDescription = etIssueDescription.getText().toString().trim();

            if (issueDescription.isEmpty()) {
                Toast.makeText(this, "Please provide a detailed description of the issue.", Toast.LENGTH_SHORT).show();
            } else {
                // Simulate the process of submitting the issue report
                // You can replace this with actual backend integration logic
                Toast.makeText(this, "Report submitted successfully.", Toast.LENGTH_SHORT).show();
                // Optionally, you can reset the form after submitting the report
                etIssueDescription.setText("");
            }
        });
    }
}