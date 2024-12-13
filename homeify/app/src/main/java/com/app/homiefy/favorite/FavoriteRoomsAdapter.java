package com.app.homiefy.favorite;

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
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.R;
import com.app.homiefy.RoomDetails;
import com.app.homiefy.room.Room;
import com.app.homiefy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavoriteRoomsAdapter extends RecyclerView.Adapter<FavoriteRoomsAdapter.ViewHolder> {

    private final List<String> favoriteRoomIds; // Store only room IDs
    private final Context context;
    private final FirebaseFirestore db;

    public FavoriteRoomsAdapter(List<String> favoriteRoomIds, Context context) {
        this.favoriteRoomIds = favoriteRoomIds;
        this.context = context;
        this.db = FirebaseFirestore.getInstance("homeify"); // Initialize Firestore
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_favorite_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String roomId = favoriteRoomIds.get(position);

        // Fetch room details using roomId
        db.collection("rooms").document(roomId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Room room = documentSnapshot.toObject(Room.class);
                        if (room != null) {
                            // Populate the view holder with room details
                            holder.tvRoomName.setText("Room name: " + room.getRoomName());
                            holder.tvRoomAddress.setText("Address: " + room.getAddress());
                            String rentPrice = room.getRentPrice();
                            try {
                                double price = Double.parseDouble(rentPrice);
                                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                                String formattedPrice = formatter.format(price) + " VND/Month";
                                holder.tvRoomPrice.setText("Price: " + formattedPrice);
                            } catch (NumberFormatException e) {
                                holder.tvRoomPrice.setText("Price: " + rentPrice + " VND/Month");
                            }

                            Glide.with(context)
                                    .load(room.getImageUrl())
                                    .placeholder(R.drawable.placeholder_image)
                                    .into(holder.ivRoomImage);

                            // Handle "View Details" button
                            holder.btnViewDetails.setOnClickListener(v -> {
                                Intent intent = new Intent(context, RoomDetails.class);
                                intent.putExtra("roomId", roomId); // Pass room ID to RoomDetails activity
                                context.startActivity(intent);
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch room details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Handle "Remove from List" button
        holder.btnRemove.setOnClickListener(v -> removeRoomFromFavorites(roomId, position));
    }

    private void removeRoomFromFavorites(String roomId, int position) {
        String userId = new SessionManager(context).getUserId();

        if (userId != null) {
            db.collection("users").document(userId)
                    .update("favorites", FieldValue.arrayRemove(roomId))
                    .addOnSuccessListener(aVoid -> {
                        favoriteRoomIds.remove(position); // Remove roomId from the list
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, favoriteRoomIds.size());
                        Toast.makeText(context, "Room removed from favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to remove room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public int getItemCount() {
        return favoriteRoomIds.size(); // Return the count of room IDs
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvRoomName, tvRoomAddress, tvRoomPrice;
        private final ImageView ivRoomImage;
        private final Button btnRemove, btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomAddress = itemView.findViewById(R.id.tvRoomAddress);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
