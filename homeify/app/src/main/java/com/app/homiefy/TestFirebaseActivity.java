package com.app.homiefy;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TestFirebaseActivity extends AppCompatActivity {

    private static final String TAG = "FirebaseTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase);

        FirebaseOptions options1 = new FirebaseOptions.Builder()
                .setProjectId("homeify-3380d")
                .setApplicationId("1:92431922154:android:68feb36ba0daeed7a37893")
                .setApiKey("AIzaSyBaZLVk7kkw3svm0EaHMxF2MJqFgzHHs0k")
                .build();

        FirebaseApp.initializeApp(this, options1, "DB_PROD");

        FirebaseFirestore db = FirebaseFirestore.getInstance(FirebaseApp.getInstance("DB_PROD"));


        Map<String, Object> user = new HashMap<>();
        user.put("first", "Phuc");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Thêm document mới
        db.collection("users").add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Document added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });

        // Đọc dữ liệu
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}