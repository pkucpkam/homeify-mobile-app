package com.app.homiefy;

import static android.content.ContentValues.TAG;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class TestFirebaseActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseTest";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase);

        // Check Google Play Services
        if (checkPlayServices()) {
            initFirebase();
        } else {
            Log.e(TAG, "Google Play Services not available");
            // Show dialog to install/update Google Play Services
            showPlayServicesDialog();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            return false;
        }
        return true;
    }

    private void showPlayServicesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Google Play Services Required")
                .setMessage("This app requires Google Play Services to function. Please install or update Google Play Services.")
                .setPositiveButton("Install", (dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.google.android.gms")));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")));
                    }
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void initFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance("homeify");

        Map<String, Object> user = new HashMap<>();
        user.put("first", "Test");
        user.put("last", "User");
        user.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Success: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error: " + e.getMessage()));
    }
}