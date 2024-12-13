package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class DepositSystem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_system);

        setupMenuListeners();

        Spinner spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPaymentMethod.setAdapter(adapter);

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedPaymentMethod = parentView.getItemAtPosition(position).toString();
                Toast.makeText(DepositSystem.this, "Select method: " + selectedPaymentMethod, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        View rootView = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(DepositSystem.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(DepositSystem.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(DepositSystem.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(DepositSystem.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DepositSystem.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
