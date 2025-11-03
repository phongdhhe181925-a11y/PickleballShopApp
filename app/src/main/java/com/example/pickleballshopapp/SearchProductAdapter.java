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

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.SearchProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public SearchProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public SearchProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_product, parent, false);
        return new SearchProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set thương hiệu
        holder.brandTextView.setText(product.getBrand());

        // Set tên sản phẩm
        holder.nameTextView.setText(product.getName());

        // Định dạng giá tiền
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        holder.priceTextView.setText(currencyFormatter.format(product.getPrice()));

        // Load ảnh - dùng getMainImageUrl() để lấy ảnh đầu tiên từ danh sách images hoặc imageUrl
        String imageUrl = product.getMainImageUrl();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageView);

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

    public static class SearchProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView brandTextView;
        TextView nameTextView;
        TextView priceTextView;

        public SearchProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.searchProductImage);
            brandTextView = itemView.findViewById(R.id.searchProductBrand);
            nameTextView = itemView.findViewById(R.id.searchProductName);
            priceTextView = itemView.findViewById(R.id.searchProductPrice);
        }
    }
}



