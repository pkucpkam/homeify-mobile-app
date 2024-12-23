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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        String formattedTime = formatTimestamp(chat.getLastMessageTimestamp());
        holder.chatTimestamp.setText(formattedTime);

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

    private String formatTimestamp(String rawTimestamp) {
        try {
            // Định dạng ban đầu của timestamp (giả sử từ server là ISO 8601)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inputFormat.setLenient(false);

            // Định dạng mong muốn
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            // Parse và format lại thời gian
            Date date = inputFormat.parse(rawTimestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return rawTimestamp; // Trả về thời gian gốc nếu lỗi
        }
    }

}