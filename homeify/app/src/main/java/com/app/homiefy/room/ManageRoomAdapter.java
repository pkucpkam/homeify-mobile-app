package com.app.homiefy.room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.EditRoomActivity;
import com.app.homiefy.R;
import com.app.homiefy.RoomDetails;
import com.app.homiefy.notification.Notification;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;

public class ManageRoomAdapter extends RecyclerView.Adapter<ManageRoomAdapter.ManageRoomViewHolder> {

    private List<Room> roomList;
    private static final int EDIT_ROOM_REQUEST = 100;

    public ManageRoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public ManageRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_room, parent, false);
        return new ManageRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageRoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        // Set room name
        holder.tvRoomName.setText("Room name: " + room.getRoomName());

        // Set price
        holder.tvRoomPrice.setText("Price: " + room.getRentPrice() + " VND");

        // Set area
        holder.tvRoomArea.setText("Area: " + room.getArea() + " m²");

        // Set address
        holder.tvRoomAddress.setText("Address: " + room.getAddress());

        // Set status
        if (room.isRented()) {
            holder.tvRoomStatus.setText("Status: Not Available");
            holder.tvRoomStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            holder.btnRemind.setVisibility(View.VISIBLE);
        } else {
            holder.tvRoomStatus.setText("Status: Available");
            holder.tvRoomStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            holder.btnRemind.setVisibility(View.GONE);
        }

        // Load image with null check
        String imageUrl = room.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.ivRoomImage);
        } else {
            holder.ivRoomImage.setImageResource(R.drawable.placeholder_image);
        }

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, EditRoomActivity.class);

            // Truyền dữ liệu phòng qua Intent
            intent.putExtra("roomId", room.getId());
            intent.putExtra("roomName", room.getRoomName());
            intent.putExtra("rentPrice", room.getRentPrice());
            intent.putExtra("area", room.getArea());
            intent.putExtra("address", room.getAddress());
            intent.putExtra("deposit", room.getDeposit());
            intent.putExtra("otherFees", room.getOtherFees());
            intent.putExtra("description", room.getDescription());
            intent.putExtra("rules", room.getRules());
            intent.putExtra("startDate", room.getStartDate());
            intent.putExtra("endDate", room.getEndDate());
            intent.putExtra("contactInfo", room.getContactInfo());
            intent.putExtra("imageUrl", room.getImageUrl());

            if (room.getSurroundingInfo() != null) {
                intent.putExtra("supermarket", room.getSurroundingInfo().get("supermarket"));
                intent.putExtra("hospital", room.getSurroundingInfo().get("hospital"));
                intent.putExtra("transport", room.getSurroundingInfo().get("transport"));
                intent.putExtra("education", room.getSurroundingInfo().get("education"));
            }

            if (room.getAmenities() != null) {
                intent.putExtra("amenities", room.getAmenities().toArray(new String[0]));
            }

            // Sử dụng startActivityForResult thay vì startActivity
            ((Activity) context).startActivityForResult(intent, EDIT_ROOM_REQUEST);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            // Tạo AlertDialog để xác nhận trước khi xóa
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this room?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Nếu người dùng chọn Yes, thực hiện xóa phòng trong Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance("homeify");
                        DocumentReference roomRef = db.collection("rooms").document(room.getId());

                        // Cập nhật trường 'isDeleted' thành true
                        roomRef.update("deleted", true)
                                .addOnSuccessListener(aVoid -> {
                                    // Sau khi cập nhật thành công, xóa phòng khỏi danh sách và cập nhật RecyclerView
                                    roomList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(holder.itemView.getContext(), "Room deleted: " + room.getRoomName(), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(holder.itemView.getContext(), "Failed to delete room: " + room.getRoomName(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Nếu người dùng chọn Cancel, không làm gì cả
                        dialog.dismiss();
                    })
                    .show();
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, RoomDetails.class);
            intent.putExtra("roomId", room.getId());
            context.startActivity(intent);
        });

        //Remind button
        holder.btnRemind.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            FirebaseFirestore db = FirebaseFirestore.getInstance("homeify");

            // Truy vấn collection "deposits" để tìm tài liệu có roomId trùng khớp
            db.collection("deposits")
                    .whereEqualTo("roomId", room.getId())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Lấy userId từ tài liệu đầu tiên trùng khớp
                            String receiverId = querySnapshot.getDocuments().get(0).getString("userId");
                            if (receiverId != null) {
                                // Tạo thông báo
                                String notificationId = UUID.randomUUID().toString(); // ID thông báo duy nhất
                                String title = "Reminder: Rent and Utility Payment";
                                String content = "This is a reminder to pay the rent and utility bills for the room: " + room.getRoomName();
                                String timestamp = String.valueOf(System.currentTimeMillis());
                                boolean isRead = false; // Mặc định là chưa đọc

                                // Tạo đối tượng Notification
                                Notification notification = new Notification(notificationId, title, content, timestamp, isRead, receiverId);

                                // Lưu thông báo vào Firestore
                                db.collection("notifications").document(notificationId).set(notification)
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(context, "Reminder notification sent successfully!", Toast.LENGTH_SHORT).show()
                                        )
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Failed to send reminder notification: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            } else {
                                Toast.makeText(context, "No user associated with this room!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "No deposit found for this room!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to fetch deposit information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });


    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ManageRoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage;
        TextView tvRoomName, tvRoomPrice, tvRoomArea, tvRoomAddress, tvRoomStatus;
        Button btnEdit, btnDelete, btnViewDetails, btnRemind;

        public ManageRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomArea = itemView.findViewById(R.id.tvRoomArea);
            tvRoomAddress = itemView.findViewById(R.id.tvRoomAddress);
            tvRoomStatus = itemView.findViewById(R.id.tvRoomStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnRemind = itemView.findViewById(R.id.btnRemind);
        }
    }
}