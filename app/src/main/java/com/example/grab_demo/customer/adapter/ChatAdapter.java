package com.example.grab_demo.customer.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

// ChatAdapter.java
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> messages = new ArrayList<>();
    private Context context;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        View messageContainer;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            // Align messages based on sender
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) messageContainer.getLayoutParams();
            if (message.isUser()) {
                params.gravity = Gravity.END;
                messageContainer.setBackgroundResource(R.drawable.user);
            } else {
                params.gravity = Gravity.START;
                messageContainer.setBackgroundResource(R.drawable.icon_bot);
            }
            messageContainer.setLayoutParams(params);
        }
    }
}