package com.example.pickleballshopapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartDialogFragment extends DialogFragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button checkoutButton, viewCartButton;
    private TextView emptyCartText;
    private ImageButton closeButton;
    private List<Product> cartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate layout dialog_cart.xml
        return inflater.inflate(R.layout.dialog_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View (dùng ID mới)
        cartRecyclerView = view.findViewById(R.id.dialogCartRecyclerView);
        checkoutButton = view.findViewById(R.id.dialogCheckoutButton);
        viewCartButton = view.findViewById(R.id.dialogViewCartButton);
        emptyCartText = view.findViewById(R.id.dialogEmptyCartText);
        closeButton = view.findViewById(R.id.close_cart_button);

        // 2. Lấy giỏ hàng
        cartItems = CartManager.getCartItems();

        // 3. Hiển thị hoặc ẩn RecyclerView
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            // checkoutButton.setVisibility(View.GONE); // Có thể vẫn hiện nút
            // viewCartButton.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            cartAdapter = new CartAdapter(getContext(), cartItems);
            cartRecyclerView.setAdapter(cartAdapter);
        }

        // 4. Xử lý nút đóng (X)
        closeButton.setOnClickListener(v -> dismiss()); // Đóng DialogFragment

        // 5. Xử lý nút "Thanh toán"
        checkoutButton.setOnClickListener(v -> {
            // Kiểm tra đăng nhập... (giống code cũ)
            SessionManager sessionManager = new SessionManager(requireActivity());
            if (!sessionManager.isLoggedIn()) {
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                dismiss(); // Đóng dialog trước khi mở Login
            } else {
                Toast.makeText(getContext(), "Đã đăng nhập! (Đi tới Checkout...)", Toast.LENGTH_SHORT).show();
                // Đóng dialog và xử lý checkout
                dismiss();
            }
        });

        // 6. Xử lý nút "Xem giỏ hàng" (tạm thời đóng dialog)
        viewCartButton.setOnClickListener(v -> {
            // Có thể mở CartFragment chính ở đây nếu muốn
            Toast.makeText(getContext(),"Mở trang giỏ hàng chi tiết", Toast.LENGTH_SHORT).show();
            dismiss(); // Đóng DialogFragment
        });
    }

    // --- Cấu hình Dialog để trượt từ bên phải ---
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {

                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.END; // Hiện ở bên phải
                params.width = WindowManager.LayoutParams.WRAP_CONTENT; // Chiều rộng tự động
                params.height = WindowManager.LayoutParams.MATCH_PARENT; // Cao hết màn hình
                window.setAttributes(params);
                window.setBackgroundDrawableResource(android.R.color.transparent); // Nền trong suốt
                // Thêm hiệu ứng trượt (animation)
                window.setWindowAnimations(R.style.DialogAnimationSlideRight);
            }
        }
    }

    // Tạo style animation (cần tạo file anim/slide_in_right.xml, anim/slide_out_right.xml và styles.xml)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Áp dụng style animation
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RightSlideDialogStyle);
    }

}