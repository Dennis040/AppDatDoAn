package com.example.grab_demo.customer.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.activity.HomeActivity;
import com.example.grab_demo.database.ConnectionClass;
import com.example.grab_demo.login.LoginActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProfileFragment extends Fragment {
    Connection connection;
    String query;
    Button btnLogout;
    Statement smt;
    ResultSet resultSet;
    TextView txt_name, txt_phone, txt_email, txt_point, txt_rank;
    private View view;
    private HomeActivity homeActivity;
    private int userId;
    int point = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        addControls();
        if (getActivity() instanceof HomeActivity) {
            homeActivity = (HomeActivity) getActivity();
            userId = Integer.parseInt(homeActivity.getUserId());
            Log.e("userId", String.valueOf(userId));
        }
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        userId = sharedPreferences.getInt("userId", -1);
        Log.e("HomeFragment", String.valueOf(userId));

        if (userId != -1) {
            loadData(); // Load data using userId
//            point = Integer.parseInt(txt_point.getText().toString());
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });

        } else {
            Log.e("HomeFragment", "userId is null");
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userId != -1) {
            loadData(); // Load data using userId
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT username, email, phone_number, point FROM Users WHERE user_id = " + userId;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                if (resultSet.next()) {
                    txt_name.setText(resultSet.getString(1));
                    txt_phone.setText(resultSet.getString(3));
                    txt_email.setText(resultSet.getString(2));
                    point = resultSet.getInt(4);
                    txt_point.setText(point + " Điểm");
                    loadRank();
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error: ", "Failed to close connection: " + e.getMessage());
                }
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }


    private void loadRank() {
        if (point >= 10000) {
            txt_rank.setText("Kim cương");
        } else if (point >= 5000) {
            txt_rank.setText("Vàng");
        } else if (point >= 2000) {
            txt_rank.setText("Bạc");
        } else if (point >= 1000) {
            txt_rank.setText("Đồng");
        } else {
            txt_rank.setText("Sắt");
        }
    }


    private void addControls() {
        txt_name = view.findViewById(R.id.edtName_profile);
        txt_phone = view.findViewById(R.id.txt_phone_profile);
        txt_email = view.findViewById(R.id.edtEmail_profile);
        txt_point = view.findViewById(R.id.txt_point_customer);
        txt_rank = view.findViewById(R.id.txt_rank);
        btnLogout = view.findViewById(R.id.btnLogout);
    }
}