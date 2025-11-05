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
        // Ẩn nút khi đã Delivered, Cancelled hoặc Shipped
        if ("Delivered".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status) || "Shipped".equalsIgnoreCase(status)) {
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.btnCancel.setVisibility(View.VISIBLE);
        }

        // Hiển thị text trạng thái màu
        holder.tvStatusMessage.setVisibility(View.GONE);
        if ("Pending".equalsIgnoreCase(status)) {
            holder.tvStatusMessage.setText("Chờ xác nhận");
            holder.tvStatusMessage.setTextColor(0xFFFFA500); // Vàng đậm (Orange)
            holder.tvStatusMessage.setVisibility(View.VISIBLE);
        } else if ("Confirmed".equalsIgnoreCase(status)) {
            holder.tvStatusMessage.setText("Đơn hàng của bạn đã được xác nhận.");
            holder.tvStatusMessage.setTextColor(0xFF1976D2); // Xanh dương đậm
            holder.tvStatusMessage.setVisibility(View.VISIBLE);
        } else if ("Shipped".equalsIgnoreCase(status)) {
            holder.tvStatusMessage.setText("Đơn hàng của bạn đang được giao.");
            holder.tvStatusMessage.setTextColor(0xFF9C27B0); // Tím nhạt
            holder.tvStatusMessage.setVisibility(View.VISIBLE);
        } else if ("Delivered".equalsIgnoreCase(status)) {
            holder.tvStatusMessage.setText("Đơn hàng của bạn đã được giao.");
            holder.tvStatusMessage.setTextColor(0xFF4CAF50); // Xanh lá
            holder.tvStatusMessage.setVisibility(View.VISIBLE);
        }

        // Hiển thị thông báo khi Cancelled (giữ nguyên status là "Cancelled", chỉ hiển thị thông tin trong tvCancelledInfo)
        if ("Cancelled".equalsIgnoreCase(status)) {
            // Giữ nguyên status là "Cancelled", không thay đổi tvStatus
            String by = o.getCancelled_by();
            String reason = o.getCancel_reason();
            String msg = "";
            if (by != null && by.equalsIgnoreCase("admin")) {
                msg = "Đơn hàng của bạn đã bị hủy bởi admin";
            } else {
                msg = "Bạn đã hủy đơn hàng";
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
        TextView tvId, tvDate, tvStatus, tvTotal, tvCancelledInfo, tvStatusMessage;
        Button btnCancel;
        VH(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvOrderId);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvCancelledInfo = itemView.findViewById(R.id.tvCancelledInfo);
            tvStatusMessage = itemView.findViewById(R.id.tvStatusMessage);
            btnCancel = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}


