package com.app.homiefy;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.appointment.Appointment;
import com.app.homiefy.appointment.AppointmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointments;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private boolean isOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        appointments = new ArrayList<>();

        // Determine if current user is owner
        isOwner = getIntent().getBooleanExtra("isOwner", false);

        recyclerView = findViewById(R.id.rvAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(this, appointments, isOwner);
        recyclerView.setAdapter(adapter);

        loadAppointments();
    }

    private void loadAppointments() {
        String userId = mAuth.getCurrentUser().getUid();
        Query query;

        if (isOwner) {
            query = db.collection("room_appointments")
                    .whereEqualTo("ownerId", userId);
        } else {
            query = db.collection("room_appointments")
                    .whereEqualTo("userId", userId);
        }

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            appointments.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                Appointment appointment = doc.toObject(Appointment.class);
                appointment.setId(doc.getId());
                appointments.add(appointment);
            }
            adapter.notifyDataSetChanged();
        });
    }
}