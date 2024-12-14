package com.app.homiefy.chat;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.R;

import java.util.List;

// ChatAdapter.java
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private String currentUserId;
    private OnItemClickListener listener; // Added this line

    // Interface để xử lý click event
    public interface OnItemClickListener {
        void onItemClick(String chatId);
    }

    public ChatAdapter(List<Chat> chatList, String currentUserId, OnItemClickListener listener) {
        this.chatList = chatList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.chatUserName.setText(chat.getOtherUser());

        String lastMessage;
        if (chat.getLastMessageSenderId().equals(currentUserId)) {
            lastMessage = chat.getLastMessage();
        } else {
            lastMessage = chat.isRead() ? chat.getLastMessage() : chat.getLastMessage().toUpperCase();
        }
        holder.chatLastMessage.setText(lastMessage);
        holder.chatTimestamp.setText(chat.getLastMessageTimestamp());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chat.getChatId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatUserName, chatLastMessage, chatTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatUserName = itemView.findViewById(R.id.chatUserName);
            chatLastMessage = itemView.findViewById(R.id.chatLastMessage);
            chatTimestamp = itemView.findViewById(R.id.chatTimestamp);
        }
    }
}