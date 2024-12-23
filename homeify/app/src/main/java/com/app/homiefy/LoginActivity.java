package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.homiefy.utils.SessionManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 9001; // Ma nhan dang GG
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;
    private GoogleSignInClient mGoogleSignInClient;  // Doi tuong dang nhap cua gg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("homeify");
        sessionManager = new SessionManager(this);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Find views in the layout
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView tvRegister = findViewById(R.id.tvRegister);
        Button googleSignInBtn = findViewById(R.id.googleSignInBtn);

        loginBtn.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                db.collection("users").document(uid)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot document = task1.getResult();
                                                if (document.exists()) {
                                                    String role = document.getString("role");
                                                    // Save UID and role to session
                                                    sessionManager.createLoginSession(uid, role);

                                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Error getting user data: " + task1.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        googleSignInBtn.setOnClickListener(v -> signInWithGoogle());

        // Ensure the interface is not obstructed by system bars (if needed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign-In failed
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String email = user.getEmail(); // Lấy email từ Google
                            String name = acct.getDisplayName();
                            // Kiểm tra nếu user chưa tồn tại, thêm tài liệu mới
                            db.collection("users").document(uid)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document = task1.getResult();
                                            if (!document.exists()) {
                                                // Nếu tài liệu chưa tồn tại, tạo user mới
                                                Map<String, Object> newUser = new HashMap<>();
                                                newUser.put("email", email);
                                                newUser.put("name", name);
                                                newUser.put("role", "USER"); // Vai trò mặc định
                                                newUser.put("createdAt", System.currentTimeMillis());
                                                newUser.put("username", name);

                                                db.collection("users").document(uid)
                                                        .set(newUser)
                                                        .addOnSuccessListener(aVoid -> {
                                                            // Lưu thông tin vào session
                                                            sessionManager.createLoginSession(uid, "user");
                                                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(LoginActivity.this, "Failed to create user: " + e.getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                // Nếu user đã tồn tại
                                                String role = document.getString("role");
                                                sessionManager.createLoginSession(uid, role);
                                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error checking user data: " + task1.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
