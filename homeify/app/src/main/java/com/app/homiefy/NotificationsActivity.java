package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.notification.Notification;
import com.app.homiefy.notification.NotificationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance("homeify");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList, notification -> {
            // Handle notification click
            markAsRead(notification);
            // Navigate to relevant screen based on notification type
            handleNotificationClick(notification);
        });

        recyclerView.setAdapter(adapter);

        // Load notifications
        loadNotifications();
        setupMenuListeners();
        setupBackButton();
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadNotifications() {
        db.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading notifications", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        notificationList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Notification notification = document.toObject(Notification.class);
                            notification.setId(document.getId());
                            notificationList.add(notification);
                        }
                        adapter.notifyDataSetChanged();

                        // Show/hide empty view
                        findViewById(R.id.tvEmptyList).setVisibility(
                                notificationList.isEmpty() ? View.VISIBLE : View.GONE
                        );
                    }
                });
    }

    private void markAsRead(Notification notification) {
        if (!notification.isRead()) {
            db.collection("notifications")
                    .document(notification.getId())
                    .update("read", true)
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error marking notification as read",
                                    Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void handleNotificationClick(Notification notification) {
        // Implement navigation logic based on notification type
        // Example:
        /*
        Intent intent;
        switch(notification.getType()) {
            case "order":
                intent = new Intent(this, OrderDetailActivity.class);
                intent.putExtra("orderId", notification.getReferenceId());
                break;
            case "promotion":
                intent = new Intent(this, PromotionDetailActivity.class);
                intent.putExtra("promotionId", notification.getReferenceId());
                break;
            default:
                return;
        }
        startActivity(intent);
        */
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}