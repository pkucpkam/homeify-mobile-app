package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AreaInformation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayout amenitiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_information);

        setupMenuListeners();

        amenitiesList = findViewById(R.id.amenitiesList);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        String[] amenities = {"Hospital", "Supermarket", "School", "Public Transport", "Park"};

        for (String amenity : amenities) {
            TextView amenityTextView = new TextView(this);
            amenityTextView.setText(amenity);
            amenityTextView.setTextSize(18);
            amenityTextView.setPadding(0, 8, 0, 8);
            amenitiesList.addView(amenityTextView);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a default location (example: Hanoi, Vietnam)
        LatLng defaultLocation = new LatLng(21.0285, 105.8542); // Example coordinates for Hanoi
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14));

        // Add a marker for the location (example: the place where the rental is)
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Rental Location"));

        // Optionally, add other markers for amenities
        LatLng hospitalLocation = new LatLng(21.0295, 105.8535);  // Example coordinates for hospital
        mMap.addMarker(new MarkerOptions().position(hospitalLocation).title("Nearby Hospital"));

        LatLng supermarketLocation = new LatLng(21.0305, 105.8540);  // Example coordinates for supermarket
        mMap.addMarker(new MarkerOptions().position(supermarketLocation).title("Nearby Supermarket"));

        // You can add more markers for other amenities similarly
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, OnlineSupport.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, FavoriteRooms.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AreaInformation.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}