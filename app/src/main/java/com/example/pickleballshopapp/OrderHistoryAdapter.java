package com.example.pickleballshopapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.VH> {

    public interface OrderActionListener {
        void onCancelOrderClicked(OrderDto order);
        void onOrderClicked(OrderDto order);
    }

    private final List<OrderDto> orders;
    private final OrderActionListener listener;

    public OrderHistoryAdapter(List<OrderDto> orders, OrderActionListener listener) {
        this.orders = orders != null ? orders : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<OrderDto> newData) {
        this.orders.clear();
        if (newData != null) this.orders.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderDto o = orders.get(position);
        holder.tvId.setText("#" + o.getId());
        holder.tvDate.setText(o.getOrder_date());
        holder.tvStatus.setText(o.getStatus());
        holder.tvTotal.setText(String.format("%,.0f đ", o.getTotal_money()));

        String status = o.getStatus() != null ? o.getStatus() : "";
        // Ẩn nút khi đã Delivered hoặc Cancelled
        if ("Delivered".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.btnCancel.setVisibility(View.VISIBLE);
        }

        // Hiển thị thông báo khi Cancelled (ưu tiên hiển thị ai hủy + lý do nếu có)
        if ("Cancelled".equalsIgnoreCase(status)) {
            String by = o.getCancelled_by();
            String reason = o.getCancel_reason();
            String msg = "Đơn hàng của bạn đã bị hủy";
            if (by != null && by.equalsIgnoreCase("admin")) {
                msg = "Đơn hàng của bạn đã bị hủy bởi admin";
            }
            if (reason != null && !reason.trim().isEmpty()) {
                msg += ". Lý do: " + reason.trim();
            }
            holder.tvCancelledInfo.setText(msg);
            holder.tvCancelledInfo.setVisibility(View.VISIBLE);
        } else {
            holder.tvCancelledInfo.setVisibility(View.GONE);
        }

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancelOrderClicked(o);
        });

        // Click vào item để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClicked(o);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvStatus, tvTotal, tvCancelledInfo;
        Button btnCancel;
        VH(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvOrderId);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvCancelledInfo = itemView.findViewById(R.id.tvCancelledInfo);
            btnCancel = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}


