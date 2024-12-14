package com.app.homiefy;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.message.Message;
import com.app.homiefy.message.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private String chatId;
    private String currentUserId;
    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.messageInput);
        btnSend = findViewById(R.id.btnSend); // Changed from btnSend to sendButton to match layout

        // Get chatId from intent
//        chatId = getIntent().getStringExtra("chatId");
        chatId = "chatId2";
        currentUserId = "userId1";
//        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("ChatDetail", "Current User ID: " + currentUserId); // Thêm log này

        // Initialize RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Initialize Firebase Reference
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        loadMessages();
        setupSendButton();
    }

    private void loadMessages() {
        chatRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String senderId = snapshot.child("senderId").getValue(String.class);
                String text = snapshot.child("text").getValue(String.class);
                String timestamp = snapshot.child("timestamp").getValue(String.class);

                // Thêm log để debug
                Log.d("ChatDetail", "Message sender ID: " + senderId);
                Log.d("ChatDetail", "Current user ID: " + currentUserId);
                Log.d("ChatDetail", "Is sender current user? " + senderId.equals(currentUserId));

                if (senderId != null && text != null && timestamp != null) {
                    Message message = new Message(
                            snapshot.getKey(),
                            senderId,
                            text,
                            timestamp
                    );

                    // Thêm tin nhắn vào danh sách
                    messageList.add(message);

                    // Thông báo cập nhật dữ liệu
                    messageAdapter.notifyItemInserted(messageList.size() - 1);

                    // Cuộn xuống cuối
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void setupSendButton() {
        btnSend.setOnClickListener(v -> {
            String messageText = etMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                etMessage.setText("");
            }
        });
    }

    private void sendMessage(String messageText) {
        String messageId = chatRef.child("messages").push().getKey();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                .format(new Date());

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", currentUserId);
        messageData.put("text", messageText);
        messageData.put("timestamp", timestamp);

        // Update messages
        chatRef.child("messages").child(messageId).setValue(messageData);

        // Update last message
        Map<String, Object> lastMessageData = new HashMap<>(messageData);
        lastMessageData.put("read", false);
        chatRef.child("lastMessage").setValue(lastMessageData);
    }
}