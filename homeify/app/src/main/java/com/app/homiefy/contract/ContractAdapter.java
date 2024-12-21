package com.app.homiefy.contract;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.ConfirmContractActivity;
import com.app.homiefy.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {
    private List<Contract> contractList;
    private Context context;
    private FirebaseFirestore db;
    private String currentUserId;

    public ContractAdapter(List<Contract> contractList, String currentUserId) {
        this.contractList = contractList;
        this.currentUserId = currentUserId;
        this.db = FirebaseFirestore.getInstance("homeify");
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_contract, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        Contract contract = contractList.get(position);

        // Fetch room details
        db.collection("rooms").document(contract.getRoomId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String roomName = documentSnapshot.getString("roomName");
                        String address = documentSnapshot.getString("address");
                        holder.tvRoomName.setText(roomName);
                        holder.tvAddress.setText(address);
                    }
                });

        // Check status based on current user
        boolean isOwner = currentUserId.equals(contract.getOwner());
        String status;
        int statusColor;

        if (isOwner) {
            if (!contract.isOwnerConfirmed()) {
                status = "Waiting for confirmation";
                statusColor = Color.parseColor("#FFA500"); // Orange
            } else if (contract.isOwnerConfirmed() && !contract.isRenterConfirmed()) {
                status = "Waiting for tenant confirmation";
                statusColor = Color.parseColor("#2196F3"); // Blue
            } else {
                status = "Completed";
                statusColor = Color.parseColor("#4CAF50"); // Green
            }
        } else {
            if (!contract.isRenterConfirmed()) {
                status = "Waiting for confirmation";
                statusColor = Color.parseColor("#FFA500"); // Orange
            } else if (contract.isRenterConfirmed() && !contract.isOwnerConfirmed()) {
                status = "Waiting for host confirmation";
                statusColor = Color.parseColor("#2196F3"); // Blue
            } else {
                status = "Completed";
                statusColor = Color.parseColor("#4CAF50"); // Green
            }
        }

        holder.tvStatus.setText(status);
        holder.tvStatus.setTextColor(statusColor);

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConfirmContractActivity.class);
            intent.putExtra("contractId", contract.getDepositId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    static class ContractViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvAddress, tvStatus;

        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvRoomStatus);
        }
    }
}
