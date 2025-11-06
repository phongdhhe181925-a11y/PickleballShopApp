package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteRegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnCompleteRegister;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);

        // Lấy user_id từ Intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        // Ánh xạ views
        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCompleteRegister = findViewById(R.id.btnCompleteRegister);

        // Tự động điền dữ liệu từ Intent (nếu có)
        String savedFullName = getIntent().getStringExtra("full_name");
        String savedPassword = getIntent().getStringExtra("password");
        if (savedFullName != null && !savedFullName.isEmpty()) {
            etFullName.setText(savedFullName);
        }
        if (savedPassword != null && !savedPassword.isEmpty()) {
            etPassword.setText(savedPassword);
            etConfirmPassword.setText(savedPassword);
        }

        // Xử lý nút Hoàn tất đăng ký
        btnCompleteRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (fullName.isEmpty()) {
                    Toast.makeText(CompleteRegisterActivity.this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    Toast.makeText(CompleteRegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(CompleteRegisterActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                completeRegister(fullName, password);
            }
        });
    }

    private void completeRegister(String fullName, String password) {
        btnCompleteRegister.setEnabled(false);
        btnCompleteRegister.setText("Đang xử lý...");

        ApiService apiService = RetrofitClient.getApiService();
        RegisterWithOtpRequest request = new RegisterWithOtpRequest(userId, fullName, password);
        Call<AuthResponse> call = apiService.registerWithOtp(request);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnCompleteRegister.setEnabled(true);
                btnCompleteRegister.setText("Hoàn tất đăng ký");

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(CompleteRegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển về màn hình đăng nhập
                        Intent intent = new Intent(CompleteRegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CompleteRegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CompleteRegisterActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnCompleteRegister.setEnabled(true);
                btnCompleteRegister.setText("Hoàn tất đăng ký");
                Toast.makeText(CompleteRegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

