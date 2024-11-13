package com.example.grab_demo.customer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.adapter.C_VoucherAdapter;
import com.example.grab_demo.customer.m_interface.StClickItem;
import com.example.grab_demo.customer.model.Voucher;
import com.example.grab_demo.database.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class VoucherActivity extends AppCompatActivity {
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    ArrayList<Integer> listvoucherid = new ArrayList<>();
    ImageView btn_back;
    RecyclerView rcv_voucher;
    List<Voucher> voucherList;
    C_VoucherAdapter voucherAdapter;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getInt("user_id", -1);
        Log.e("userId", String.valueOf(user_id));
        addControls();
        loadData();
        addEvents();
    }


    private void loadData() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
                try {


                    query = "SELECT Vouchers.voucher_id, Vouchers.voucher_name, Vouchers.discount, Vouchers.end_date, Vouchers.quantity " +
                            "FROM Vouchers " +
                            "WHERE NOT EXISTS ( " +
                            "    SELECT 1 FROM Orders " +
                            "    WHERE Orders.voucher_id = Vouchers.voucher_id " +
                            "    AND Orders.customer_id = " + user_id +
                            ")";

                    smt = connection.createStatement();
                    resultSet = smt.executeQuery(query);

                    voucherList.clear();
                    while (resultSet.next()) {
                        int voucherId = resultSet.getInt(1);
                        String voucherName = resultSet.getString(2);
                        double discount = resultSet.getDouble(3);
                        Date startDate = resultSet.getDate(4);
                        int quantity = resultSet.getInt(5);
                        voucherList.add(new Voucher(voucherId, voucherName, discount, startDate, quantity));
                    }
                    voucherAdapter.notifyDataSetChanged();
                    connection.close();

                } catch (Exception e) {
                    Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
                }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void addEvents() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        voucherAdapter.setOnClickItemListener(new StClickItem() {
            @Override
            public void onClickItem(String data) {
                Intent intent = new Intent(VoucherActivity.this, CartActivity.class);
                intent.putExtra("voucher_id", Integer.parseInt(data));
                startActivity(intent);
                finish();
            }
        });
    }

    private void addControls() {
        btn_back = findViewById(R.id.img_back);
        rcv_voucher = findViewById(R.id.rcv_voucher);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_voucher.setLayoutManager(linearLayoutManager);  // Set LayoutManager cho RecyclerView

        voucherList = new ArrayList<>();
        voucherAdapter = new C_VoucherAdapter(this, voucherList);
        rcv_voucher.setAdapter(voucherAdapter);
    }
}