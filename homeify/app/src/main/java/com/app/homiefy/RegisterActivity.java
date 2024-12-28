package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("homeify");

        // Find views in the layout
        EditText edtFullName = findViewById(R.id.edtFullName);
        EditText edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        EditText edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        Button registerBtn = findViewById(R.id.registerBtn);
        TextView tvLogin = findViewById(R.id.tvLogin); // TextView for redirecting to LoginActivity
        TextView errorTxt = findViewById(R.id.errorTxt);

        errorTxt.setVisibility(View.GONE); // Hide error text initially

        // Handle click event when registering
        registerBtn.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString().trim();
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            errorTxt.setVisibility(View.GONE); // Hide error text before validation

            // Validate input fields
            if (fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("Please fill in all fields.");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("Invalid email format.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("Passwords do not match.");
                return;
            }

            if (password.length() < 6) {
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("Password must be at least 6 characters long.");
                return;
            }

            // Register the new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Save user information in Firestore
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("username", fullName);
                                userData.put("email", email);
                                userData.put("phone", phoneNumber);
                                userData.put("role", "USER");
                                userData.put("name", fullName);
                                userData.put("createdAt", System.currentTimeMillis());
                                userData.put("updatedAt", System.currentTimeMillis());
                                userData.put("status", "ACTIVE");

                                // Save to Firestore
                                db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            errorTxt.setVisibility(View.GONE);
                                            showSuccessDialog();
                                        })
                                        .addOnFailureListener(e -> {
                                            errorTxt.setVisibility(View.VISIBLE);
                                            errorTxt.setText("Error saving user information: " + e.getMessage());
                                        });
                            }
                        } else {
                            errorTxt.setVisibility(View.VISIBLE);
                            errorTxt.setText(task.getException().getMessage());
                        }
                    });
        });

        // Handle event when clicking TextView to go back to LoginActivity
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Ensure the interface is not obstructed by system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    private void showSuccessDialog() {
        // Tạo dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Registration Successful")
                .setMessage("You have successfully registered. Please log in to continue.")
                .setCancelable(false) // Không cho phép tắt dialog bằng cách nhấn ra ngoài
                .setPositiveButton("Go to Login", (dialog, which) -> {
                    // Chuyển về trang đăng nhập
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });

        // Hiển thị dialog
        builder.create().show();
    }

}
