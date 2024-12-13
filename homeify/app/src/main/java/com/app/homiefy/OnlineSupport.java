package com.app.homiefy;

import android.content.Intent;
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

    private EditText etSupportRequest, etChatMessage;
    private Button btnSubmitRequest, btnSendMessage;
    private TextView tvChatMessages;
    private LinearLayout llChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_support);

        setupBackButton();
        setupMenuListeners();

        // Initialize UI elements
        etSupportRequest = findViewById(R.id.etSupportRequest);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        tvChatMessages = findViewById(R.id.tvChatMessages);
        llChat = findViewById(R.id.llChat);

        // Handle the support request form submission
        btnSubmitRequest.setOnClickListener(v -> {
            String requestMessage = etSupportRequest.getText().toString().trim();

            if (requestMessage.isEmpty()) {
                Toast.makeText(this, "Please type a question or support request", Toast.LENGTH_SHORT).show();
            } else {
                // Simulate sending the support request (replace with actual backend call)
                Toast.makeText(this, "Support request submitted successfully", Toast.LENGTH_SHORT).show();
                etSupportRequest.setText("");  // Clear the input field
            }
        });

        // Handle the chat message submission
        btnSendMessage.setOnClickListener(v -> {
            String message = etChatMessage.getText().toString().trim();

            if (message.isEmpty()) {
                Toast.makeText(this, "Please type a message to send", Toast.LENGTH_SHORT).show();
            } else {
                // Simulate sending the chat message (replace with actual backend chat logic)
                String currentMessages = tvChatMessages.getText().toString();
                tvChatMessages.setText(currentMessages + "\nYou: " + message);
                etChatMessage.setText("");  // Clear the message input field

                // Simulate a response from support (can be replaced with backend interaction)
                tvChatMessages.append("\nSupport: Thank you for your message. How can I assist you?");
            }
        });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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