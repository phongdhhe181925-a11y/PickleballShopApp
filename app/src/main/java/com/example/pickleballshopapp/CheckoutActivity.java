package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import com.bumptech.glide.Glide;

public class CheckoutActivity extends AppCompatActivity {
    
    // Hằng số phí vận chuyển
    private static final double SHIPPING_STANDARD = 50000.0; // Giao hàng tiết kiệm
    private static final double SHIPPING_FAST = 100000.0;     // Giao hàng nhanh
    private static final double SHIPPING_EXPRESS = 250000.0;  // Giao hàng siêu tốc
    
           private RadioGroup rgShippingMethod;
           private RadioGroup rgPaymentMethod;
           private double currentShippingFee = SHIPPING_STANDARD; // Mặc định
           private String currentPaymentMethod = "bank_transfer"; // Mặc định: Chuyển khoản ngân hàng
           private double totalFinal; // Tổng tiền sản phẩm (không bao gồm phí vận chuyển)
           private TextView tvTotal;
           private TextView tvSubtotalLeft, tvSubtotalRight, tvShippingRight;
           private androidx.cardview.widget.CardView cardStandard;
           private androidx.cardview.widget.CardView cardFast;
           private androidx.cardview.widget.CardView cardExpress;
           private androidx.cardview.widget.CardView cardBankTransfer;
           private androidx.cardview.widget.CardView cardCOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup logo clickable để quay về trang chủ
        android.widget.ImageView logo = toolbar.findViewById(R.id.toolbar_logo);
        if (logo != null) {
            logo.setOnClickListener(v -> {
                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Đóng màn hình checkout khi quay về trang chủ
            });
        }

        // Setup cart icon
        android.widget.ImageView cartAction = toolbar.findViewById(R.id.action_cart);
        if (cartAction != null) {
            cartAction.setOnClickListener(v -> {
                Intent cartIntent = new Intent(CheckoutActivity.this, CartActivity.class);
                startActivity(cartIntent);
            });
        }

        EditText etAddress = findViewById(R.id.etShippingAddress);
        EditText etPhone = findViewById(R.id.etShippingPhone);
        android.widget.LinearLayout llOrderSummary = findViewById(R.id.llOrderSummary);
        tvSubtotalLeft = findViewById(R.id.tvSubtotalLeft);
        tvSubtotalRight = findViewById(R.id.tvSubtotalRight);
        tvShippingRight = findViewById(R.id.tvShippingRight);
        tvTotal = findViewById(R.id.tvOrderTotal);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        
        // Ánh xạ RadioGroup và RadioButtons
        rgShippingMethod = findViewById(R.id.rgShippingMethod);
        RadioButton rbStandard = findViewById(R.id.rbStandard);
        RadioButton rbFast = findViewById(R.id.rbFast);
        RadioButton rbExpress = findViewById(R.id.rbExpress);
        
        // Set màu đen cho RadioButton
        rbStandard.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK));
        rbFast.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK));
        rbExpress.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK));
        
        // Ánh xạ TextView giá để thêm underline cho chữ "đ"
        TextView tvStandardPrice = findViewById(R.id.tvStandardPrice);
        TextView tvFastPrice = findViewById(R.id.tvFastPrice);
        TextView tvExpressPrice = findViewById(R.id.tvExpressPrice);
        
        // Thêm underline cho chữ "đ" trong các TextView giá
        setupPriceUnderline(tvStandardPrice);
        setupPriceUnderline(tvFastPrice);
        setupPriceUnderline(tvExpressPrice);
        
        // Ánh xạ CardView cho Shipping
        cardStandard = findViewById(R.id.cardStandard);
        cardFast = findViewById(R.id.cardFast);
        cardExpress = findViewById(R.id.cardExpress);
        
        // Ánh xạ RadioGroup và RadioButtons cho Payment
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        RadioButton rbBankTransfer = findViewById(R.id.rbBankTransfer);
        RadioButton rbCOD = findViewById(R.id.rbCOD);
        
        // Set màu đen cho Payment RadioButton
        rbBankTransfer.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK));
        rbCOD.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK));
        
        // Ánh xạ CardView cho Payment
        cardBankTransfer = findViewById(R.id.cardBankTransfer);
        cardCOD = findViewById(R.id.cardCOD);
        
        // Mặc định chọn "Giao hàng tiết kiệm" - sử dụng RadioGroup.check() để đảm bảo chỉ 1 được chọn
        currentShippingFee = SHIPPING_STANDARD;
        currentPaymentMethod = "bank_transfer"; // Mặc định: Chuyển khoản ngân hàng
        
        // Click listener cho các CardView Shipping - khi click vào CardView thì chọn RadioButton tương ứng
        cardStandard.setOnClickListener(v -> {
            rgShippingMethod.check(R.id.rbStandard); // Sử dụng RadioGroup.check() để đảm bảo chỉ 1 được chọn
        });
        
        cardFast.setOnClickListener(v -> {
            rgShippingMethod.check(R.id.rbFast);
        });
        
        cardExpress.setOnClickListener(v -> {
            rgShippingMethod.check(R.id.rbExpress);
        });
        
        // Click listener cho các CardView Payment
        cardBankTransfer.setOnClickListener(v -> {
            rgPaymentMethod.check(R.id.rbBankTransfer);
        });
        
        cardCOD.setOnClickListener(v -> {
            rgPaymentMethod.check(R.id.rbCOD);
        });
        
        // Click listener cho RadioButton - đảm bảo khi click trực tiếp vào RadioButton cũng hoạt động
        // Không cần setOnClickListener riêng vì RadioGroup đã tự động xử lý

        Intent intent = getIntent();
        Product buyNowProduct = (Product) intent.getSerializableExtra("BUY_NOW_PRODUCT");
        int buyNowQty = intent.getIntExtra("BUY_NOW_QTY", 1);
        java.util.List<Product> items;
        if (buyNowProduct != null) {
            buyNowProduct.setQuantity(buyNowQty);
            items = new java.util.ArrayList<>();
            items.add(buyNowProduct);
        } else {
            items = CartManager.getCartItems();
        }
        double total = 0d;
        int itemCount = 0;
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        // Tạo item view cho mỗi sản phẩm
        int dpToPx = (int) (getResources().getDisplayMetrics().density);
        for (Product p : items) {
            int qty = p.getQuantity();
            itemCount += qty;
            double line = p.getPrice() * qty;
            total += line;
            
            // Tạo RelativeLayout container cho mỗi item
            android.widget.RelativeLayout itemContainer = new android.widget.RelativeLayout(this);
            android.widget.RelativeLayout.LayoutParams containerParams = new android.widget.RelativeLayout.LayoutParams(
                android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            containerParams.setMargins(0, 0, 0, 16 * dpToPx); // margin bottom 16dp
            itemContainer.setLayoutParams(containerParams);
            
            // ImageView bên trái
            android.widget.ImageView ivProductImage = new android.widget.ImageView(this);
            ivProductImage.setId(android.view.View.generateViewId()); // Tạo ID duy nhất
            android.widget.RelativeLayout.LayoutParams imageParams = new android.widget.RelativeLayout.LayoutParams(
                80 * dpToPx, // width 80dp
                80 * dpToPx  // height 80dp
            );
            imageParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_START);
            imageParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);
            imageParams.setMargins(0, 0, 0, 0); // Không cần marginEnd vì sẽ dùng marginStart cho llProductInfo
            ivProductImage.setLayoutParams(imageParams);
            ivProductImage.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER); // FIT_CENTER để hiển thị đầy đủ ảnh
            ivProductImage.setAdjustViewBounds(false); // Không cần adjustViewBounds khi đã set size cố định
            itemContainer.addView(ivProductImage);
            
            // Load ảnh bằng Glide
            Glide.with(this)
                .load(p.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivProductImage);
            
            // LinearLayout vertical bên phải ảnh chứa tên và option value
            android.widget.LinearLayout llProductInfo = new android.widget.LinearLayout(this);
            llProductInfo.setOrientation(android.widget.LinearLayout.VERTICAL);
            android.widget.RelativeLayout.LayoutParams infoParams = new android.widget.RelativeLayout.LayoutParams(
                android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            infoParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);
            infoParams.addRule(android.widget.RelativeLayout.RIGHT_OF, ivProductImage.getId());
            infoParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
            infoParams.setMargins(12 * dpToPx, 0, 0, 0); // marginStart 12dp để cách ảnh
            llProductInfo.setLayoutParams(infoParams);
            
            // LinearLayout horizontal cho tên sản phẩm và số lượng
            android.widget.LinearLayout llNameQty = new android.widget.LinearLayout(this);
            llNameQty.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            android.widget.LinearLayout.LayoutParams nameQtyParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            llNameQty.setLayoutParams(nameQtyParams);
            
            // TextView tên sản phẩm
            TextView tvProductName = new TextView(this);
            android.widget.LinearLayout.LayoutParams nameParams = new android.widget.LinearLayout.LayoutParams(
                0, // width = 0dp
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            nameParams.weight = 1.0f; // weight = 1 để chiếm phần còn lại
            tvProductName.setLayoutParams(nameParams);
            tvProductName.setText(p.getName());
            tvProductName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
            tvProductName.setTypeface(null, android.graphics.Typeface.BOLD);
            tvProductName.setTextColor(getResources().getColor(android.R.color.black));
            tvProductName.setMaxLines(Integer.MAX_VALUE); // Cho phép nhiều dòng
            llNameQty.addView(tvProductName);
            
            // TextView số lượng - cùng hàng với tên
            TextView tvQty = new TextView(this);
            android.widget.LinearLayout.LayoutParams qtyParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            qtyParams.setMargins(8 * dpToPx, 0, 0, 0); // marginStart 8dp để cách tên
            tvQty.setLayoutParams(qtyParams);
            tvQty.setText("x " + qty);
            tvQty.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
            tvQty.setTextColor(getResources().getColor(android.R.color.black));
            llNameQty.addView(tvQty);
            
            llProductInfo.addView(llNameQty);
            
            // TextView option value
            String selectedOptionsText = p.getSelectedOptionValuesText();
            if (selectedOptionsText != null && !selectedOptionsText.isEmpty()) {
                TextView tvOptionValue = new TextView(this);
                android.widget.LinearLayout.LayoutParams optionParams = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                );
                optionParams.setMargins(0, 4 * dpToPx, 0, 0); // marginTop 4dp
                tvOptionValue.setLayoutParams(optionParams);
                tvOptionValue.setText(selectedOptionsText);
                tvOptionValue.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
                tvOptionValue.setTextColor(getResources().getColor(android.R.color.darker_gray));
                llProductInfo.addView(tvOptionValue);
            }
            
            itemContainer.addView(llProductInfo);
            
            // TextView giá - ở góc dưới bên phải
            TextView tvPrice = new TextView(this);
            android.widget.RelativeLayout.LayoutParams priceParams = new android.widget.RelativeLayout.LayoutParams(
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            priceParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
            priceParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);
            tvPrice.setLayoutParams(priceParams);
            tvPrice.setText(vn.format(line));
            tvPrice.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
            tvPrice.setTypeface(null, android.graphics.Typeface.BOLD);
            tvPrice.setTextColor(getResources().getColor(android.R.color.black));
            itemContainer.addView(tvPrice);
            
            // Thêm item vào container
            llOrderSummary.addView(itemContainer);
        }
        
        totalFinal = total; // Tổng tiền sản phẩm (chưa bao gồm phí vận chuyển)
        // Cập nhật hàng Tổng phụ và Phí giao hàng
        if (tvSubtotalLeft != null) tvSubtotalLeft.setText("Tổng phụ: " + itemCount + " mặt hàng");
        if (tvSubtotalRight != null) tvSubtotalRight.setText(vn.format(totalFinal));
        if (tvShippingRight != null) tvShippingRight.setText(vn.format(currentShippingFee));
        
        // Listener cho RadioGroup - cập nhật phí vận chuyển khi thay đổi
        rgShippingMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbStandard) {
                currentShippingFee = SHIPPING_STANDARD;
            } else if (checkedId == R.id.rbFast) {
                currentShippingFee = SHIPPING_FAST;
            } else if (checkedId == R.id.rbExpress) {
                currentShippingFee = SHIPPING_EXPRESS;
            }
            updateCardViewSelection(checkedId); // Cập nhật màu CardView
            // Cập nhật hiển thị phí giao hàng riêng
            if (tvShippingRight != null) tvShippingRight.setText(vn.format(currentShippingFee));
            updateTotal(); // Cập nhật lại tổng tiền
        });
        
        // Listener cho Payment RadioGroup
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBankTransfer) {
                currentPaymentMethod = "bank_transfer";
            } else if (checkedId == R.id.rbCOD) {
                currentPaymentMethod = "cod";
            }
            updatePaymentCardViewSelection(checkedId); // Cập nhật màu CardView
        });
        
        // Sau khi setup listener, mới check RadioButton ban đầu
        // Điều này đảm bảo listener được gọi và cập nhật màu đúng
        rgShippingMethod.check(R.id.rbStandard);
        rgPaymentMethod.check(R.id.rbBankTransfer); // Mặc định chọn Chuyển khoản ngân hàng
        
        // Hiển thị tổng ban đầu
        updateTotal();

        btnPlaceOrder.setOnClickListener(v -> {
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            
            // Validation địa chỉ
            if (address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                etAddress.requestFocus();
                return;
            }
            
            // Validation số điện thoại - BẮT BUỘC
            if (phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                etPhone.requestFocus();
                return;
            }
            
            // Validation số điện thoại Việt Nam: bắt đầu bằng 09, đúng 10 chữ số
            if (!isValidVietnamesePhone(phone)) {
                Toast.makeText(this, "Số điện thoại không hợp lệ!\nVui lòng nhập số bắt đầu bằng 09 và có đúng 10 chữ số\nVí dụ: 0968918961", Toast.LENGTH_LONG).show();
                etPhone.setText("");
                etPhone.requestFocus();
                return; // KHÔNG cho phép đặt hàng nếu số điện thoại sai
            }
            
            // Double check: Validate lại một lần nữa trước khi tiếp tục
            String cleanPhone = phone.trim().replaceAll("[^0-9]", "");
            if (cleanPhone.length() != 10 || !cleanPhone.startsWith("09")) {
                Toast.makeText(this, "Số điện thoại không hợp lệ. Vui lòng kiểm tra lại", Toast.LENGTH_LONG).show();
                etPhone.setText("");
                etPhone.requestFocus();
                return;
            }
            
            SessionManager sessionManager = new SessionManager(this);
            String userIdStr = sessionManager.getUserId();
            int userId = 0;
            try { 
                if (userIdStr != null) userId = Integer.parseInt(userIdStr); 
            } catch (NumberFormatException ignored) {}
            
            if (userId > 0) {
                // Chỉ gọi API khi đã validate đúng số điện thoại
                syncCartAndCheckout(userId, address, cleanPhone, totalFinal, items);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // Hàm cập nhật tổng tiền bao gồm phí vận chuyển
    private void updateTotal() {
        double finalTotal = totalFinal + currentShippingFee;
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotal.setText(vn.format(finalTotal));
    }
    
    // Hàm thêm underline cho chữ "đ" trong TextView giá
    private void setupPriceUnderline(TextView textView) {
        String text = textView.getText().toString();
        android.text.SpannableString spannable = new android.text.SpannableString(text);
        
        // Tìm vị trí chữ "đ" cuối cùng trong chuỗi
        int lastIndex = text.lastIndexOf("đ");
        if (lastIndex >= 0) {
            spannable.setSpan(new android.text.style.UnderlineSpan(), lastIndex, lastIndex + 1, 
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        textView.setText(spannable);
    }
    
    // Hàm cập nhật màu nền CardView khi chọn phương thức vận chuyển
    private void updateCardViewSelection(int checkedId) {
        // Reset tất cả CardView về màu trắng
        cardStandard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardFast.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardExpress.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        
        // Tô màu xám đậm hơn cho CardView được chọn
        if (checkedId == R.id.rbStandard) {
            cardStandard.setCardBackgroundColor(getResources().getColor(R.color.selected_card_background));
        } else if (checkedId == R.id.rbFast) {
            cardFast.setCardBackgroundColor(getResources().getColor(R.color.selected_card_background));
        } else if (checkedId == R.id.rbExpress) {
            cardExpress.setCardBackgroundColor(getResources().getColor(R.color.selected_card_background));
        }
    }
    
    // Hàm cập nhật màu nền CardView khi chọn phương thức thanh toán
    private void updatePaymentCardViewSelection(int checkedId) {
        // Reset tất cả CardView về màu trắng
        cardBankTransfer.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardCOD.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        
        // Tô màu xám đậm hơn cho CardView được chọn
        if (checkedId == R.id.rbBankTransfer) {
            cardBankTransfer.setCardBackgroundColor(getResources().getColor(R.color.selected_card_background));
        } else if (checkedId == R.id.rbCOD) {
            cardCOD.setCardBackgroundColor(getResources().getColor(R.color.selected_card_background));
        }
    }

    private void syncCartAndCheckout(int userId, String address, String phone, double total, java.util.List<Product> localItems) {
        // Validate lại số điện thoại trước khi gọi API (safety check)
        if (phone == null || phone.trim().isEmpty() || !isValidVietnamesePhone(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
            EditText etPhone = findViewById(R.id.etShippingPhone);
            etPhone.setText("");
            etPhone.requestFocus();
            return; // Dừng ngay, không tiếp tục
        }
        
        ApiService api = RetrofitClient.getApiService();
        UserOnlyRequest clearReq = new UserOnlyRequest();
        clearReq.user_id = userId;
        api.clearCart(clearReq).enqueue(new retrofit2.Callback<BaseResponse>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                // Group items by product ID - lấy quantity từ Product.getQuantity() thay vì đếm số lần xuất hiện
                java.util.Map<String, Integer> qtyMap = new java.util.HashMap<>();
                java.util.Map<String, Product> productMap = new java.util.HashMap<>();
                for (Product p : localItems) {
                    String prodId = p.getId();
                    int currentQty = qtyMap.getOrDefault(prodId, 0);
                    qtyMap.put(prodId, currentQty + p.getQuantity()); // Lấy quantity từ Product, không phải đếm
                    productMap.put(prodId, p);
                }
                java.util.List<Product> uniqueProducts = new java.util.ArrayList<>(productMap.values());
                addItemsToServer(userId, uniqueProducts, qtyMap, 0, address, phone, total);
            }
            @Override
            public void onFailure(retrofit2.Call<BaseResponse> call, Throwable t) {
                // Group items by product ID - lấy quantity từ Product.getQuantity() thay vì đếm số lần xuất hiện
                java.util.Map<String, Integer> qtyMap = new java.util.HashMap<>();
                java.util.Map<String, Product> productMap = new java.util.HashMap<>();
                for (Product p : localItems) {
                    String prodId = p.getId();
                    int currentQty = qtyMap.getOrDefault(prodId, 0);
                    qtyMap.put(prodId, currentQty + p.getQuantity()); // Lấy quantity từ Product, không phải đếm
                    productMap.put(prodId, p);
                }
                java.util.List<Product> uniqueProducts = new java.util.ArrayList<>(productMap.values());
                addItemsToServer(userId, uniqueProducts, qtyMap, 0, address, phone, total);
            }
        });
    }

    private void addItemsToServer(int userId, java.util.List<Product> uniqueItems, java.util.Map<String, Integer> qtyMap, int index, String address, String phone, double total) {
        if (index >= uniqueItems.size()) {
            doCheckout(userId, address, phone);
            return;
        }

        ApiService api = RetrofitClient.getApiService();
        Product p = uniqueItems.get(index);
        final int qty = qtyMap.getOrDefault(p.getId(), 1);
        CartAddRequest req = new CartAddRequest();
        req.user_id = userId;
        try { req.product_id = Integer.parseInt(p.getId()); } catch (NumberFormatException e) { req.product_id = 0; }
        req.quantity = qty;
        
        // Lấy danh sách selected option value IDs từ product
        req.selected_option_values = p.getSelectedOptionValueIds();
        api.addToCart(req).enqueue(new retrofit2.Callback<BaseResponse>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                addItemsToServer(userId, uniqueItems, qtyMap, index + 1, address, phone, total);
            }
            @Override
            public void onFailure(retrofit2.Call<BaseResponse> call, Throwable t) {
                addItemsToServer(userId, uniqueItems, qtyMap, index + 1, address, phone, total);
            }
        });
    }

    private void doCheckout(int userId, String address, String phone) {
        // Validate lại một lần nữa trước khi checkout (final safety check)
        if (phone == null || phone.trim().isEmpty() || !isValidVietnamesePhone(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ. Vui lòng kiểm tra lại", Toast.LENGTH_LONG).show();
            EditText etPhone = findViewById(R.id.etShippingPhone);
            etPhone.setText("");
            etPhone.requestFocus();
            return; // Dừng ngay, không gọi API checkout
        }
        
        // Xác định shipping method dựa trên RadioButton được chọn
        String shippingMethod = "standard";
        if (rgShippingMethod.getCheckedRadioButtonId() == R.id.rbFast) {
            shippingMethod = "fast";
        } else if (rgShippingMethod.getCheckedRadioButtonId() == R.id.rbExpress) {
            shippingMethod = "express";
        }
        
        ApiService api = RetrofitClient.getApiService();
        UserOnlyRequest req = new UserOnlyRequest();
        req.user_id = userId;
        req.customer_address = address;
        req.customer_phone = phone;
        req.shipping_method = shippingMethod;
        req.shipping_fee = currentShippingFee;
        req.payment_method = currentPaymentMethod; // Thêm payment_method
        api.checkout(req).enqueue(new retrofit2.Callback<CheckoutResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CheckoutResponse> call, retrofit2.Response<CheckoutResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    CartManager.getCartItems().clear();
                    Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
                    // Truyền dữ liệu đơn hàng
                    intent.putExtra("order_address", address);
                    intent.putExtra("order_phone", phone);
                    // Lấy tên phương thức giao hàng
                    String shippingMethodName = "Giao hàng tiết kiệm";
                    if (rgShippingMethod.getCheckedRadioButtonId() == R.id.rbFast) {
                        shippingMethodName = "Giao hàng nhanh";
                    } else if (rgShippingMethod.getCheckedRadioButtonId() == R.id.rbExpress) {
                        shippingMethodName = "Giao hàng siêu tốc";
                    }
                    intent.putExtra("order_shipping_method", shippingMethodName);
                    // Lấy tên phương thức thanh toán
                    String paymentMethodName = "Chuyển khoản ngân hàng";
                    boolean isCod = currentPaymentMethod.equals("cod");
                    if (isCod) {
                        paymentMethodName = "Thanh toán khi nhận hàng";
                    }
                    intent.putExtra("order_payment_method", paymentMethodName);
                    intent.putExtra("is_cod_payment", isCod);
                    intent.putExtra("order_total", totalFinal + currentShippingFee);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<CheckoutResponse> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Không thể đặt hàng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validation số điện thoại Việt Nam: bắt đầu bằng 09, đúng 10 chữ số
    private boolean isValidVietnamesePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Loại bỏ khoảng trắng và ký tự đặc biệt
        String cleanPhone = phone.trim().replaceAll("[^0-9]", "");
        // Kiểm tra: bắt đầu bằng 09 và đúng 10 chữ số
        if (cleanPhone.length() != 10) {
            return false;
        }
        if (!cleanPhone.startsWith("09")) {
            return false;
        }
        // Kiểm tra thêm: tất cả 10 ký tự đều là số
        try {
            Long.parseLong(cleanPhone);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


