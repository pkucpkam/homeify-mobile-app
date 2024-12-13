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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private String currentUserId;


    public ChatAdapter(List<Chat> chatList, String currentUserId) {
        this.chatList = chatList;
        this.currentUserId = currentUserId;
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
        holder.chatUserName.setText(chat.getUserName());

        String lastMessage;
        if (chat.getLastMessageSenderId().equals(currentUserId)) {
            lastMessage = chat.getLastMessage();
        } else {
            lastMessage = chat.isRead() ? chat.getLastMessage() : chat.getLastMessage().toUpperCase(); // In hoa nếu chưa đọc
        }

        holder.chatLastMessage.setText(lastMessage);
        holder.chatTimestamp.setText(chat.getLastMessageTimestamp());
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