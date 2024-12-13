package com.app.homiefy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class OnlineSupport extends AppCompatActivity {

    private TextView tvAdminName, tvAdminPhone, tvAdminEmail;
    private Button btnCallNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_support);

        // Initialize UI elements
        tvAdminName = findViewById(R.id.tvAdminName);
        tvAdminPhone = findViewById(R.id.tvAdminPhone);
        tvAdminEmail = findViewById(R.id.tvAdminEmail);
        btnCallNow = findViewById(R.id.btnCallNow);

        setupBackButton();
        setupMenuListeners();

        // Set admin contact details
        tvAdminName.setText("Name: John Doe");
        tvAdminPhone.setText("Phone: +123456789");
        tvAdminEmail.setText("Email: admin@example.com");

        // Call Now button functionality
        btnCallNow.setOnClickListener(v -> {
            String phoneNumber = "+123456789"; // Admin phone number
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(OnlineSupport.this, "Failed to initiate call", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(OnlineSupport.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(OnlineSupport.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(OnlineSupport.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(OnlineSupport.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(OnlineSupport.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}