package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContractDetailsActivity extends AppCompatActivity {

    private TextView tvContractName, tvSigningDate, tvLeaseDuration;
    private Button btnDownloadContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_details);

        tvContractName = findViewById(R.id.tvContractName);
        tvSigningDate = findViewById(R.id.tvSigningDate);
        tvLeaseDuration = findViewById(R.id.tvLeaseDuration);
        btnDownloadContract = findViewById(R.id.btnDownloadContract);

        Intent intent = getIntent();
        String contractName = intent.getStringExtra("contractName");
        String signingDate = intent.getStringExtra("signingDate");
        String leaseDuration = intent.getStringExtra("leaseDuration");

        tvContractName.setText(contractName);
        tvSigningDate.setText(signingDate);
        tvLeaseDuration.setText(leaseDuration);

        btnDownloadContract.setOnClickListener(v -> {
            Toast.makeText(this, "Download PDF Contract", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(ContractDetailsActivity.this, OnlineSupport.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(ContractDetailsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ContractDetailsActivity.this, FavoriteRooms.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ContractDetailsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}