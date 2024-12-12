package com.app.homiefy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RoomDetails extends AppCompatActivity {

    private TextView tvRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String roomId = getIntent().getStringExtra("ROOM_ID");

//        // Hiển thị ID phòng (nếu bạn muốn)
//        tvRoomId = findViewById(R.id.tvRoomId); // Giả sử bạn có TextView này trong layout
//        if (roomId != null) {
//            tvRoomId.setText("Room ID: " + roomId);
//        }


        setupMenuListeners();
    }
    public static Intent createIntent(Context context, String roomId) {
        Intent intent = new Intent(context, RoomDetails.class);
        intent.putExtra("ROOM_ID", roomId);
        return intent;
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, OnlineSupport.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, NotificationSettings.class);
            startActivity(intent);
        });

        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, FavoriteRooms.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}