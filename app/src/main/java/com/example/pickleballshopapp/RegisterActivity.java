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

    private Button btnRegister;

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
        btnRegister = findViewById(R.id.btnRegister);

// 4. Xử lý khi bấm nút "Đăng Ký"
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 5. Kiểm tra dữ liệu
                if (fullName.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 6. Gửi OTP để xác thực email (lưu tạm fullName và password)
                sendOtpForRegistration(email, fullName, password);
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
    // === HÀM GỬI OTP CHO ĐĂNG KÝ ===

    private void sendOtpForRegistration(String email, String fullName, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang gửi...");

        ApiService apiService = RetrofitClient.getApiService();
        SendOtpRequest request = new SendOtpRequest(email, "verify_email");
        Call<SendOtpResponse> call = apiService.sendOtp(request);

        call.enqueue(new Callback<SendOtpResponse>() {
            @Override
            public void onResponse(Call<SendOtpResponse> call, Response<SendOtpResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng Ký");

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Chuyển sang màn hình xác thực OTP (truyền kèm fullName và password)
                        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("user_id", response.body().getUserId());
                        intent.putExtra("purpose", "verify_email");
                        intent.putExtra("email", email);
                        intent.putExtra("full_name", fullName);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SendOtpResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng Ký");
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