package com.example.pickleballshopapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.VH> {

    private final List<OrderDetailDto> details;

    public OrderDetailAdapter(List<OrderDetailDto> details) {
        this.details = details != null ? details : new ArrayList<>();
    }

    public void updateData(List<OrderDetailDto> newData) {
        this.details.clear();
        if (newData != null) this.details.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderDetailDto d = details.get(position);
        holder.tvProductName.setText(d.getProduct_name());
        holder.tvQuantity.setText("x" + d.getQuantity());
        holder.tvPrice.setText(String.format("%,.0f đ", d.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice;

        VH(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}






