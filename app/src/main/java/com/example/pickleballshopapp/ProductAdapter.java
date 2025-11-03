package com.example.pickleballshopapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Thư viện tải ảnh
import java.text.NumberFormat; // Để định dạng tiền tệ
import java.util.List;
import java.util.Locale;
import android.content.Intent;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    // Constructor
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    // 1. Tạo ViewHolder (khung)
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Lấy layout item_product.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    // 2. "Bơm" dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set tên sản phẩm
        holder.nameTextView.setText(product.getName());
// ... (bên trong hàm onBindViewHolder)

// Xử lý khi người dùng bấm vào một sản phẩm
        // Xử lý khi người dùng bấm vào một sản phẩm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1. Lấy vị trí HIỆN TẠI (cách an toàn để sửa cảnh báo)
                int currentPosition = holder.getAdapterPosition();

                // 2. Kiểm tra xem vị trí có còn hợp lệ không
                if (currentPosition != RecyclerView.NO_POSITION) {

                    // 3. Lấy đúng sản phẩm vừa được bấm
                    Product clickedProduct = productList.get(currentPosition);

                    // 4. Tạo một "lá thư" (Intent) để gửi đến màn hình chi tiết
                    Intent intent = new Intent(context, ProductDetailActivity.class);

                    // 5. "Gói" (Put) sản phẩm vào lá thư
                    intent.putExtra("PRODUCT_DETAIL", clickedProduct);

                    // 6. Gửi lá thư (khởi động Activity mới)
                    context.startActivity(intent);
                }
            }
        });
        // Định dạng giá tiền (ví dụ: 5.500.000 đ)
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        holder.priceTextView.setText(currencyFormatter.format(product.getPrice()));
        // Set text cho Thương hiệu (thêm dòng này)
        holder.productBrandTextView.setText(product.getBrand());

        // Dùng Glide để tải ảnh từ URL
        Glide.with(context)
                .load(product.getImageUrl()) // Lấy link ảnh
                .placeholder(R.drawable.ic_launcher_background) // Ảnh giữ chỗ
                .error(R.drawable.ic_launcher_background) // Ảnh khi lỗi
                .into(holder.imageView); // Bơm vào ImageView
    }

    // 3. Trả về số lượng sản phẩm
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Lớp ViewHolder: Ánh xạ các View trong item_product.xml
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView productBrandTextView;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Tìm ID khớp với file item_product.xml
            imageView = itemView.findViewById(R.id.productImageView);
            nameTextView = itemView.findViewById(R.id.productNameTextView);
            priceTextView = itemView.findViewById(R.id.productPriceTextView);
            productBrandTextView = itemView.findViewById(R.id.productBrandTextView); // <-- THÊM DÒNG NÀY (dòng 101)
        }
    }
}
