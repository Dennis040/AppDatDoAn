package com.example.grab_demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.customer.adapter.ChatAdapter;
import com.example.grab_demo.customer.model.ChatMessage;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

// MainActivity.java
public class ChatBotActivity extends AppCompatActivity {
    private DialogflowBot dialogflowBot;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private Button sendButton;
    ImageButton btn_back_chatbot;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        // Khởi tạo DialogflowBot
        dialogflowBot = new DialogflowBot(this);

        // Khởi tạo views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        btn_back_chatbot = findViewById(R.id.btn_back_chatbot);
        btn_back_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Thiết lập RecyclerView
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Thêm tin nhắn chào mừng
//        chatAdapter.addMessage(new ChatMessage(
//                "Xin chào! Mình có thể giúp bạn tìm quán gà rán ngon. Bạn đang ở khu vực nào?",
//                false
//        ));

        // Xử lý sự kiện gửi tin nhắn
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                // Hiển thị tin nhắn người dùng
                chatAdapter.addMessage(new ChatMessage(message, true));

                // Gửi tin nhắn đến Dialogflow
                dialogflowBot.sendMessage(message, new DialogflowBot.BotResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        // Hiển thị phản hồi từ bot
                        chatAdapter.addMessage(new ChatMessage(response, false));
                        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

                        // Kiểm tra nếu có suggestion chips
                        if (response.contains("Bạn muốn biết thêm") ||
                                response.contains("đặt bàn không?")) {
                            showSuggestionChips();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ChatBotActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });

                // Xóa input
                messageInput.setText("");
            }
        });
    }

    private void showSuggestionChips() {
        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.removeAllViews();

        String[] suggestions = {"Đặt bàn", "Xem menu", "Tìm quán khác"};
        for (String suggestion : suggestions) {
            Chip chip = new Chip(this);
            chip.setText(suggestion);
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setOnClickListener(v -> {
                messageInput.setText(suggestion);
                sendButton.performClick();
            });
            chipGroup.addView(chip);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogflowBot.closeSession();
    }
}