package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Hiện nút Back (mũi tên) và tiêu đề nếu dùng ActionBar mặc định
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đăng nhập");
        }

// 1. Ánh xạ các TextView
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

// 2. Xử lý khi bấm vào "Đăng ký ngay"
        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình Đăng ký
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

// 3. Xử lý khi bấm vào "Quên mật khẩu"
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình Quên mật khẩu
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
// 3. Ánh xạ các View
// (Bấm Alt+Enter để import 'EditText' và 'Button')
        EditText etEmail = findViewById(R.id.etLoginEmail);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

// 4. Xử lý khi bấm nút "Đăng Nhập"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 5. Tạo "gói hàng" (Request)
                LoginRequest loginRequest = new LoginRequest(email, password);

                // 6. Gọi API
                callLoginApi(loginRequest);
            }
        });
    }
    // === DÁN HÀM MỚI NÀY VÀO BÊN NGOÀI onCreate ===

    private void callLoginApi(LoginRequest loginRequest) {
        // (Bấm Alt+Enter để import 'Log', 'Call', 'Callback', 'Response', 'LoginResponse')

        ApiService apiService = RetrofitClient.getApiService();
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        // ... (bên trong onResponse và if (response.body().isSuccess()))

// Lấy thông tin user
                        User user = response.body().getData();
                        String userId = user.getUserId();
                        String userName = user.getFullName();
                        String userEmail = user.getEmail();
                        String userPhone = user.getPhone();

// === BƯỚC 2: LƯU PHIÊN ĐĂNG NHẬP ===
// Khởi tạo SessionManager
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
// Gọi hàm lưu
                        sessionManager.createLoginSession(userId, userName, userEmail, userPhone);
// ==================================

// Đóng màn hình Login và quay lại Giỏ hàng
                        finish();

                    } else {
                        // Đăng nhập thất bại (sai pass, email không tồn tại)
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_FAILURE", "Lỗi: " + t.getMessage());
            }
        });
    }
    // ===============================================
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Xử lý khi bấm nút Back (mũi tên)
        if (item.getItemId() == android.R.id.home) {
            // Quay về MainActivity, xóa các activity trên cùng
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Đóng LoginActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}