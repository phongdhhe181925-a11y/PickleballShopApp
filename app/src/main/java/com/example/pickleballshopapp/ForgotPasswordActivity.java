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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        // Ánh xạ views
        etEmail = findViewById(R.id.etEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        // Xử lý nút Gửi OTP
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendOtpForResetPassword(email);
            }
        });
    }

    private void sendOtpForResetPassword(String email) {
        btnSendOtp.setEnabled(false);
        btnSendOtp.setText("Đang gửi...");

        ApiService apiService = RetrofitClient.getApiService();
        SendOtpRequest request = new SendOtpRequest(email, "reset_password");
        Call<SendOtpResponse> call = apiService.sendOtp(request);

        call.enqueue(new Callback<SendOtpResponse>() {
            @Override
            public void onResponse(Call<SendOtpResponse> call, Response<SendOtpResponse> response) {
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi mã OTP");

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Chuyển sang màn hình xác thực OTP
                        Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("user_id", response.body().getUserId());
                        intent.putExtra("purpose", "reset_password");
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SendOtpResponse> call, Throwable t) {
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi mã OTP");
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


