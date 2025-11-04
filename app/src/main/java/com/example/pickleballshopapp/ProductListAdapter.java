package com.example.pickleballshopapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.VH> {
    private final List<Product> items = new ArrayList<>();

    public void replace(List<Product> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        TextView brand;

        VH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.productImageView);
            name = v.findViewById(R.id.productNameTextView);
            price = v.findViewById(R.id.productPriceTextView);
            brand = v.findViewById(R.id.productBrandTextView);
        }

        void bind(Product p) {
            name.setText(p.getName());
            NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
            price.setText(vn.format(p.getPrice()));
            Glide.with(image.getContext()).load(p.getMainImageUrl()).into(image);
            if (brand != null) {
                brand.setText(p.getBrand() != null ? p.getBrand() : "");
            }
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("PRODUCT_DETAIL", p);
                v.getContext().startActivity(intent);
            });
        }
    }
}


