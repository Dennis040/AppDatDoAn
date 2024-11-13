package com.example.grab_demo.customer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.adapter.ListStoreCartAdapter;
import com.example.grab_demo.database.ConnectionClass;
import com.example.grab_demo.store_owner.OnItemClickListener;
import com.example.grab_demo.store_owner.model.Stores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ListStoreCartActivity extends AppCompatActivity {
    ImageButton btn_back_liststorecart;
    RecyclerView rcv_liststorecart;
    ArrayList<Stores> arr;
    ListStoreCartAdapter listStoreCartAdapter;
    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;
    int userId = -1;
    ArrayList<Integer> listStoreId = new ArrayList<>();
    int storeid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_store_cart);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        Log.e("userId", String.valueOf(userId));
        if (userId != -1) {
            addControls();
            addEvents();
            loadData();
        }
    }

    private void loadData() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT store_id FROM Cart WHERE customer_id = " + userId;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                while (resultSet.next()) {
                    Log.e("store_id", String.valueOf(resultSet.getInt(1)));
                    listStoreId.add(resultSet.getInt(1));
                }
                connection.close();
                loadDataStore();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void loadDataStore() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            for (int i = 0; i < listStoreId.size(); i++) {
                storeid = listStoreId.get(i);
                Log.e("store_id", String.valueOf(storeid));
                if (storeid != -1) {
                    try {
                        query = "SELECT store_name, image FROM Stores WHERE store_id = " + storeid;
                        smt = connection.createStatement();
                        resultSet = smt.executeQuery(query);
                        while (resultSet.next()) {
                            String storeName = resultSet.getString(1);
                            byte[] image = resultSet.getBytes(2);
                            arr.add(new Stores(image, storeName));
                        }
                        connection.close();

                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                }
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void addEvents() {
        btn_back_liststorecart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listStoreCartAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                ConnectionClass sql = new ConnectionClass();
                connection = sql.conClass();
                if (connection != null) {
                    try {
                        String query = "SELECT store_id FROM Stores WHERE store_name = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, data);
                        resultSet = statement.executeQuery();
                        if(resultSet.next()) {
                            storeid = resultSet.getInt(1);
                        }
                        connection.close();
                        Intent intent = new Intent(ListStoreCartActivity.this, CartActivity.class);
                        intent.putExtra("store_id", storeid);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                } else {
                    Log.e("Error: ", "Connection null");
                }
            }

            @Override
            public void onItemClickIStoreRegistration(int data, String name) {

            }

            @Override
            public void onItemClickMessage(int sender_id, int reciever_id) {

            }

            @Override
            public void onItemClickID(int data) {

            }
        });
    }

    private void addControls() {
        btn_back_liststorecart = findViewById(R.id.btn_back_liststorecart);
        rcv_liststorecart = findViewById(R.id.rcv_liststorecart);
        arr = new ArrayList<>();
        listStoreCartAdapter = new ListStoreCartAdapter(this, arr);
        rcv_liststorecart.setAdapter(listStoreCartAdapter);
        rcv_liststorecart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}