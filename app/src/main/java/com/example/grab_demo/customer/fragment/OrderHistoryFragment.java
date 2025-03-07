package com.example.grab_demo.customer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.adapter.C_OrderAdapter;
import com.example.grab_demo.customer.model.Order;
import com.example.grab_demo.database.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private static final long REFRESH_INTERVAL = 1000; // 2 giây

    Connection connection;
    String query;
    Statement smt;
    ResultSet resultSet;

    RecyclerView rcv_order;
    List<Order> orderList;
    C_OrderAdapter orderAdapter;
    private Handler handler;
    private Runnable refreshRunnable;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_history, container, false);

        addControls();
        loadData();
        addEvents();
        handler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadData();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionClass sql = new ConnectionClass();
                connection = sql.conClass();
                if (connection != null) {
                    try {
                        query = "SELECT order_id, delivery_id, total_price, status FROM Orders WHERE status IN ('delivered', 'canceled')";
                        smt = connection.createStatement();
                        resultSet = smt.executeQuery(query);
                        final ArrayList<Order> tempArr = new ArrayList<>();

                        while (resultSet.next()) {
                            int orderId = resultSet.getInt(1);
                            int deliveryId = resultSet.getInt(2);
                            double totalPrice = resultSet.getDouble(3);
                            String status = resultSet.getString(4);
                            tempArr.add(new Order(orderId, deliveryId, totalPrice, status));
                        }
                        connection.close();

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                orderList.clear();
                                orderList.addAll(tempArr);
                                orderAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                } else {
                    Log.e("Error: ", "Connection null");
                }
            }
        }).start();
    }

    private void addEvents() {

    }

    private void addControls() {
        rcv_order = view.findViewById(R.id.rcv_order_history);
        orderList = new ArrayList<>();
        orderAdapter = new C_OrderAdapter(getActivity(), orderList);
        rcv_order.setAdapter(orderAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcv_order.setLayoutManager(linearLayoutManager);  // Set LayoutManager cho RecyclerView
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderList = new ArrayList<>(); // Khởi tạo orderList ở đây
    }
}
