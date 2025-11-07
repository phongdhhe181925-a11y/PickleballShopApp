package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button checkoutButton;
    private TextView emptyCartText;
    private TextView tvCartTotal;
    private TextView tvCartPrice;
    private View totalAndCheckoutContainer;
    private View cartDivider;
    private TextView tvCartShippingNote;
    private List<Product> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        // Setup footer
        View footerView = findViewById(R.id.footer);
        if (footerView != null) {
            new FooterHelper(this, footerView);
        }

// Toolbar đã được cấu hình qua ToolbarUtils và không có nút back

        // 1. Ánh xạ View từ file activity_cart.xml...
        // 1. Ánh xạ View từ file activity_cart.xml
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        checkoutButton = findViewById(R.id.checkoutButton);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        tvCartPrice = findViewById(R.id.tvCartPrice);
        totalAndCheckoutContainer = findViewById(R.id.totalAndCheckoutContainer);
        cartDivider = findViewById(R.id.cartDivider);
        tvCartShippingNote = findViewById(R.id.tvCartShippingNote);
        // Sử dụng nút back mặc định trên Action Bar (đã xử lý trong onOptionsItemSelected)
        emptyCartText = findViewById(R.id.emptyCartText);

        // 2. Lấy danh sách sản phẩm từ giỏ hàng (CartManager)
        cartItems = CartManager.getCartItems();

        // 3. Kiểm tra xem giỏ hàng có trống không
        if (cartItems.isEmpty()) {
            // Nếu trống: Ẩn danh sách và container tổng tiền, hiện chữ "Giỏ hàng trống"
            cartRecyclerView.setVisibility(View.GONE);
            totalAndCheckoutContainer.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
        } else {
            // 4. Nếu có hàng: Hiện danh sách và container tổng tiền, ẩn chữ "Giỏ hàng trống"
            cartRecyclerView.setVisibility(View.VISIBLE);
            totalAndCheckoutContainer.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);

            // Cài đặt RecyclerView
            cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartAdapter = new CartAdapter(this, cartItems);
            cartAdapter.setOnCartChangedListener(() -> updateTotal());
            cartRecyclerView.setAdapter(cartAdapter);
            updateTotal();
        }

        // 5. Xử lý khi bấm nút "Thanh toán"
        checkoutButton.setOnClickListener(new View.OnClickListener() {

            // ... (bên trong checkoutButton.setOnClickListener)
            @Override
            public void onClick(View v) {

                // 1. Khởi tạo SessionManager
                SessionManager sessionManager = new SessionManager(CartActivity.this);

                // 2. Kiểm tra xem đã đăng nhập chưa
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                    startActivity(intent);
                } else {
                    // Nếu CHƯA ĐĂNG NHẬP
                    // Mới chuyển đến màn hình Đăng nhập
                    Toast.makeText(CartActivity.this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    // ... (Đây là dấu } kết thúc của hàm onCreate)

    // Không xử lý nút back trên action bar vì đã tắt hiển thị

    private void updateTotal() {
        // Cập nhật lại cartItems từ CartManager để lấy dữ liệu mới nhất
        cartItems = CartManager.getCartItems();
        
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            totalAndCheckoutContainer.setVisibility(View.GONE);
            tvCartPrice.setText("0₫");
            emptyCartText.setVisibility(View.VISIBLE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            totalAndCheckoutContainer.setVisibility(View.VISIBLE);
            checkoutButton.setVisibility(View.VISIBLE);
            tvCartTotal.setVisibility(View.VISIBLE);
            tvCartPrice.setVisibility(View.VISIBLE);
            cartDivider.setVisibility(View.VISIBLE);
            tvCartShippingNote.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            java.text.NumberFormat vn = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi","VN"));
            double total = 0d;
            for (Product p : cartItems) total += p.getPrice() * p.getQuantity();
            tvCartPrice.setText(vn.format(total));
        }
    }
} // Dấu } cuối cùng của class CartActivity
