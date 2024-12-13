package com.app.homiefy.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<Room> roomList;

    public RoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.tvRoomTitle.setText(room.getTitle());
        holder.tvRoomDescription.setText(room.getDescription());
        holder.tvRoomPrice.setText(formatPrice(room.getPrice()));
        holder.tvRoomAddress.setText(room.getAddress().toString());
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomTitle, tvRoomDescription, tvRoomPrice, tvRoomAddress;

        public RoomViewHolder(View itemView) {
            super(itemView);
            tvRoomTitle = itemView.findViewById(R.id.tvRoomTitle);
            tvRoomDescription = itemView.findViewById(R.id.tvRoomDescription);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomAddress = itemView.findViewById(R.id.tvRoomAddress);
        }
    }

    private String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price) + " VND";
    }
}
