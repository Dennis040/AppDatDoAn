package com.example.grab_demo.customer.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.ChatbotHelper;
import com.example.grab_demo.customer.adapter.ChatAdapter;
import com.example.grab_demo.customer.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private ChatbotHelper chatbotHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Khởi tạo views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Thiết lập RecyclerView
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        chatbotHelper = new ChatbotHelper();

        // Thêm tin nhắn chào mừng
        addBotMessage("Xin chào! Tôi có thể giúp bạn tìm kiếm món ăn hoặc giải đáp thắc mắc. Bạn muốn biết thông tin gì?");

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            // Hiển thị tin nhắn người dùng
            addUserMessage(message);

            // Xử lý và hiển thị phản hồi của bot
            String response = chatbotHelper.processMessage(message);
            addBotMessage(response);

            // Xóa input
            messageInput.setText("");
        }
    }

    private void addUserMessage(String message) {
        chatAdapter.addMessage(new ChatMessage(message, true));
        scrollToBottom();
    }

    private void addBotMessage(String message) {
        chatAdapter.addMessage(new ChatMessage(message, false));
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }
}