package com.example.grab_demo.store_owner.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.activity.OrderDetailsActivity;
import com.example.grab_demo.customer.adapter.OrderDetailsAdapter;
import com.example.grab_demo.customer.model.OrderDetails;
import com.example.grab_demo.database.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderDetailSOActivity extends AppCompatActivity {
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    TextView txt_order_id;
    ImageButton btn_close;

    RecyclerView rcv_orderDetails;
    List<OrderDetails> orderDetailsList;
    OrderDetailsAdapter orderDetailsAdapter;

    int orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail_soactivity);
        addControls();

        orderId = getIntent().getIntExtra("order_id", -1);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(50000);
        if (orderId == -1) {
            Log.e("OrderDetailsActivity", "order_id is null");
        } else {
            txt_order_id.setText(String.valueOf(orderId));
            loadOrderDetails(orderId);
        }

        addEvents();
    }

    private void loadOrderDetails(int orderId) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "select order_id, item_id, quantity, price from OrderDetails where order_id = " + orderId;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                orderDetailsList.clear();

                while (resultSet.next()) {
                    int order_id = resultSet.getInt(1);
                    int item_id = resultSet.getInt(2);
                    int quantity = resultSet.getInt(3);
                    double price = resultSet.getDouble(4);

                    orderDetailsList.add(new OrderDetails(order_id, item_id, quantity, price));
                }
                connection.close();
                orderDetailsAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void addEvents() {
        btn_close.setOnClickListener(v -> finish());
    }


    private void addControls() {
        txt_order_id = findViewById(R.id.txt_order_id);
        btn_close = findViewById(R.id.btn_close);
        rcv_orderDetails = findViewById(R.id.rcv_orderDetails);
        orderDetailsList = new ArrayList<>();
        orderDetailsAdapter = new OrderDetailsAdapter(this, orderDetailsList);
        rcv_orderDetails.setAdapter(orderDetailsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_orderDetails.setLayoutManager(linearLayoutManager);

    }
}