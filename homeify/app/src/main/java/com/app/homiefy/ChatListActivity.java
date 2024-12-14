package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
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
                            String lastMessage = chatDataSnapshot.child("lastMessage/text").getValue(String.class);
                            String lastMessageSenderId = chatDataSnapshot.child("lastMessage/senderId").getValue(String.class);
                            String lastMessageTimestamp = chatDataSnapshot.child("lastMessage/timestamp").getValue(String.class);
                            boolean isRead = chatDataSnapshot.child("lastMessage/read").getValue(Boolean.class);

                            DataSnapshot usersSnapshot = chatDataSnapshot.child("users");
                            String otherUserName = "";
                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                if (!userId.equals(currentUserId)) {
                                    otherUserName = userSnapshot.child("userName").getValue(String.class);
                                    break;
                                }
                            }
                            // Tạo chat object
                            Chat chat = new Chat(chatId, "", otherUserName ,lastMessage, lastMessageSenderId, lastMessageTimestamp, isRead);
                            chatList.add(chat);
                            chatAdapter.notifyDataSetChanged();
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
}