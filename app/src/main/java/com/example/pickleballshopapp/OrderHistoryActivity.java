package com.example.pickleballshopapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OrderActionListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private OrderHistoryAdapter adapter;
    private android.widget.TextView tvEmptyOrders;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        recyclerView = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        SessionManager sm = new SessionManager(this);
        int userId = 0;
        try {
            userId = Integer.parseInt(sm.getUserId());
        } catch (NumberFormatException e) {
            // UserID is not a valid int or is null, userId remains 0
        }

        if (userId <= 0) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getApiService();
        api.getOrders(userId).enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                progressBar.setVisibility(View.GONE);
                OrdersResponse body = response.body();
                if (body != null && body.isSuccess()) {
                    List<OrderDto> orders = body.getData();
                    adapter.updateData(orders);
                    
                    // Hiển thị/ẩn thông báo khi không có đơn hàng
                    if (orders == null || orders.isEmpty()) {
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmptyOrders.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không tải được đơn hàng", Toast.LENGTH_SHORT).show();
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderHistoryActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                tvEmptyOrders.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCancelOrderClicked(OrderDto order) {
        String status = order.getStatus();
        if ("Shipped".equalsIgnoreCase(status)) {
            Toast.makeText(this, "Đơn hàng đang được giao, không thể hủy", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("Delivered".equalsIgnoreCase(status)) {
            // Theo yêu cầu: nút đã ẩn, phòng hờ
            return;
        }
        
        // Hiển thị dialog để nhập lí do hủy đơn
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_cancel_order);
        
        android.widget.EditText etReason = dialog.findViewById(R.id.etCancelReason);
        Button btnCancelDialog = dialog.findViewById(R.id.btnCancelDialog);
        Button btnConfirmCancel = dialog.findViewById(R.id.btnConfirmCancel);
        
        btnCancelDialog.setOnClickListener(v -> dialog.dismiss());
        
        btnConfirmCancel.setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            
            SessionManager sm = new SessionManager(this);
            int userId = 0;
            try {
                userId = Integer.parseInt(sm.getUserId());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Lỗi tài khoản, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            ApiService api = RetrofitClient.getApiService();
            api.cancelOrder(new CancelOrderRequest(userId, order.getId(), reason))
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            BaseResponse body = response.body();
                            if (body != null && body.isSuccess()) {
                                Toast.makeText(OrderHistoryActivity.this, "Đã hủy đơn", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                loadOrders();
                            } else {
                                Toast.makeText(OrderHistoryActivity.this, body != null ? body.getMessage() : "Hủy đơn thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            Toast.makeText(OrderHistoryActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        
        dialog.show();
    }

    @Override
    public void onOrderClicked(OrderDto order) {
        // Hiển thị dialog với chi tiết đơn hàng
        showOrderDetailsDialog(order);
    }

    private void showOrderDetailsDialog(OrderDto order) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_order_details);

        RecyclerView rvDetails = dialog.findViewById(R.id.rvOrderDetails);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        rvDetails.setLayoutManager(new LinearLayoutManager(this));
        OrderDetailAdapter detailAdapter = new OrderDetailAdapter(new ArrayList<>());
        rvDetails.setAdapter(detailAdapter);

        SessionManager sm = new SessionManager(this);
        int userId = 0;
        try {
            userId = Integer.parseInt(sm.getUserId());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lỗi tài khoản", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        ApiService api = RetrofitClient.getApiService();
        api.getOrderDetails(order.getId(), userId).enqueue(new Callback<OrderDetailsResponse>() {
            @Override
            public void onResponse(Call<OrderDetailsResponse> call, Response<OrderDetailsResponse> response) {
                OrderDetailsResponse body = response.body();
                if (body != null && body.isSuccess()) {
                    detailAdapter.updateData(body.getData());
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không tải được chi tiết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
