package com.example.grab_demo.store_owner.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grab_demo.R;
import com.example.grab_demo.database.ConnectionClass;
import com.example.grab_demo.store_owner.model.DishMenuHSO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongKeActivity extends AppCompatActivity {
    Toolbar toolbaritem;
    PieChart piechartitem;
    BarChart barchartitem;
    ImageButton exchangethongke;
    int storeID = -1;
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thong_ke);
        storeID = Integer.parseInt(getIntent().getStringExtra("store_id"));
        Log.d("ThongKeActivity", "Received storeId: " + storeID);
        if (storeID != -1 && storeID >= 1 ) {
            addControl();
            ActionToolBar();
            addEvents();
            loadDataPieChart();
            settingBarChar();
            loadDataBarChart();
        } else {
            Log.e("ThongKeActivity", "storeID is null");
        }

    }

    private void settingBarChar() {
        barchartitem.getDescription().setEnabled(false);
        barchartitem.setDrawValueAboveBar(false);
        //barchartitem.setDrawGridBackground(false);
        XAxis xAxis = barchartitem.getXAxis();
        xAxis.setAxisMaximum(12);
        xAxis.setAxisMinimum(1);
        YAxis yAxisRight = barchartitem.getAxisRight();
        yAxisRight.setAxisMinimum(0);
        YAxis yAxisLeft = barchartitem.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
    }

    private void loadDataBarChart() {
        List<BarEntry> listdata = new ArrayList<>();
        loadDataBC(listdata);
        BarDataSet barDataSet = new BarDataSet(listdata,"Thống kê");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(13f);
        barDataSet.setValueTextColor(Color.RED);
        BarData barData = new BarData(barDataSet);
//        barData.setValueTextColor(Color.RED);
//        barData.setValueTextSize(13f);
//        barData.setValueFormatter(new PercentFormatter(piechartitem));
        barchartitem.setData(barData);
        barchartitem.animateXY(2000,2000);
        barchartitem.invalidate();
    }

    private void loadDataBC(List<BarEntry> listdata) {
        ConnectionClass connectionClass = new ConnectionClass();
        connection = connectionClass.conClass();
        if (connection != null) {
            try {
                String sql = "EXEC GetMonthlySalesByStore ?"; // Thay thế tên stored procedure và tham số tương ứng
                PreparedStatement stmt = connection.prepareStatement(sql);
                Log.d("SQL", sql);
                stmt.setInt(1, storeID);
                ResultSet rs = stmt.executeQuery();
                listdata.clear();
                // Xử lý kết quả trả về
                while (rs.next()) {
                    int month = rs.getInt("SalesMonth");
                    Log.d("month", String.valueOf(month));
                    float total = rs.getFloat("TotalSales");
                    listdata.add(new BarEntry(month,total));
                }
                // Đóng kết nối và các tài nguyên
                rs.close();
                stmt.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void addEvents() {
        exchangethongke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(piechartitem.getVisibility() == View.VISIBLE){
                piechartitem.setVisibility(View.GONE);
                barchartitem.setVisibility(View.VISIBLE);
                }else{
                    piechartitem.setVisibility(View.VISIBLE);
                    barchartitem.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadDataPieChart() {
        List<PieEntry> listdata = new ArrayList<>();
        loadData(listdata);
        PieDataSet pieDataSet = new PieDataSet(listdata,"Thống kê");
        PieData pieData = new PieData(pieDataSet);
        pieData.setDataSet(pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueFormatter(new PercentFormatter(piechartitem));
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        piechartitem.setData(pieData);
        piechartitem.getDescription().setEnabled(false);
        piechartitem.setCenterText("Thống kê");
        piechartitem.animate();
        piechartitem.setUsePercentValues(true);
        piechartitem.animateXY(2000,2000);
        piechartitem.invalidate();
    }

    private void loadData(List<PieEntry> listdata) {
        ConnectionClass connectionClass = new ConnectionClass();
        connection = connectionClass.conClass();
        if (connection != null) {
            try {
                String sql = "EXEC GetProductSalesByStore ?"; // Thay thế tên stored procedure và tham số tương ứng
                PreparedStatement stmt = connection.prepareStatement(sql);
                Log.d("SQL", sql);
                stmt.setInt(1, storeID);
                ResultSet rs = stmt.executeQuery();
                listdata.clear();
                // Xử lý kết quả trả về
                while (rs.next()) {
                    String itemName = rs.getString("ProductName");
                    Log.d("itemName", itemName);
                    Integer SL = rs.getInt("TotalQuantitySold");
                    listdata.add(new PieEntry(SL,itemName));
                }
                // Đóng kết nối và các tài nguyên
                rs.close();
                stmt.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void addControl() {
        toolbaritem = findViewById(R.id.toolbaritem);
        piechartitem = findViewById(R.id.piechartitem);
        barchartitem = findViewById(R.id.barchartitem);
        exchangethongke = findViewById(R.id.exchangethongke);
    }

    private void ActionToolBar(){
        setSupportActionBar(toolbaritem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbaritem.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}