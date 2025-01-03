package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.review.Review;
import com.app.homiefy.review.ReviewAdapter;
import com.app.homiefy.room.Room;
import com.app.homiefy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomDetails extends AppCompatActivity {

    private TextView tvRoomName, tvRentPrice, tvRoomDescription, tvHouseRules,
            tvDeposit, tvOtherFees, tvContactInfo, tvArea, tvAddress, tvStartDate, tvEndDate, tvRoomStatus;
    private ImageView ivRoomImage, ivReport;
    private MaterialButton btnMessage, btnScheduleVisit, btnRateRoom, btnRentRoom;
    private ChipGroup chipGroupAmenities;
    private SessionManager sessionManager; // Declare the instance
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;


    private FirebaseFirestore db;
    private String roomId, ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance("homeify");

        // Get roomId from intent
        roomId = getIntent().getStringExtra("roomId");

        if (roomId == null || roomId.isEmpty()) {
            Toast.makeText(this, "Room ID not provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        sessionManager = new SessionManager(this);

        // Initialize UI components
        initializeUI();
        getRoomOwner();
        fetchRoomDetails();
        setupBackButton();
        setupMenuListeners();
        setupFavoriteButton();
        setupActionButtons();
        fetchReviewsAndRatings();
    }

    private void initializeUI() {
        tvRoomName = findViewById(R.id.tvRoomName);
        tvRentPrice = findViewById(R.id.tvRentPrice);
        tvRoomStatus = findViewById(R.id.tvRoomStatus);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvHouseRules = findViewById(R.id.tvHouseRules);
        tvDeposit = findViewById(R.id.tvDeposit);
        tvOtherFees = findViewById(R.id.tvOtherFees);
        tvContactInfo = findViewById(R.id.tvContactInfo);
        tvArea = findViewById(R.id.tvArea);
        tvAddress = findViewById(R.id.tvAddress);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        ivRoomImage = findViewById(R.id.ivRoomImage);
        ivReport = findViewById(R.id.ivReport);
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);

        btnMessage = findViewById(R.id.btnMessage);
        btnScheduleVisit = findViewById(R.id.btnScheduleVisit);
        btnRateRoom = findViewById(R.id.btnRateRoom);
        btnRentRoom = findViewById(R.id.btnRentRoom);

        btnMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatDetailActivity.class);
            intent.putExtra("otherUserId", ownerId); // Pass roomId if needed
            startActivity(intent);
        });

        btnScheduleVisit.setOnClickListener(v -> {
            String roomAddress = tvAddress.getText().toString();
            String roomPrice = tvRentPrice.getText().toString();
            String roomName = tvRoomName.getText().toString();
            Intent intent = new Intent(RoomDetails.this, RoomViewingAppointment.class);
            intent.putExtra("roomId", roomId);
            intent.putExtra("roomName", roomName);
            intent.putExtra("ownerId", ownerId);
            intent.putExtra("roomAddress", roomAddress);
            intent.putExtra("roomRentPrice", roomPrice);
            startActivity(intent);
        });

        rvReviews = findViewById(R.id.rvReviews);

        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setAdapter(reviewAdapter);

    }


    private void setupActionButtons() {
        Button btnNearbyInfo = findViewById(R.id.btnNearbyInfo);
        btnNearbyInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, AreaInformation.class);
            intent.putExtra("roomId", roomId); // Pass roomId if needed
            startActivity(intent);
        });

        // Set up Rent Room button
        btnRentRoom.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, DepositSystem.class);
            intent.putExtra("roomId", roomId);
            intent.putExtra("ownerId", ownerId); // Passing ownerId to the renting page
            startActivity(intent);
        });

        // Kiểm tra nếu người dùng đã thuê phòng này thì hiện nút Rate Room
        String userId = sessionManager.getUserId(); // Lấy userId từ SessionManager
        if (userId != null) {
            db.collection("deposits")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("roomId", roomId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // User đã thuê phòng này -> hiện nút Rate Room
                            btnRateRoom.setVisibility(View.VISIBLE);
                            btnRateRoom.setOnClickListener(v -> {
                                Intent intent = new Intent(RoomDetails.this, ReviewsAndRatings.class);
                                intent.putExtra("roomId", roomId);
                                startActivity(intent);
                            });
                        } else {
                            // User chưa thuê phòng này -> ẩn nút Rate Room
                            btnRateRoom.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi kiểm tra trạng thái thuê phòng: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Nếu user chưa đăng nhập, ẩn nút Rate Room
            btnRateRoom.setVisibility(View.GONE);
        }
    }


    private void setupFavoriteButton() {
        ImageButton btnFavorite = findViewById(R.id.btnFavorite);

        // Check if user is logged in
        String userId = sessionManager.getUserId();
        if (userId == null) {
            btnFavorite.setOnClickListener(v -> {
                Toast.makeText(this, "Please log in to add favorites!", Toast.LENGTH_SHORT).show();
            });
            return;
        }

        // Check if room is already in favorites
        db.collection("users").document(userId).get()
                .addOnSuccessListener(userDocument -> {
                    List<String> favorites = (List<String>) userDocument.get("favorites");
                    boolean isAlreadyFavorite = favorites != null && favorites.contains(roomId);

                    // Update button state and click listener based on current favorite status
                    updateFavoriteButtonState(btnFavorite, isAlreadyFavorite);

                    btnFavorite.setOnClickListener(v -> {
                        if (isAlreadyFavorite) {
                            // Remove from favorites
                            db.collection("users").document(userId)
                                    .update("favorites", FieldValue.arrayRemove(roomId))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Removed from favorites!", Toast.LENGTH_SHORT).show();
                                        updateFavoriteButtonState(btnFavorite, false);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to remove from favorites: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Add to favorites
                            db.collection("users").document(userId)
                                    .update("favorites", FieldValue.arrayUnion(roomId))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                                        updateFavoriteButtonState(btnFavorite, true);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add to favorites: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking favorites: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFavoriteButtonState(ImageButton btnFavorite, boolean isFavorite) {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_outline);
        }
    }

    private void fetchRoomDetails() {
        db.collection("rooms").document(roomId).get().
                addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Room room = documentSnapshot.toObject(Room.class);

                        if (room != null) {
                            tvRoomName.setText(room.getRoomName());
                            tvRentPrice.setText(formatCurrency(room.getRentPrice()) + "/month");
                            tvRoomDescription.setText(room.getDescription() != null ?
                                    room.getDescription() : "No description available");
                            tvHouseRules.setText(room.getRules() != null ?
                                    room.getRules() : "No rules specified");
                            tvDeposit.setText("Deposit: " + formatCurrency(room.getDeposit()));
                            tvOtherFees.setText("Other expenses: " + (room.getOtherFees() != null ?
                                    room.getOtherFees() : "Not specified"));
                            tvContactInfo.setText("Contact: " + room.getContactInfo());
                            tvArea.setText(room.getArea() != null ? room.getArea() + " m²" : "Area not specified");
                            tvAddress.setText(room.getAddress() != null ? room.getAddress() : "Address not specified");
                            tvStartDate.setText("Start Date: " + (room.getStartDate() != null ? room.getStartDate() : "--/--/----"));
                            tvEndDate.setText("End Date: " + (room.getEndDate() != null ? room.getEndDate() : "--/--/----"));

                            // Check room rental status
                            if (room.isRented()) {
                                tvRoomStatus.setText("Not Available");
                                btnScheduleVisit.setVisibility(View.GONE);  // Ẩn nút Book Viewing
                                btnRentRoom.setVisibility(View.GONE);  // Ẩn nút Rent// Room is rented
                            } else {
                                tvRoomStatus.setText("Available");  // Room is available
                                btnScheduleVisit.setVisibility(View.VISIBLE);  // Ẩn nút Book Viewing
                                btnRentRoom.setVisibility(View.VISIBLE);  // Ẩn nút Rent
                            }


                            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(room.getImageUrl())
                                        .placeholder(R.drawable.placeholder_image)
                                        .into(ivRoomImage);
                            }

                            // Update ChipGroup for amenities
                            updateAmenities(room.getAmenities());
                        }
                    } else {
                        Toast.makeText(this, "Room not found!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to fetch room details: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void updateAmenities(List<String> amenities) {
        // Clear all existing chips
        chipGroupAmenities.removeAllViews();

        if (amenities == null || amenities.isEmpty()) {
            Toast.makeText(this, "No amenities found!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dynamically create chips for amenities from the database
        for (String amenity : amenities) {
            Chip chip = new Chip(this);
            chip.setText(amenity);
            chip.setCheckable(false); // Optional: Make chips non-checkable for display purposes
            chip.setChipBackgroundColorResource(R.color.chip_background); // Customize as needed
            chip.setTextColor(getResources().getColor(R.color.black)); // Customize as needed
            chip.setChipIconResource(getAmenityIcon(amenity)); // Set icon dynamically if applicable
            chip.setIconStartPadding(8f);
            chipGroupAmenities.addView(chip);
        }
    }

    private int getAmenityIcon(String amenity) {
        switch (amenity.toLowerCase()) {
            case "wi-fi":
                return R.drawable.ic_wifi;
            case "air conditioner":
                return R.drawable.ic_ac;
            case "parking":
                return R.drawable.ic_parking;
            case "fridge":
                return R.drawable.ic_fridge;
            case "washing machine":
                return R.drawable.ic_wm;
            default:
                return R.drawable.ic_placeholder; // Default icon for unknown amenities
        }
    }

    private String formatCurrency(String price) {
        try {
            double priceValue = Double.parseDouble(price);
            return String.format(Locale.US, "%,.0f VND", priceValue);
        } catch (NumberFormatException e) {
            return price + " VND";
        }
    }

    private void getRoomOwner() {
        db.collection("rooms").document(roomId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    ownerId = documentSnapshot.getString("userId");
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to fetch room owner: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void fetchReviewsAndRatings() {
        db.collection("reviews")
                .whereEqualTo("roomId", roomId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewList.clear();
                        float totalRating = 0;
                        int reviewCount = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            if (review != null) {
                                totalRating += review.getRating();
                                reviewCount++;

                                // Lấy userId của người đánh giá
                                String userId = review.getUserId();
                                if (userId != null && !userId.isEmpty()) {
                                    // Truy vấn Firestore để lấy thông tin người dùng
                                    db.collection("users").document(userId)
                                            .get()
                                            .addOnSuccessListener(userDocument -> {
                                                if (userDocument.exists()) {
                                                    String userName = userDocument.getString("name");
                                                    review.setReviewerName(userName != null ? userName : "Anonymous");
                                                } else {
                                                    review.setReviewerName("Anonymous");
                                                }
                                                reviewList.add(review);
                                                reviewAdapter.notifyDataSetChanged(); // Cập nhật giao diện sau khi có tên
                                            })
                                            .addOnFailureListener(e -> {
                                                review.setReviewerName("Anonymous");
                                                reviewList.add(review);
                                                reviewAdapter.notifyDataSetChanged(); // Cập nhật giao diện ngay cả khi lỗi
                                            });
                                } else {
                                    review.setReviewerName("Anonymous");
                                    reviewList.add(review);
                                    reviewAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error fetching reviews: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupMenuListeners() {
        ImageView ivChat = findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, ProfileActivity.class);
            startActivity(intent);
        });

        ivReport.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetails.this, RoomReport.class);
            intent.putExtra("roomId", roomId);
            intent.putExtra("ownerId", ownerId);
            startActivity(intent);
        });
    }
}
