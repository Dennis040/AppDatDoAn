package com.example.grab_demo.deliver.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grab_demo.R;
import com.example.grab_demo.database.ConnectionClass;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class StatisticalActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "StatisticalPrefs";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_ACCUMULATED_TIME = "accumulatedTime";
    private static final int COLOR_GREEN = Color.parseColor("#00B14F");
    private static final int COLOR_RED = Color.RED;

    private TextView tv_thu_nhap_chuyen_di, tv_da_thu_tien_mat, tv_thoi_gian_truc_tuyen, tv_so_chuyen_di;
    private View Color_report;
    private long startTime;
    private long accumulatedTime = 0;
    private String userId;
    private Handler handler = new Handler();

    // Runnable để cập nhật thời gian hoạt động
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateActivityTime();
            handler.postDelayed(this, 1000);
        }
    };

    // Runnable để kiểm tra trạng thái flag
    private Runnable checkFlagRunnable = new Runnable() {
        @Override
        public void run() {
            new CheckFlagTask().execute(userId);
            handler.postDelayed(this, 5000); // Kiểm tra mỗi 5 giây
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);
        Color_report = findViewById(R.id.Color_report);
        tv_thoi_gian_truc_tuyen = findViewById(R.id.tv_thoi_gian_truc_tuyen);
        tv_so_chuyen_di = findViewById(R.id.tv_so_chuyen_di);
        tv_thu_nhap_chuyen_di = findViewById(R.id.tv_thu_nhap_chuyen_di);
        tv_da_thu_tien_mat = findViewById(R.id.tv_da_thu_tien_mat);

        // Nhận Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user_id")) {
            userId = intent.getStringExtra("user_id");
        }

        // Load thời gian bắt đầu và thời gian tích lũy từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        startTime = prefs.getLong(KEY_START_TIME, System.currentTimeMillis());
        accumulatedTime = prefs.getLong(KEY_ACCUMULATED_TIME, 0);

        // Bắt đầu Runnable cập nhật thời gian và kiểm tra flag
        handler.post(updateTimeRunnable);
        handler.post(checkFlagRunnable);

        // Thực hiện truy vấn ban đầu
        if (userId != null) {
            new QueryTask().execute(userId);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putLong(KEY_START_TIME, System.currentTimeMillis());
        editor.putLong(KEY_ACCUMULATED_TIME, accumulatedTime + (System.currentTimeMillis() - startTime));
        editor.apply();

        // Dừng Runnable khi Activity dừng
        handler.removeCallbacks(updateTimeRunnable);
        handler.removeCallbacks(checkFlagRunnable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        startTime = prefs.getLong(KEY_START_TIME, System.currentTimeMillis());
        accumulatedTime = prefs.getLong(KEY_ACCUMULATED_TIME, accumulatedTime);

        // Khởi động lại Runnable khi Activity tiếp tục
        handler.post(updateTimeRunnable);
        handler.post(checkFlagRunnable);
    }

    private void updateActivityTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = accumulatedTime + (currentTime - startTime);

        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        tv_thoi_gian_truc_tuyen.setText(timeString);
    }

    // Lớp hỗ trợ để lưu kết quả truy vấn
    private static class QueryResult {
        int orderCount;
        BigDecimal totalCashCollected;
        BigDecimal totalIncome;

        QueryResult(int orderCount, BigDecimal totalCashCollected, BigDecimal totalIncome) {
            this.orderCount = orderCount;
            this.totalCashCollected = totalCashCollected;
            this.totalIncome = totalIncome;
        }
    }

    private class QueryTask extends AsyncTask<String, Void, QueryResult> {
        @Override
        protected QueryResult doInBackground(String... params) {
            String userId = params[0];
            int orderCount = 0;
            BigDecimal totalCashCollected = BigDecimal.ZERO;
            BigDecimal totalIncome = BigDecimal.ZERO;

            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.conClass();

            if (connection != null) {
                try {
                    String countQuery = "SELECT COUNT(*) FROM Orders WHERE delivery_id = ? AND status = 'delivered'";
                    PreparedStatement countStatement = connection.prepareStatement(countQuery);
                    countStatement.setString(1, userId);
                    ResultSet countResult = countStatement.executeQuery();

                    if (countResult.next()) {
                        orderCount = countResult.getInt(1);
                    }
                    countResult.close();
                    countStatement.close();

                    String cashQuery = "SELECT o.total_price, o.delivery_price " +
                            "FROM Orders o " +
                            "WHERE o.delivery_id = ? AND o.status = 'delivered' AND o.payment_method = 'cash'";
                    PreparedStatement cashStatement = connection.prepareStatement(cashQuery);
                    cashStatement.setString(1, userId);
                    ResultSet cashResult = cashStatement.executeQuery();

                    while (cashResult.next()) {
                        BigDecimal totalPrice = cashResult.getBigDecimal("total_price");
                        BigDecimal deliveryPrice = cashResult.getBigDecimal("delivery_price");
                        totalCashCollected = totalCashCollected.add(totalPrice).add(deliveryPrice);
                    }
                    cashResult.close();
                    cashStatement.close();

                    totalIncome = new BigDecimal(orderCount).multiply(new BigDecimal("50000"));

                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return new QueryResult(orderCount, totalCashCollected, totalIncome);
        }

        @Override
        protected void onPostExecute(QueryResult result) {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            BigDecimal bonus = BigDecimal.ZERO;
            if (result.orderCount >= 10) {
                int multiplier = result.orderCount / 10;
                bonus = new BigDecimal(multiplier).multiply(new BigDecimal("50000"));
            }

            BigDecimal totalIncomeWithBonus = result.totalIncome.add(bonus);

            String formattedIncome = currencyFormatter.format(totalIncomeWithBonus);
            String formattedCashCollected = currencyFormatter.format(result.totalCashCollected);

            tv_so_chuyen_di.setText(String.valueOf(result.orderCount));
            tv_thu_nhap_chuyen_di.setText(formattedIncome);
            tv_da_thu_tien_mat.setText(formattedCashCollected);

            if (result.orderCount >= 20) {
                new UpdateFlagTask().execute(userId);
            }
        }
    }

    private class CheckFlagTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String userId = params[0];
            int flagValue = 0;

            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.conClass();

            if (connection != null) {
                try {
                    String query = "SELECT flag FROM Users WHERE user_id = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, userId);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        flagValue = resultSet.getInt("flag");
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return flagValue;
        }

        @Override
        protected void onPostExecute(Integer flagValue) {
            if (flagValue == 1) {
                Color_report.setBackgroundColor(COLOR_RED);
            } else {
                Color_report.setBackgroundColor(COLOR_GREEN);
            }
        }
    }

    private class UpdateFlagTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String userId = params[0];

            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.conClass();

            if (connection != null) {
                try {
                    String checkFlagQuery = "SELECT flag FROM Users WHERE user_id = ?";
                    PreparedStatement checkFlagStatement = connection.prepareStatement(checkFlagQuery);
                    checkFlagStatement.setString(1, userId);
                    ResultSet flagResult = checkFlagStatement.executeQuery();

                    int flag = 0;
                    if (flagResult.next()) {
                        flag = flagResult.getInt("flag");
                    }
                    flagResult.close();
                    checkFlagStatement.close();

                    if (flag == 0) {
                        String updateFlagQuery = "UPDATE Users SET flag = 1 WHERE user_id = ?";
                        PreparedStatement updateFlagStatement = connection.prepareStatement(updateFlagQuery);
                        updateFlagStatement.setString(1, userId);
                        updateFlagStatement.executeUpdate();
                        updateFlagStatement.close();
                    }

                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
