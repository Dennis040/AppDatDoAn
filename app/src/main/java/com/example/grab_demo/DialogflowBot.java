package com.example.grab_demo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class DialogflowBot {
    private static final String PROJECT_ID = "foodrecommendation-wdty"; // Thay bằng Project ID của bạn
    private SessionsClient sessionsClient;
    private SessionName sessionName;

    // Định nghĩa interface callback
    public interface BotResponseCallback {
        void onResponse(String response);

        void onError(String error);
    }

    public DialogflowBot(Context context) {
        try {
            // Đọc file credentials
            InputStream stream = context.getResources().openRawResource(R.raw.chatbot);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

            // Khởi tạo session
            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(PROJECT_ID, UUID.randomUUID().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, final BotResponseCallback callback) {
        if (sessionsClient == null) {
            callback.onError("Dialogflow client not initialized");
            return;
        }

        // Thực hiện trong background thread
        new Thread(() -> {
            try {
                // Tạo query input
                TextInput.Builder textInput = TextInput.newBuilder()
                        .setText(message)
                        .setLanguageCode("vi"); // Đặt ngôn ngữ Tiếng Việt

                QueryInput queryInput = QueryInput.newBuilder()
                        .setText(textInput)
                        .build();

                // Tạo request
                DetectIntentRequest request = DetectIntentRequest.newBuilder()
                        .setSession(sessionName.toString())
                        .setQueryInput(queryInput)
                        .build();

                // Gửi request và nhận response
                DetectIntentResponse response = sessionsClient.detectIntent(request);

                // Xử lý response trong UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response != null && response.getQueryResult() != null) {
                        String botReply = response.getQueryResult().getFulfillmentText();
                        if (!botReply.isEmpty()) {
                            callback.onResponse(botReply);
                        } else {
                            callback.onError("No response from bot");
                        }
                    } else {
                        callback.onError("Invalid response from bot");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DialogflowBot", "Error: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error: " + e.getMessage())
                );
            }
        }).start();
    }

    public void closeSession() {
        if (sessionsClient != null) {
            sessionsClient.close();
        }
    }
}
