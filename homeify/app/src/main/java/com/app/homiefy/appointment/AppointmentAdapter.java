package com.app.homiefy.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<Appointment> appointments;
    private boolean isOwner;
    private FirebaseFirestore db;
    private Context context;

    public AppointmentAdapter(Context context, List<Appointment> appointments, boolean isOwner) {
        this.context = context;
        this.appointments = appointments;
        this.isOwner = isOwner;
        this.db = FirebaseFirestore.getInstance("homeify");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.tvName.setText(appointment.getRoomName());
        holder.tvDateTime.setText(appointment.getDateTime());

        // Set status text and color
        switch (appointment.getStatus()) {
            case Appointment.STATUS_PENDING:
                holder.tvStatus.setText("Waiting");
                holder.tvStatus.setTextColor(context.getColor(R.color.pending));
                break;
            case Appointment.STATUS_ACCEPTED:
                holder.tvStatus.setText("Appointed");
                holder.tvStatus.setTextColor(context.getColor(R.color.accepted));
                break;
            case Appointment.STATUS_REJECTED:
                holder.tvStatus.setText("Refused");
                holder.tvStatus.setTextColor(context.getColor(R.color.rejected));
                break;
        }

        // Show/hide action buttons for owner
        if (isOwner && appointment.getStatus().equals(Appointment.STATUS_PENDING)) {
            holder.actionButtons.setVisibility(View.VISIBLE);
        } else {
            holder.actionButtons.setVisibility(View.GONE);
        }

        // Handle accept button
        holder.btnAccept.setOnClickListener(v -> {
            updateAppointmentStatus(appointment.getId(), Appointment.STATUS_ACCEPTED);
        });

        // Handle reject button
        holder.btnReject.setOnClickListener(v -> {
            updateAppointmentStatus(appointment.getId(), Appointment.STATUS_REJECTED);
        });
    }

    private void updateAppointmentStatus(String appointmentId, String status) {
        db.collection("room_appointments")
                .document(appointmentId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật trạng thái trong danh sách local
                    for (Appointment appointment : appointments) {
                        if (appointment.getId().equals(appointmentId)) {
                            appointment.setStatus(status);
                            // Thông báo adapter cập nhật lại UI
                            notifyDataSetChanged();
                            break;
                        }
                    }
                    Toast.makeText(context, "Status update successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDateTime, tvStatus;
        LinearLayout actionButtons;
        Button btnAccept, btnReject;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvDateTime = view.findViewById(R.id.tvDateTime);
            tvStatus = view.findViewById(R.id.tvRoomStatus);
            actionButtons = view.findViewById(R.id.actionButtons);
            btnAccept = view.findViewById(R.id.btnAccept);
            btnReject = view.findViewById(R.id.btnReject);
        }
    }
}