package com.example.grab_demo.customer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// ChatbotHelper.java
public class ChatbotHelper {
    private static final String[] FOOD_SUGGESTIONS = {
            "phở", "bún bò", "cơm tấm", "bánh mì", "bún chả",
            "gỏi cuốn", "chả giò", "bánh xèo", "bún riêu", "mì quảng"
    };

    private static final Map<String, String[]> FOOD_CATEGORIES = new HashMap<String, String[]>() {{
        put("món nước", new String[]{"phở", "bún bò", "bún riêu", "mì quảng"});
        put("món khô", new String[]{"cơm tấm", "bánh mì"});
        put("món cuốn", new String[]{"gỏi cuốn", "chả giò"});
    }};

    public String processMessage(String userMessage) {
        userMessage = userMessage.toLowerCase();

        // Xử lý các câu hỏi thường gặp
        if (userMessage.contains("chào") || userMessage.contains("xin chào")) {
            return "Xin chào! Tôi có thể giúp gì cho bạn? Bạn muốn tìm món ăn gì?";
        }

        if (userMessage.contains("đề xuất") || userMessage.contains("gợi ý")) {
            return "Tôi đề xuất bạn thử " + getRandomFood() + ". Bạn nghĩ sao?";
        }

        if (userMessage.contains("món nước")) {
            return "Các món nước ngon có: " + String.join(", ", FOOD_CATEGORIES.get("món nước"));
        }

        if (userMessage.contains("món khô")) {
            return "Các món khô ngon có: " + String.join(", ", FOOD_CATEGORIES.get("món khô"));
        }

        if (userMessage.contains("món cuốn")) {
            return "Các món cuốn ngon có: " + String.join(", ", FOOD_CATEGORIES.get("món cuốn"));
        }

        // Xử lý tìm kiếm món ăn cụ thể
        for (String food : FOOD_SUGGESTIONS) {
            if (userMessage.contains(food)) {
                return "Bạn muốn biết thêm thông tin về " + food + " phải không? " +
                        "Tôi có thể giới thiệu một số quán ngon.";
            }
        }

        return "Xin lỗi, tôi không hiểu ý bạn. Bạn có thể hỏi về các món ăn cụ thể hoặc yêu cầu gợi ý nhé!";
    }

    private String getRandomFood() {
        int index = new Random().nextInt(FOOD_SUGGESTIONS.length);
        return FOOD_SUGGESTIONS[index];
    }
}