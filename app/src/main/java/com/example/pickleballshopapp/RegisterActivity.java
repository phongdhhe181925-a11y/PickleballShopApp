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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);
// 1. Ánh xạ nút "Đăng nhập"
// (Bấm Alt+Enter vào "TextView" để import class)
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
// ... (code của tvGoToLogin.setOnClickListener(...) của bạn)

// 3. Ánh xạ các View (EditTexts và Button)
// (Bấm Alt+Enter để import 'EditText' và 'Button')
        EditText etFullName = findViewById(R.id.etRegisterFullName);
        EditText etEmail = findViewById(R.id.etRegisterEmail);
        EditText etPassword = findViewById(R.id.etRegisterPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

// 4. Xử lý khi bấm nút "Đăng Ký"
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy text từ các ô nhập liệu
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 5. Kiểm tra dữ liệu (đơn giản)
                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return; // Dừng lại nếu có ô trống
                }

                // 6. Tạo "gói hàng" (Request) để gửi đi
                RegisterRequest registerRequest = new RegisterRequest(fullName, email, password);

                // 7. Gọi API
                callRegisterApi(registerRequest);
            }
        });

// 2. Xử lý khi bấm vào text
        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng màn hình Đăng ký hiện tại và quay lại
                // màn hình Đăng nhập (LoginActivity) trước đó.
                finish();
            }
        });
    }
    // === DÁN HÀM MỚI NÀY VÀO BÊN NGOÀI onCreate ===

    private void callRegisterApi(RegisterRequest registerRequest) {
        // (Bấm Alt+Enter để import 'Log', 'Call', 'Callback', 'Response', 'RetrofitClient')

        // Lấy ApiService từ RetrofitClient
        ApiService apiService = RetrofitClient.getApiService();

        // Gọi API registerUser
        Call<AuthResponse> call = apiService.registerUser(registerRequest);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // Nếu API trả về thành công (success: true)
                    if (response.body().isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                        // Đăng ký xong, đóng màn hình này và quay lại Đăng nhập
                        finish();
                    } else {
                        // Nếu API trả về thất bại (success: false, vd: email đã tồn tại)
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Nếu server trả về lỗi (ví dụ: 404, 500)
                    Toast.makeText(RegisterActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Nếu lỗi kết nối (ví dụ: mất mạng, sai BASE_URL)
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_FAILURE", "Lỗi: " + t.getMessage());
            }
        });
    }
    // ===============================================
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Menu is provided by toolbar_common via app:menu
        return true;
    }
}