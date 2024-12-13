package com.app.homiefy;

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

        currentUserId = "userId1";
//        currentUserId = sessionManager.getUserId();

        recyclerView = findViewById(R.id.chatListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, currentUserId);
        recyclerView.setAdapter(chatAdapter);

        loadChats(); // Gọi phương thức để tải chat
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

                            // Tạo chat object
                            Chat chat = new Chat(chatId, "", lastMessage, lastMessageSenderId, lastMessageTimestamp, isRead);
                            chatList.add(chat);
                            chatAdapter.notifyDataSetChanged(); // Cập nhật Adapter sau khi thêm từng chat
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