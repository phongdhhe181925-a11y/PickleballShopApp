package com.example.pickleballshopapp;

import android.content.Context;
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
import java.util.List;
import java.util.Locale;

public class HorizontalProductAdapter extends RecyclerView.Adapter<HorizontalProductAdapter.HorizontalProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public HorizontalProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public HorizontalProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_horizontal_product, parent, false);
        return new HorizontalProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.horizontalProductBrand.setText(product.getBrand());
        holder.horizontalProductName.setText(product.getName());

        // Format giá tiền
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        holder.horizontalProductPrice.setText(currencyFormatter.format(product.getPrice()));

        // Load ảnh
        Glide.with(context)
                .load(product.getMainImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.horizontalProductImage);

        // Xử lý click
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Product clickedProduct = productList.get(currentPosition);
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("PRODUCT_DETAIL", clickedProduct);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class HorizontalProductViewHolder extends RecyclerView.ViewHolder {
        ImageView horizontalProductImage;
        TextView horizontalProductBrand;
        TextView horizontalProductName;
        TextView horizontalProductPrice;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalProductImage = itemView.findViewById(R.id.horizontalProductImage);
            horizontalProductBrand = itemView.findViewById(R.id.horizontalProductBrand);
            horizontalProductName = itemView.findViewById(R.id.horizontalProductName);
            horizontalProductPrice = itemView.findViewById(R.id.horizontalProductPrice);
        }
    }
}



