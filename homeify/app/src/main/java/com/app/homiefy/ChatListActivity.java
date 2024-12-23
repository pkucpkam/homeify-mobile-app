package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.chat.Chat;
import com.app.homiefy.chat.ChatAdapter;
import com.app.homiefy.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private SessionManager sessionManager;
    private String currentUserId; // Lưu ID người dùng hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionManager = new SessionManager(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        currentUserId = sessionManager.getUserId();

        recyclerView = findViewById(R.id.chatListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        recyclerView.setAdapter(chatAdapter);

        // Tạo interface để xử lý click event
        ChatAdapter.OnItemClickListener listener = chatId -> {
            // Lấy Chat từ chatList dựa trên chatId
            Chat selectedChat = null;
            for (Chat chat : chatList) {
                if (chat.getChatId().equals(chatId)) {
                    selectedChat = chat;
                    break;
                }
            }

            if (selectedChat != null) {
                Intent intent = new Intent(ChatListActivity.this, ChatDetailActivity.class);
                intent.putExtra("chatId", selectedChat.getChatId());
                intent.putExtra("otherUserId", selectedChat.getOtherUserId());
                intent.putExtra("otherUserName", selectedChat.getOtherUser());
                startActivity(intent);
            }
        };


        chatAdapter = new ChatAdapter(chatList, currentUserId, listener);
        recyclerView.setAdapter(chatAdapter);

        loadChats();
        setupMenuListeners();
        setupBackButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }


    private void loadChats() {
        DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId).child("chats");

        // Làm sạch danh sách trước khi tải dữ liệu
        chatList.clear();
        chatAdapter.notifyDataSetChanged();

        userChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ChatListActivity", "DataSnapshot (userChatsRef): " + dataSnapshot.toString());

                if (!dataSnapshot.exists()) {
                    chatAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();

                    if (chatId == null) continue;

                    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot chatDataSnapshot) {
                            if (!chatDataSnapshot.exists()) return;

                            // Lấy thông tin tin nhắn cuối cùng
                            String lastMessage = "";
                            String lastMessageSenderId = "";
                            String lastMessageTimestamp = "";
                            boolean isRead = true;

                            DataSnapshot lastMessageSnapshot = chatDataSnapshot.child("lastMessage");
                            if (lastMessageSnapshot.exists()) {
                                lastMessage = lastMessageSnapshot.child("text").getValue(String.class);
                                lastMessageSenderId = lastMessageSnapshot.child("senderId").getValue(String.class);
                                lastMessageTimestamp = lastMessageSnapshot.child("timestamp").getValue(String.class);
                                Boolean readValue = lastMessageSnapshot.child("read").getValue(Boolean.class);
                                isRead = readValue != null ? readValue : true;
                            }

                            // Lấy tên người dùng khác
                            String otherUserId = "";
                            String otherUserName = "";
                            DataSnapshot usersSnapshot = chatDataSnapshot.child("users");
                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                if (userId != null && !userId.equals(currentUserId)) {
                                    otherUserId = userId;
                                    otherUserName = userSnapshot.child("username").getValue(String.class);
                                    break;
                                }
                            }

                            // Kiểm tra trùng lặp trước khi thêm vào danh sách
                            boolean isChatExists = false;
                            for (Chat existingChat : chatList) {
                                if (existingChat.getChatId().equals(chatId)) {
                                    isChatExists = true;
                                    break;
                                }
                            }

                            // Chỉ thêm chat mới nếu chưa tồn tại
                            if (!isChatExists && !otherUserName.isEmpty()) {
                                Chat chat = new Chat(
                                        chatId,
                                        otherUserName,
                                        otherUserId,
                                        otherUserName,
                                        lastMessage,
                                        lastMessageSenderId,
                                        lastMessageTimestamp,
                                        isRead
                                );
                                chatList.add(chat);
                            }

                            // Cập nhật adapter sau khi thêm tất cả dữ liệu
                            chatAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ChatListActivity", "Database error: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatListActivity", "Database error: " + databaseError.getMessage());
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
            Intent intent = new Intent(ChatListActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ImageView ivPostRoom = findViewById(R.id.ivPostRoom);
        ivPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, PostingRoom.class);
            startActivity(intent);
        });

        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}