package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentIntegration extends AppCompatActivity {

    private Spinner spinnerPaymentMethod;
    private EditText etCardNumber, etCardExpiry, etCardCVV, etEWallet;
    private Button btnConfirmPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_integration);
        setupMenuListeners();

        // Initialize UI elements
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        etCardNumber = findViewById(R.id.etCardNumber);
        etCardExpiry = findViewById(R.id.etCardExpiry);
        etCardCVV = findViewById(R.id.etCardCVV);
        etEWallet = findViewById(R.id.etEWallet);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        // Set up Spinner for payment method selection
        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Show relevant payment info fields based on selection
                switch (position) {
                    case 0: // Credit card
                        etCardNumber.setVisibility(View.VISIBLE);
                        etCardExpiry.setVisibility(View.VISIBLE);
                        etCardCVV.setVisibility(View.VISIBLE);
                        etEWallet.setVisibility(View.GONE);
                        break;
                    case 1: // E-Wallet
                        etCardNumber.setVisibility(View.GONE);
                        etCardExpiry.setVisibility(View.GONE);
                        etCardCVV.setVisibility(View.GONE);
                        etEWallet.setVisibility(View.VISIBLE);
                        break;
                    case 2: // Bank Transfer
                        etCardNumber.setVisibility(View.GONE);
                        etCardExpiry.setVisibility(View.GONE);
                        etCardCVV.setVisibility(View.GONE);
                        etEWallet.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Handle payment confirmation click event
        btnConfirmPayment.setOnClickListener(v -> {
            String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

            if (paymentMethod.equals("Credit card")) {
                String cardNumber = etCardNumber.getText().toString().trim();
                String cardExpiry = etCardExpiry.getText().toString().trim();
                String cardCVV = etCardCVV.getText().toString().trim();

                if (cardNumber.isEmpty() || cardExpiry.isEmpty() || cardCVV.isEmpty()) {
                    Toast.makeText(this, "Please fill in all credit card details", Toast.LENGTH_SHORT).show();
                } else {
                    // Simulate credit card payment
                    Toast.makeText(this, "Payment successful via credit card", Toast.LENGTH_SHORT).show();
                }
            } else if (paymentMethod.equals("E-Wallet")) {
                String eWalletId = etEWallet.getText().toString().trim();

                if (eWalletId.isEmpty()) {
                    Toast.makeText(this, "Please enter e-wallet details", Toast.LENGTH_SHORT).show();
                } else {
                    // Simulate e-wallet payment
                    Toast.makeText(this, "Payment successful via e-wallet", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Simulate bank transfer payment
                Toast.makeText(this, "Bank transfer successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentIntegration.this, OnlineSupport.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentIntegration.this, NotificationSettings.class);
            startActivity(intent);
        });

        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentIntegration.this, FavoriteRooms.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentIntegration.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
