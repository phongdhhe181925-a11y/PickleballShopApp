package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup logo clickable để quay về trang chủ
        android.widget.ImageView logo = toolbar.findViewById(R.id.toolbar_logo);
        if (logo != null) {
            logo.setOnClickListener(v -> {
                Intent intent = new Intent(ThankYouActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        TextView tvTitle = findViewById(R.id.tvThanksTitle);
        TextView tvMessage = findViewById(R.id.tvThanksMessage);
        Button btnContinue = findViewById(R.id.btnContinueShopping);
        android.view.View orderDetailsCard = findViewById(R.id.orderDetailsCard);
        TextView tvOrderPhone = findViewById(R.id.tvOrderPhone);
        TextView tvOrderAddress = findViewById(R.id.tvOrderAddress);
        TextView tvOrderShippingMethod = findViewById(R.id.tvOrderShippingMethod);
        TextView tvOrderPaymentMethod = findViewById(R.id.tvOrderPaymentMethod);

        SessionManager sessionManager = new SessionManager(this);
        String name = sessionManager.getFullName();
        tvTitle.setText("Cảm ơn, " + name + "!");

        // Nhận dữ liệu đơn hàng từ Intent
        String orderAddress = getIntent().getStringExtra("order_address");
        String orderPhone = getIntent().getStringExtra("order_phone");
        String orderShippingMethod = getIntent().getStringExtra("order_shipping_method");
        String orderPaymentMethod = getIntent().getStringExtra("order_payment_method");
        double orderTotal = getIntent().getDoubleExtra("order_total", 0.0);
        boolean isCod = getIntent().getBooleanExtra("is_cod_payment", false);

        // Hiển thị text xác nhận đơn hàng dựa trên phương thức thanh toán
        if (isCod) {
            // Text cho COD
            String codText = "- Đối với đơn hàng không phải sản phẩm đặc biệt, trị giá dưới 3.000.000đ, khách hàng vui lòng thanh toán cho đơn vị vận chuyển khi nhận hàng.\n\n";
            codText += "- Đối với các đơn hàng đặc biệt hoặc trị giá trên 3.000.000đ, khách hàng vui lòng chuyển khoản cọc 10% giá trị đơn hàng và thanh toán phần còn lại khi nhận hàng. Số tài khoản nhận cọc:\n\n";
            codText += "--- Ngân hàng: MBBank\n\n";
            codText += "--- Số tài khoản: 696969696969\n\n";
            codText += "--- Chủ tài khoản: LA LAM PHONG\n\n";
            codText += "Vui lòng gọi hotline hoặc nhắn qua Zalo: 0969.696.969 để được hỗ trợ về đơn hàng.";
            tvMessage.setText(codText);
        } else {
            // Text cho chuyển khoản (giữ nguyên text hiện tại)
            tvMessage.setText("Khách hàng sau khi đặt đơn hàng vui lòng chuyển khoản vào số tài khoản dưới đây:\n\n- Ngân hàng MBBankk\n\n- Số tài khoản: 0002811200900\n\n- Chủ tài khoản: DINH HOANG PHUC\n\nSau khi xác nhận khoản thanh toán, PickleballKing sẽ chuẩn bị đơn hàng để giao tới tay khách hàng theo thông tin đã cung cấp.");
        }

        // Hiển thị chi tiết đơn hàng nếu có dữ liệu
        if (orderAddress != null && orderPhone != null && orderShippingMethod != null && orderPaymentMethod != null) {
            orderDetailsCard.setVisibility(android.view.View.VISIBLE);
            tvOrderPhone.setText(orderPhone);
            tvOrderAddress.setText(orderAddress);
            tvOrderShippingMethod.setText(orderShippingMethod);
            
            // Format tổng tiền
            java.text.NumberFormat vn = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
            String paymentText = orderPaymentMethod + ": " + vn.format(orderTotal);
            tvOrderPaymentMethod.setText(paymentText);
        }

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}



