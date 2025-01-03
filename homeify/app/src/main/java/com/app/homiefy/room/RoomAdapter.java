package com.app.homiefy.room;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.PostingRoom;
import com.app.homiefy.R;
import com.app.homiefy.RoomDetails;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<Room> roomList;

    public RoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        // Set room name
        holder.tvRoomName.setText("Room name: " + room.getRoomName());

        // Format the rent price
        String rentPrice = room.getRentPrice();
        try {
            double price = Double.parseDouble(rentPrice);
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedPrice = formatter.format(price) + " VND";
            holder.tvRoomPrice.setText("Price: " + formattedPrice);
        } catch (NumberFormatException e) {
            holder.tvRoomPrice.setText("Price: " + rentPrice + " VND");
        }

        // Set area
        holder.tvRoomArea.setText("Area: " + room.getArea() + " m²");

        // Set address
        holder.tvRoomAddress.setText("Address: " + room.getAddress());

        // Set room status based on rented value
        if (room.isRented()) {
            holder.tvRoomStatus.setText("Status: Not Available");
            holder.tvRoomStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvRoomStatus.setText("Status: Available");
            holder.tvRoomStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }

        // Load room image
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.ivRoomImage);
        }

        // Handle View Details button click
        holder.btnViewDetails.setOnClickListener(v -> {
            if (room.getId() != null) {
                Intent intent = new Intent(v.getContext(), RoomDetails.class);
                intent.putExtra("roomId", room.getId());
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "Room ID is missing!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage;
        TextView tvRoomName, tvRoomPrice, tvRoomArea, tvRoomAddress, tvRoomStatus;
        Button btnViewDetails; // Button for viewing room details

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomArea = itemView.findViewById(R.id.tvRoomArea);
            tvRoomAddress = itemView.findViewById(R.id.tvRoomAddress);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails); // Initialize button
            tvRoomStatus = itemView.findViewById(R.id.tvRoomStatus);
        }
    }
}
