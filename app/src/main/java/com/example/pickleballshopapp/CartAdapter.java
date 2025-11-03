package com.example.pickleballshopapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> cartItems;
    private OnCartChangedListener onCartChangedListener;
    public interface OnCartChangedListener {
        void onCartChanged();
    }
    public void setOnCartChangedListener(OnCartChangedListener listener) {
        this.onCartChangedListener = listener;
    }

    public CartAdapter(Context context, List<Product> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);
        
        // Hiển thị brand name (màu xám nhạt, trên tên sản phẩm)
        if (product.getBrand() != null && !product.getBrand().isEmpty()) {
            holder.tvBrand.setText(product.getBrand());
            holder.tvBrand.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.tvBrand.setVisibility(android.view.View.GONE);
        }
        
        // Hiển thị tên sản phẩm
        holder.tvName.setText(product.getName());
        
        // Hiển thị selected option values (màu xám nhạt, dưới tên sản phẩm)
        String selectedOptionsText = product.getSelectedOptionValuesText();
        if (selectedOptionsText != null && !selectedOptionsText.isEmpty()) {
            holder.tvOptions.setText(selectedOptionsText);
            holder.tvOptions.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.tvOptions.setVisibility(android.view.View.GONE);
        }
        
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        holder.tvPrice.setText(currencyFormatter.format(product.getPrice()));
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivImage);
        
        // Ẩn đường kẻ nếu là item cuối cùng (chỉ có 1 sản phẩm hoặc item cuối)
        View divider = holder.itemView.findViewById(R.id.cartDivider);
        if (divider != null) {
            if (position == cartItems.size() - 1) {
                // Item cuối cùng: ẩn đường kẻ
                divider.setVisibility(android.view.View.GONE);
            } else {
                // Không phải item cuối: hiện đường kẻ
                divider.setVisibility(android.view.View.VISIBLE);
            }
        }
        // TĂNG SỐ LƯỢNG
        holder.btnPlus.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity()+1);
            notifyItemChanged(position);
            if (onCartChangedListener != null) onCartChangedListener.onCartChanged();
        });
        // GIẢM SỐ LƯỢNG (KHÔNG CHO < 1)
        holder.btnMinus.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity()-1);
                notifyItemChanged(position);
                if (onCartChangedListener != null) onCartChangedListener.onCartChanged();
            } else {
                Toast.makeText(context, "Số lượng sản phẩm tối thiểu là 1", Toast.LENGTH_SHORT).show();
            }
        });
        // XÓA SẢN PHẨM
        holder.tvDelete.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            if (onCartChangedListener != null) onCartChangedListener.onCartChanged();
        });
        holder.tvDelete.setText("Xóa");
        holder.tvDelete.setPaintFlags(holder.tvDelete.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvBrand, tvName, tvOptions, tvPrice, tvQuantity, tvDelete;
        android.widget.ImageButton btnMinus, btnPlus;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage   = itemView.findViewById(R.id.itemCartImage);
            tvBrand   = itemView.findViewById(R.id.itemCartBrand);
            tvName    = itemView.findViewById(R.id.itemCartName);
            tvOptions = itemView.findViewById(R.id.itemCartOptions);
            tvPrice   = itemView.findViewById(R.id.itemCartPrice);
            tvQuantity= itemView.findViewById(R.id.itemCartQuantity);
            btnMinus  = itemView.findViewById(R.id.itemCartBtnMinus);
            btnPlus   = itemView.findViewById(R.id.itemCartBtnPlus);
            tvDelete  = itemView.findViewById(R.id.itemCartBtnDelete);
        }
    }
}