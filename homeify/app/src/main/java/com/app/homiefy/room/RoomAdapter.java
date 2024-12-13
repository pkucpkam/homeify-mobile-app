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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        // Set room name
        holder.tvRoomName.setText(room.getRoomName() != null ? room.getRoomName() : "No Name Provided");

        // Set room price
        holder.tvRoomPrice.setText(formatPrice(room.getRentPrice()));

        // Set room area
        holder.tvRoomArea.setText(room.getArea() != null ? room.getArea() + " m²" : "Area Not Provided");

        // Set room address
        holder.tvRoomAddress.setText(room.getAddress() != null ? room.getAddress() : "Address Not Provided");

        // Load room image
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(room.getImageUrl())
                    .into(holder.ivRoomImage);
        } else {
            holder.ivRoomImage.setImageResource(R.drawable.placeholder_image); // Placeholder image
        }
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    public void updateRoomList(List<Room> newRoomList) {
        this.roomList = newRoomList;
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvRoomPrice, tvRoomArea, tvRoomAddress;
        ImageView ivRoomImage;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomArea = itemView.findViewById(R.id.tvRoomArea);
            tvRoomAddress = itemView.findViewById(R.id.tvRoomAddress);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
        }
    }

    private String formatPrice(String price) {
        try {
            double priceValue = Double.parseDouble(price);
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return numberFormat.format(priceValue);
        } catch (NumberFormatException e) {
            return "Invalid Price";
        }
    }
}
