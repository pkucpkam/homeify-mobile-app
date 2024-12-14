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
import com.google.firebase.firestore.FirebaseFirestore;

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
    private String otherUserId; // ID của người nhận
    private DatabaseReference chatRef;
    private DatabaseReference userRef;
    private FirebaseFirestore firestore;
    private String currentUserName;
    private String otherUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.messageInput);
        btnSend = findViewById(R.id.btnSend);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("otherUserId");

        firestore = FirebaseFirestore.getInstance("homeify");
        fetchUserNames();

        // Initialize RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Khởi tạo Firebase references
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("users");

        // Tìm chatId hiện có hoặc để null
        findExistingChat();
        setupSendButton();
    }

    private void fetchUserNames() {
        // Fetch current user's name
        firestore.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentUserName = documentSnapshot.getString("username");
                    firestore.collection("users").document(otherUserId)
                            .get()
                            .addOnSuccessListener(otherUserDoc -> {
                                otherUserName = otherUserDoc.getString("username");

                                findExistingChat();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ChatDetail", "Error fetching other user's name", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatDetail", "Error fetching current user's name", e);
                });
    }

    private void findExistingChat() {
        userRef.child(currentUserId).child("chats").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Kiểm tra trong danh sách chat của user hiện tại
                for (DataSnapshot chatSnapshot : task.getResult().getChildren()) {
                    String existingChatId = chatSnapshot.getKey();
                    checkChatParticipants(existingChatId);
                }
            }
        });
    }

    private void checkChatParticipants(String existingChatId) {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        chatsRef.child(existingChatId).child("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Object> users = (Map<String, Object>) task.getResult().getValue();
                if (users != null && users.containsKey(currentUserId) && users.containsKey(otherUserId)) {
                    // Tìm thấy chat hiện có
                    chatId = existingChatId;
                    chatRef = chatsRef.child(chatId);
                    loadMessages();
                }
            }
        });
    }

    private void loadMessages() {
        if (chatRef == null) return;

        chatRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String senderId = snapshot.child("senderId").getValue(String.class);
                String text = snapshot.child("text").getValue(String.class);
                String timestamp = snapshot.child("timestamp").getValue(String.class);

                if (senderId != null && text != null && timestamp != null) {
                    Message message = new Message(
                            snapshot.getKey(),
                            senderId,
                            text,
                            timestamp
                    );
                    messageList.add(message);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
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
                if (chatId == null) {
                    // Tạo chat mới nếu chưa có
                    createNewChat(messageText);
                } else {
                    sendMessage(messageText);
                }
                etMessage.setText("");
            }
        });
    }

    private void createNewChat(String firstMessage) {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        chatId = chatsRef.push().getKey();
        chatRef = chatsRef.child(chatId);

        // Tạo cấu trúc chat mới
        Map<String, Object> chatData = new HashMap<>();

        // Thêm users vào chat với username
        Map<String, Object> users = new HashMap<>();
        Map<String, Object> currentUserData = new HashMap<>();
        Map<String, Object> otherUserData = new HashMap<>();

        currentUserData.put("username", currentUserName);
        otherUserData.put("username", otherUserName);

        users.put(currentUserId, currentUserData);
        users.put(otherUserId, otherUserData);
        chatData.put("users", users);

        // Lưu chatId vào profile của cả 2 users
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/users/" + currentUserId + "/chats/" + chatId, true);
        userUpdates.put("/users/" + otherUserId + "/chats/" + chatId, true);

        // Thực hiện update
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // First update chat data
        chatRef.setValue(chatData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Then update user references
                rootRef.updateChildren(userUpdates).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        // Sau khi tạo chat thành công, gửi tin nhắn đầu tiên
                        sendMessage(firstMessage);
                    }
                });
            }
        });
    }

    private void sendMessage(String messageText) {
        if (chatRef == null) return;

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