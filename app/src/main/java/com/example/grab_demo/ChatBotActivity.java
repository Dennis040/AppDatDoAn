package com.example.grab_demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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

import com.example.grab_demo.customer.activity.StoreListActivity;
import com.example.grab_demo.customer.activity.StoreMenuActivity;
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
    int reponse;
    ChipGroup chipGroup;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        // Khởi tạo DialogflowBot
        //dialogflowBot = new DialogflowBot(this);

        // Khởi tạo views
        chipGroup = findViewById(R.id.chipGroup);
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
                        if (response.contains("Chào bạn! Mình có thể gợi ý cho bạn một số quán ngon:\n" +
                                "- Gà rán\n" +
                                "- Burger\n" +
                                "- Cơm")) {
                            reponse = 1;
                            showSuggestionChips(reponse);
                        }
                        if (response.contains("- Quán gà rán được nhiều đánh giá nhất: KFC - [HTP]. Bạn có muốn chuyển sang quán đó để thử không")) {
                            // Hiển thị hộp thoại xác nhận chuyển qua shop
                            chipGroup.removeAllViews();
                            new AlertDialog.Builder(ChatBotActivity.this)
                                    .setTitle("Chuyển sang quán KFC")
                                    .setMessage("Bạn có muốn chuyển sang quán KFC để đặt món không?")
                                    .setPositiveButton("Chuyển", (dialog, which) -> {
                                        // Xử lý chuyển sang shop KFC, ví dụ mở activity khác hoặc thực hiện hành động chuyển
//                                        Intent intent = new Intent(ChatBotActivity.this, KFCShopActivity.class);
//                                        startActivity(intent);
                                        Intent intent = new Intent(ChatBotActivity.this, StoreMenuActivity.class);
                                        intent.putExtra("store_id", 2);  // Truyền storeId
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                                    .show();
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

    private void showSuggestionChips(int  reponse) {
        chipGroup.removeAllViews();
        if(reponse == 1) {
            String[] suggestions = {"Gà rán", "Burger", "Cơm"};
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogflowBot.closeSession();
    }
}