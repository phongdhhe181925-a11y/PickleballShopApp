package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.TextView; // Import TextView
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Import LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView; // Import RecyclerView

import java.util.List; // Import List

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button checkoutButton;
    private Button viewCartButton;
    private TextView emptyCartText;
    private TextView tvCartTotal;
    private TextView tvCartPrice;
    private TextView fragmentShippingNote;
    private List<Product> cartItems;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Find Views (use view.findViewById in Fragments)
        // Use the NEW IDs from fragment_cart.xml
        cartRecyclerView = view.findViewById(R.id.fragmentCartRecyclerView);
        checkoutButton = view.findViewById(R.id.fragmentCheckoutButton);
        viewCartButton = view.findViewById(R.id.fragmentViewCartButton);
        emptyCartText = view.findViewById(R.id.fragmentEmptyCartText);
        tvCartTotal = view.findViewById(R.id.fragmentTvCartTotal);
        tvCartPrice = view.findViewById(R.id.fragmentTvCartPrice);
        fragmentShippingNote = view.findViewById(R.id.fragmentShippingNote);
// === THÊM CODE XỬ LÝ NÚT ĐÓNG VÀO ĐÂY (Dòng 58) ===
        // Ánh xạ nút đóng (X)
        // (Bấm Alt+Enter để import android.widget.ImageButton)
        android.widget.ImageButton closeButton = view.findViewById(R.id.fragment_close_cart_button);

        // Xử lý khi bấm nút đóng (X)
        closeButton.setOnClickListener(v -> { // Dùng Lambda cho gọn
            // Yêu cầu Activity (MainActivity) xóa Fragment này đi
            if (getActivity() != null) {
                // Lệnh này sẽ xóa Fragment hiện tại và chạy animation ngược lại
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        // =================================================
        // 2. Get items from CartManager
        cartItems = CartManager.getCartItems();

        // 3. Check if cart is empty
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            checkoutButton.setVisibility(View.GONE);
            viewCartButton.setVisibility(View.GONE);
            fragmentShippingNote.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
        } else {
            // 4. If not empty, show RecyclerView
            cartRecyclerView.setVisibility(View.VISIBLE);
            checkoutButton.setVisibility(View.VISIBLE);
            viewCartButton.setVisibility(View.VISIBLE);
            fragmentShippingNote.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);

            // Setup RecyclerView (use requireContext() in Fragments)
            cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            cartAdapter = new CartAdapter(requireContext(), cartItems);
            cartAdapter.setOnCartChangedListener(this::updateTotal);
            cartRecyclerView.setAdapter(cartAdapter);
            updateTotal();
        }

        // 5. Handle Checkout Button click
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check login status (Use requireActivity() for context)
                SessionManager sessionManager = new SessionManager(requireActivity());

                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(requireActivity(), CheckoutActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        viewCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateTotal() {
        // Cập nhật lại cartItems từ CartManager để lấy dữ liệu mới nhất
        cartItems = CartManager.getCartItems();
        
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            checkoutButton.setVisibility(View.GONE);
            viewCartButton.setVisibility(View.GONE);
            fragmentShippingNote.setVisibility(View.GONE);
            tvCartTotal.setVisibility(View.GONE);
            if (tvCartPrice != null) {
                tvCartPrice.setVisibility(View.GONE);
                tvCartPrice.setText("0₫");
            }
            emptyCartText.setVisibility(View.VISIBLE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            checkoutButton.setVisibility(View.VISIBLE);
            viewCartButton.setVisibility(View.VISIBLE);
            fragmentShippingNote.setVisibility(View.VISIBLE);
            tvCartTotal.setVisibility(View.VISIBLE);
            if (tvCartPrice != null) tvCartPrice.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            java.text.NumberFormat vn = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi","VN"));
            double total = 0d;
            for (Product p : cartItems) total += p.getPrice() * p.getQuantity();
            tvCartTotal.setText("Tổng phụ");
            if (tvCartPrice != null) tvCartPrice.setText(vn.format(total));
        }
    }
}