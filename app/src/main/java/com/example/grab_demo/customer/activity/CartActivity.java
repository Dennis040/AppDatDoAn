package com.example.grab_demo.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.customer.adapter.C_VoucherAdapter;
import com.example.grab_demo.customer.adapter.ListOrderAdapter;
import com.example.grab_demo.customer.m_interface.StClickItem;
import com.example.grab_demo.customer.model.Item;
import com.example.grab_demo.customer.model.Voucher;
import com.example.grab_demo.database.ConnectionClass;
import com.example.grab_demo.zalopay.Api.CreateOrder;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

//import vn.zalopay.sdk.Environment;
//import vn.zalopay.sdk.ZaloPayError;
//import vn.zalopay.sdk.ZaloPaySDK;
//import vn.zalopay.sdk.listeners.PayOrderListener;

public class CartActivity extends AppCompatActivity {
    private static final long REFRESH_INTERVAL = 1000; // 1s
    RecyclerView rcv_voucher;
    List<Voucher> voucherList;
    C_VoucherAdapter voucherAdapter;
    Connection connection, connection2;
    String query, query2;
    Statement smt, smt2;
    ResultSet resultSet, resultSet2;
    Spinner SP_order;
    RecyclerView rcv_cart;
    List<Item> itemList;
    ListOrderAdapter itemAdapter;
    ImageView img_back;
    TextView txt_name_voucher, txt_orderMoney, txt_shipMoney, txt_voucher, txt_totalMoney;
    Button btn_orderNow, btn_getVoucher;
    int voucherId = 1;
    int storeId;
    double orderMoney = 0;
    double shipMoney = 0;
    double voucher = 0;
    double totalMoney = 0;
    int userId;
    private Handler handler;
    private Runnable refreshRunnable;
    List<String> listpay = new ArrayList<>();
    int pay;
    int cateid;
    int point =0;
    int orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        addControls();

//        voucherId = getIntent().getIntExtra("voucher_id", -1);  // Lấy cate_id kiểu int với giá trị mặc định là -1
        storeId = getIntent().getIntExtra("store_id", -1);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        Log.e("userId", String.valueOf(userId));

//        if (voucherId == -1) {
//            Log.e("CartActivity", "voucher_id is null");
//        }

        // Truy vấn và cập nhật tên voucher
//        loadVoucherName(voucherId);
        loadDataPointUser();
        handler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadData();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        createDataSpinnerPay();
        //zaloPay();
        addEvents();
    }

    private void loadDataPointUser() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT point FROM Users WHERE user_id = " + userId;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);
                while (resultSet.next()) {
                    point = resultSet.getInt(1);
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

//    private void zaloPay() {
//        StrictMode.ThreadPolicy policy = new
//                StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        // ZaloPay SDK Init
//        ZaloPaySDK.init(2553, Environment.SANDBOX);
//    }

//    private void orderzalopay() {
//        //String totalText = String.valueOf(totalMoney).replaceAll("[^0-9]", ""); // Loại bỏ tất cả ký tự không phải số
//        // Định dạng số mà không thêm dấu phân cách ngàn
//        DecimalFormat df = new DecimalFormat("0.##");
//        String totalText = df.format(totalMoney);
//        Log.d("Amount",String.valueOf( totalMoney));
//        CreateOrder orderApi = new CreateOrder();
//        try {
//            JSONObject data = orderApi.createOrder(totalText);
//            Log.d("Amount", totalText);
//            String code = data.getString("return_code");
//            Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();
//
//            if (code.equals("1")) {
//                String token = data.getString("zp_trans_token");
//                ZaloPaySDK.getInstance().payOrder(CartActivity.this, token, "demo://app", new PayOrderListener() {
//                    @Override
//                    public void onPaymentSucceeded(String s, String s1, String s2) {
//                        insertDataToOrder(userId,"e-wallet");
//                        clearCartItems();
//                        clearCartI();
//                        Toast.makeText(CartActivity.this, "Order successfully!", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(CartActivity.this, HomeActivity.class));
//                        finish();
//                    }
//
//                    @Override
//                    public void onPaymentCanceled(String s, String s1) {
//                        Toast.makeText(CartActivity.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
//                        Log.e("Order canceled","Order canceled");
//                    }
//
//                    @Override
//                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
//                        Toast.makeText(CartActivity.this, "Order Failed!", Toast.LENGTH_SHORT).show();
//                        Log.e("Order Failed","Order Failed");
//                    }
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        ZaloPaySDK.getInstance().onResult(intent);
//    }
    private void loadVoucherName(int voucherId) {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT voucher_name, discount FROM Vouchers WHERE voucher_id = " + voucherId;
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                if (resultSet.next()) {
                    final String voucherName = resultSet.getString(1);
                    final double discount = resultSet.getDouble(2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txt_name_voucher.setText(voucherName);
                            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                            String formattedPrice = formatter.format(discount);
                            txt_voucher.setText(formattedPrice);
                            //txt_voucher.setText(String.valueOf(discount));
                            voucher = discount;
                        }
                    });
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
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
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {
                query = "SELECT Items.item_id, Items.item_name, Items.price, Items.image, CartItems.quantity FROM CartItems " +
                        "JOIN Items ON CartItems.item_id = Items.item_id " +
                        "JOIN Cart ON CartItems.cart_id = Cart.cart_id "  +
                        "WHERE Cart.customer_id = " + userId + "AND Cart.store_id = " + storeId; // Sử dụng cart_id thật của bạn
                smt = connection.createStatement();
                resultSet = smt.executeQuery(query);

                itemList.clear();
                orderMoney = 0; // Reset orderMoney

                while (resultSet.next()) {
                    int itemId = resultSet.getInt(1);
                    String itemName = resultSet.getString(2);
                    double price = resultSet.getDouble(3);
                    byte[] image = resultSet.getBytes(4);
                    int quantity = resultSet.getInt(5);

                    orderMoney += price * quantity; // Tính toán Order Money

                    itemList.add(new Item(itemId, itemName, price, image, quantity));
                }
                itemAdapter.notifyDataSetChanged();
                connection.close();

                calculateTotalMoney(); // Tính toán tổng tiền

            } catch (Exception e) {
                Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }

    private void calculateTotalMoney() {
        // Giả sử giá trị shipMoney và voucher

        shipMoney = 50000;
        totalMoney = orderMoney + shipMoney - voucher;
        // Tính toán Total Money
        if (point >= 2000) {
           totalMoney *= 0.96;
        } else if (point >= 1000) {
            totalMoney *= 0.98;
        }else if (point >= 5000) {
            totalMoney *= 0.94;
        }else if (point >= 10000) {
            totalMoney *= 0.92;
        }


        // Cập nhật giao diện người dùng trên UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String formattedPrice = formatter.format(orderMoney);
                txt_orderMoney.setText(formattedPrice);
                //txt_orderMoney.setText(String.valueOf(orderMoney));
                formattedPrice = formatter.format(shipMoney);
                txt_shipMoney.setText(formattedPrice);
                if (totalMoney < 0)
                    totalMoney = 0;
                else {
                    formattedPrice = formatter.format(totalMoney);
                    txt_totalMoney.setText(formattedPrice);
                    //txt_totalMoney.setText(String.valueOf(totalMoney));
                }
            }
        });
    }

    private void createDataSpinnerPay() {
        listpay.add("Thanh toán tiền mặt");
        listpay.add("Thanh toán qua zalopay");
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listpay);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP_order.setAdapter(adapter);
    }

    private void addEvents() {
        SP_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listpay.get(position).equals("Thanh toán qua zalopay")) {
                    pay = 1;
                } else {
                    pay = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_getVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalMoney > 50000) {
//                    Intent intent = new Intent(CartActivity.this, VoucherActivity.class);
//                    startActivity(intent);
//                    finish();
                    rcv_voucher.setVisibility(View.VISIBLE);
                    loadDataVoucher();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Tổng giá trị đơn hàng phải trên 50.000đ");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
        btn_orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderMoney > 0) {
                    if (pay == 0) {
                        insertDataToOrder(userId,"cash");
                         clearCartItems(); // Xóa các item trong CartItems
                        clearCartI();
                        Toast.makeText(CartActivity.this, "Order successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CartActivity.this, HomeActivity.class));
                    } else if (pay == 1) {
//                        orderzalopay();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Giỏ hàng trống!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
        voucherAdapter.setOnClickItemListener(new StClickItem() {
            @Override
            public void onClickItem(String data) {
//                Intent intent = new Intent(CartActivity.this, CartActivity.class);
//                intent.putExtra("voucher_id", Integer.parseInt(data));
//                startActivity(intent);
//                finish();
                rcv_voucher.setVisibility(View.GONE);
                voucherId = Integer.parseInt(data);
                loadVoucherName(voucherId);
            }
        });
    }

    private void loadDataVoucher() {
        ConnectionClass sql = new ConnectionClass();
        connection = sql.conClass();
        if (connection != null) {
            try {


                query = "SELECT Vouchers.voucher_id, Vouchers.voucher_name, Vouchers.discount, Vouchers.end_date, Vouchers.quantity " +
                        "FROM Vouchers " +
                        "WHERE NOT EXISTS ( " +
                        "    SELECT 1 FROM Orders " +
                        "    WHERE Orders.voucher_id = Vouchers.voucher_id " +
                        "    AND Orders.customer_id = " + userId +
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
    private void clearCartI() {
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            try {
                String deleteQuery = "DELETE FROM Cart WHERE store_id = (SELECT store_id FROM Orders WHERE order_id = " + orderId + ") AND customer_id = (SELECT customer_id FROM Orders WHERE order_id = " + orderId + ")";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    Log.d("CartActivity", "Cart items cleared successfully");
                } else {
                    Log.e("CartActivity", "Failed to clear cart items");
                }
            } catch (SQLException e) {
                Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
                }
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }
    private void clearCartItems() {
        ConnectionClass sql = new ConnectionClass();
        Connection connection = sql.conClass();

        if (connection != null) {
            try {
                String deleteQuery = "DELETE FROM CartItems WHERE cart_id IN (SELECT cart_id FROM Cart WHERE customer_id = " + userId + ")";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    Log.d("CartActivity", "Cart items cleared successfully");
                } else {
                    Log.e("CartActivity", "Failed to clear cart items");
                }
            } catch (SQLException e) {
                Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
                }
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }


    private void insertDataToOrder(int userId,String cash) {
        ConnectionClass sql = new ConnectionClass();
        connection2 = sql.conClass();

        if (connection2 != null) {
            try {
                // Kiểm tra và xử lý userId
                if (userId == -1) {
                    Log.e("CartActivity", "UserId is null or empty");
                    return;
                }
                int customerId;
                try {
                    customerId = userId;
                } catch (NumberFormatException e) {
                    Log.e("CartActivity", "Invalid userId format: " + e.getMessage());
                    return;
                }

                // Kiểm tra và xử lý txt_shipMoney
                String shipMoneyText = txt_shipMoney.getText().toString();
                double deliveryPrice;
                try {
                    // Loại bỏ ký tự tiền tệ và dấu cách
                    shipMoneyText = shipMoneyText.replace("₫", "").trim();

                    // Chuyển đổi dấu phân cách ngàn từ "." sang ","
                    shipMoneyText = shipMoneyText.replace(".", ",");

                    // Phân tích cú pháp chuỗi thành số
                    NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
                    Number number = format.parse(shipMoneyText);

                    // Chuyển sang kiểu dữ liệu mong muốn, ví dụ như Double
                    deliveryPrice = number.doubleValue();

                    System.out.println("Số tiền: " + deliveryPrice);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("CartActivity", "Invalid ship money format: " + e.getMessage());
                    return;
                }

//                try {
//                    deliveryPrice = Double.parseDouble(shipMoneyText);
//                } catch (NumberFormatException e) {
//                    Log.e("CartActivity", "Invalid ship money format: " + e.getMessage());
//                    return;
//                }

                // Kiểm tra và xử lý txt_totalMoney
                String totalMoneyText = txt_totalMoney.getText().toString();
                double totalPrice;
                try {
                    // Loại bỏ ký tự tiền tệ và dấu cách
                    totalMoneyText = totalMoneyText.replace("₫", "").trim();

                    // Chuyển đổi dấu phân cách ngàn từ "." sang ","
                    totalMoneyText = totalMoneyText.replace(".", ",");

                    // Phân tích cú pháp chuỗi thành số
                    NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
                    Number number = format.parse(totalMoneyText);

                    // Chuyển sang kiểu dữ liệu mong muốn, ví dụ như Double
                    totalPrice = number.doubleValue();

                    System.out.println("Số tiền: " + totalPrice);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("CartActivity", "Invalid ship money format: " + e.getMessage());
                    return;
                }
//                try {
//                    totalPrice = Double.parseDouble(totalMoneyText);
//                } catch (NumberFormatException e) {
//                    Log.e("CartActivity", "Invalid total money format: " + e.getMessage());
//                    return;
//                }

                // Lấy store_id từ CartItems
                String query1 = "SELECT i.store_id FROM CartItems c JOIN Items i ON c.item_id = i.item_id JOIN Cart ca ON c.cart_id = ca.cart_id WHERE ca.customer_id = " + userId;
                PreparedStatement insertStmt1 = connection2.prepareStatement(query1);
                ResultSet rs = insertStmt1.executeQuery();
                int storeId = -1;
                if (rs.next()) {
                    storeId = rs.getInt(1);
                } else {
                    Log.e("CartActivity", "store_id is null");
                    return;
                }
                rs.close();
                insertStmt1.close();

                // Chuẩn bị và thực hiện câu lệnh SQL để thêm đơn hàng
                String query2 = "INSERT INTO Orders (customer_id, store_id, delivery_price, total_price, payment_method, voucher_id, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = connection2.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setInt(1, customerId);
                insertStmt.setInt(2, storeId);
                insertStmt.setDouble(3, deliveryPrice*1000);
                insertStmt.setDouble(4, totalPrice*1000);
                insertStmt.setString(5, cash);
                if (voucherId == -1) { // Kiểm tra nếu voucherId là null
                    insertStmt.setNull(6, java.sql.Types.INTEGER);
                } else {
                    insertStmt.setInt(6, voucherId);
                }
                insertStmt.setString(7, "pending");

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {

                    //Lấy orderId đơn hàng vua them
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);

                        // Truy vấn danh sách item_id và quantity từ CartItems
                        String query3 = "SELECT  item_id, quantity FROM CartItems"+
                                "JOIN Cart ON CartItems.cart_id = Cart.cart_id  WHERE Cart.customer_id = " + userId + "AND Cart.store_id = " + storeId;
                        PreparedStatement stmtDetails = connection2.prepareStatement(query3);
                        ResultSet rsDetails = stmtDetails.executeQuery();

                        String insertOrderDetailsQuery = "INSERT INTO OrderDetails (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertStmt2 = connection2.prepareStatement(insertOrderDetailsQuery);

                        while (rsDetails.next()) {
                            int itemId = rsDetails.getInt("item_id");
                            int quantity = rsDetails.getInt("quantity");

                            // Truy vấn giá của item từ bảng Items
                            String query4 = "SELECT price FROM Items WHERE item_id = ?";
                            PreparedStatement stmtItem = connection2.prepareStatement(query4);
                            stmtItem.setInt(1, itemId);
                            ResultSet rsItem = stmtItem.executeQuery();

                            if (rsItem.next()) {
                                double itemPrice = rsItem.getDouble("price");
                                double price = itemPrice * quantity;

                                insertStmt2.setInt(1, orderId);
                                insertStmt2.setInt(2, itemId);
                                insertStmt2.setInt(3, quantity);
                                insertStmt2.setDouble(4, price);

                                insertStmt2.executeUpdate();
                            } else {
                                Log.e("CartActivity", "Item not found with item_id: " + itemId);
                            }

                            rsItem.close();
                            stmtItem.close();
                        }

                        rsDetails.close();
                        stmtDetails.close();
                        insertStmt2.close();

                        Log.d("CartActivity", "Insert successfully");
                    } else {
                        Log.e("CartActivity", "Failed to retrieve order_id");
                    }
                    generatedKeys.close();

                } else {
                    Log.e("CartActivity", "Insert failed");
                }
                insertStmt.close();
            } catch (SQLException e) {
                Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
                try {
                    connection2.rollback(); // Rollback nếu có lỗi xảy ra
                } catch (SQLException ex) {
                    Log.e("Error: ", Objects.requireNonNull(ex.getMessage()));
                }
            } finally {
                try {
                    if (connection2 != null && !connection2.isClosed()) {
                        connection2.setAutoCommit(true); // Đặt lại AutoCommit
                        connection2.close(); // Đóng kết nối
                    }
                } catch (SQLException e) {
                    Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
                }
            }
        } else {
            Log.e("Error: ", "Connection null");
        }
    }


    private void addControls() {
        txt_name_voucher = findViewById(R.id.txt_name_voucher);
        txt_orderMoney = findViewById(R.id.txt_orderMoney);
        txt_shipMoney = findViewById(R.id.txt_shipMoney);
        txt_voucher = findViewById(R.id.txt_voucher);
        txt_totalMoney = findViewById(R.id.txt_totalMoney);

        img_back = findViewById(R.id.img_back);
        btn_orderNow = findViewById(R.id.btn_orderNow);
        btn_getVoucher = findViewById(R.id.btn_getVoucher);
        SP_order = findViewById(R.id.SP_order);
        rcv_cart = findViewById(R.id.rcv_cart);
        itemList = new ArrayList<>();
        itemAdapter = new ListOrderAdapter(this, itemList);
        rcv_cart.setAdapter(itemAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_cart.setLayoutManager(linearLayoutManager);
        rcv_voucher = findViewById(R.id.rcv_voucher_cart);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_voucher.setLayoutManager(linearLayoutManager2);  // Set LayoutManager cho RecyclerView

        voucherList = new ArrayList<>();
        voucherAdapter = new C_VoucherAdapter(this, voucherList);
        rcv_voucher.setAdapter(voucherAdapter);
    }

    @Override
    protected void onResume() {
        loadData(); // Tải dữ liệu khi Activity được hiển thị

        super.onResume();
        startAutoRefresh(); // Bắt đầu làm mới tự động
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable); // Ngừng làm mới khi Activity không còn hiển thị
    }
}