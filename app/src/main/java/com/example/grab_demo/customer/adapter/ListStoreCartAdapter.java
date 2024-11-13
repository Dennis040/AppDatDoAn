package com.example.grab_demo.customer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grab_demo.R;
import com.example.grab_demo.database.ConnectionClass;
import com.example.grab_demo.store_owner.OnItemClickListener;
import com.example.grab_demo.store_owner.adapter.StoreHSOAdapter;
import com.example.grab_demo.store_owner.model.Stores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ListStoreCartAdapter extends RecyclerView.Adapter<ListStoreCartAdapter.ViewHolder>{

    Context context;
    ArrayList<Stores> arr;
    private OnItemClickListener onItemClickListener;
    String status;
    public ListStoreCartAdapter(Context context, ArrayList<Stores> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_liststorecart, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Stores stores = arr.get(position);
        byte[] hinhAlbumByteArray = stores.getHinh();
        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAlbumByteArray, 0, hinhAlbumByteArray.length);
        holder.img.setImageBitmap(bitmap);
        holder.txtTen.setText(stores.getTensp());
        holder.getCurrentStoreStatus();
        // Kiểm tra trạng thái của cửa hàng
        if ("closed".equalsIgnoreCase(status)){
            // Làm mờ item và vô hiệu hóa click
            holder.itemView.setAlpha(0.5f);
            holder.itemView.setEnabled(false);
            holder.itemView.setOnClickListener(null);  // Ngắt onClickListener
        } else {
            // Đặt lại alpha và click khi cửa hàng mở
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(arr.get(position).getTensp());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtTen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ImageView_item_liststorecart);
            txtTen = itemView.findViewById(R.id.tv_name_item_liststorecart);
        }
        private void getCurrentStoreStatus() {
            ConnectionClass sql = new ConnectionClass();
            Connection connection = sql.conClass();
            if (connection != null) {
                try {
                    String query = "SELECT status FROM Stores WHERE store_name = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, txtTen.getText().toString().trim()); // Thay storeId bằng ID của cửa hàng bạn muốn lấy trạng thái
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        status = resultSet.getString(1);
                    }
                    connection.close();
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
            } else {
                Log.e("Error: ", "Connection null");
            }
        }

    }

}
