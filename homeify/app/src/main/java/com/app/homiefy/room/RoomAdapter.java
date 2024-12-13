package com.app.homiefy.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.R;
import com.bumptech.glide.Glide;

import java.util.List;

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

        holder.tvRoomName.setText(room.getRoomName());
        holder.tvRoomPrice.setText(room.getRentPrice());
        holder.tvRoomArea.setText(room.getArea() + " m²");
        holder.tvRoomAddress.setText(room.getAddress());

        // Tải ảnh từ URL (cần thêm thư viện như Glide)
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.ivRoomImage);
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage;
        TextView tvRoomName, tvRoomPrice, tvRoomArea, tvRoomAddress;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomArea = itemView.findViewById(R.id.tvRoomArea);
            tvRoomAddress = itemView.findViewById(R.id.tvRoomAddress);
        }
    }
}
