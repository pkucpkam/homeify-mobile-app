package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Xử lý sự kiện của menu trong MainActivity
        setupMenuListeners();

        // Chuyển sang trang tìm kiếm khi bấm nút
        findViewById(R.id.btnGoToSearch).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchRoom.class);
            startActivity(intent);
        });
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnlineSupport.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationSettings.class);
            startActivity(intent);
        });

        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoriteRooms.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
