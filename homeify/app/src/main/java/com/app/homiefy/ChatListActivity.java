package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
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
            Intent intent = new Intent(ChatListActivity.this, ChatDetailActivity.class);
            intent.putExtra("chatId", chatId);
            startActivity(intent);
        };

        chatAdapter = new ChatAdapter(chatList, currentUserId, listener);
        recyclerView.setAdapter(chatAdapter);

        loadChats();
        setupMenuListeners();
        setupBackButton();
    }

    private void loadChats() {
        DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("chats");
        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();
                    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot chatDataSnapshot) {
                            if (!chatDataSnapshot.exists()) {
                                return;
                            }

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

                            // Lấy thông tin người dùng khác
                            String otherUserName = "";
                            DataSnapshot usersSnapshot = chatDataSnapshot.child("users");
                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                if (userId != null && !userId.equals(currentUserId)) {
                                    // Lấy userName từ node users trong chat
                                    String userName = userSnapshot.child("userName").getValue(String.class);
                                    if (userName != null && !userName.isEmpty()) {
                                        otherUserName = userName;
                                        break;
                                    } else {
                                        // Nếu không có userName trong chat, lấy từ node users gốc
                                        DatabaseReference userRef = FirebaseDatabase.getInstance()
                                                .getReference("users").child(userId);
                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                                                String userName = userDataSnapshot.child("userName").getValue(String.class);
                                                if (userName != null) {
                                                    Chat chat = new Chat();
                                                    chatList.add(chat);
                                                    chatAdapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Handle error
                                            }
                                        });
                                        return;
                                    }
                                }
                            }

                            // Nếu tìm thấy otherUserName trực tiếp từ chat
                            if (!otherUserName.isEmpty()) {
                                Chat chat = new Chat(
                                        chatId,
                                        "",
                                        otherUserName,
                                        lastMessage,
                                        lastMessageSenderId,
                                        lastMessageTimestamp,
                                        isRead
                                );
                                chatList.add(chat);
                                chatAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error here
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error here
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