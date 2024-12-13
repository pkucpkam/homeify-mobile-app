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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentListActivity";

    private FirebaseFirestore db;
    private RecyclerView rvAppointments;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance("homeify");

        // Initialize RecyclerView
        rvAppointments = findViewById(R.id.rvAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(appointmentList);
        rvAppointments.setAdapter(adapter);

        // Fetch appointments from Firestore
        fetchAppointments();
    }

    private void fetchAppointments() {
        db.collection("room_appointments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            String email = document.getString("email");
                            String date = document.getString("date");
                            String time = document.getString("time");

                            Appointment appointment = new Appointment(name, phone, email, date, time);
                            appointmentList.add(appointment);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Appointments fetched successfully.");
                    } else {
                        Log.e(TAG, "Error fetching appointments", task.getException());
                        Toast.makeText(this, "Failed to fetch appointments.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching appointments", e);
                    Toast.makeText(this, "Failed to fetch appointments.", Toast.LENGTH_SHORT).show();
                });
    }
}