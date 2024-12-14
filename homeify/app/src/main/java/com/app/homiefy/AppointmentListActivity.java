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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointments;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        db = FirebaseFirestore.getInstance("homeify");
        mAuth = FirebaseAuth.getInstance();
        appointments = new ArrayList<>();

        recyclerView = findViewById(R.id.rvAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadAppointments();
    }

    private void loadAppointments() {
        String currentUserId = mAuth.getCurrentUser().getUid();



        db.collection("room_appointments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appointments.clear();
                    boolean isOwner = false;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String userId = doc.getString("userId");
                        String ownerId = doc.getString("ownerId");

                        // Kiểm tra nếu currentUserId trùng với userId hoặc ownerId
                        if (currentUserId.equals(userId)) {
                            isOwner = false;
                            Appointment appointment = doc.toObject(Appointment.class);
                            if (appointment != null) {
                                appointment.setId(doc.getId());
                                appointments.add(appointment);
                            }
                        }
                        else if (currentUserId.equals(ownerId)) {
                            isOwner = true;
                            Appointment appointment = doc.toObject(Appointment.class);
                            if (appointment != null) {
                                appointment.setId(doc.getId());
                                appointments.add(appointment);
                            }
                        }
                    }

                    // Cập nhật RecyclerView
                    if (adapter == null) {
                        adapter = new AppointmentAdapter(this, appointments, isOwner);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}