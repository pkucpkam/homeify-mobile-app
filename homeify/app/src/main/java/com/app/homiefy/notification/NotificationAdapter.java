package com.app.homiefy.notification;

import android.content.Context;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvContent.setText(notification.getContent());
        holder.tvTime.setText(formatTime(notification.getTimestamp()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private String formatTime(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = sdf.parse(timestamp);

            // Format relative time
            long diff = System.currentTimeMillis() - date.getTime();
            if (diff < 24 * 60 * 60 * 1000) {
                return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
            } else {
                return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
            }
        } catch (ParseException e) {
            return timestamp;
        }
    }

    public void updateNotifications(List<Notification> newNotifications) {
        notifications.clear();
        notifications.addAll(newNotifications);
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotification;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;

        NotificationViewHolder(View itemView) {
            super(itemView);
            ivNotification = itemView.findViewById(R.id.ivNotification);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
