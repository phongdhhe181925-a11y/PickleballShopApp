package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtpCode;
    private Button btnVerifyOtp;
    private TextView tvResendOtp;
    private int userId;
    private String purpose; // "verify_email" hoặc "reset_password"
    private String email;
    private String fullName; // Cho đăng ký
    private String password; // Cho đăng ký

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Lấy dữ liệu từ Intent
        userId = getIntent().getIntExtra("user_id", -1);
        purpose = getIntent().getStringExtra("purpose");
        email = getIntent().getStringExtra("email");
        fullName = getIntent().getStringExtra("full_name"); // Có thể null nếu là reset password
        password = getIntent().getStringExtra("password"); // Có thể null nếu là reset password

        if (userId == -1 || purpose == null) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        // Ánh xạ views
        etOtpCode = findViewById(R.id.etOtpCode);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvResendOtp = findViewById(R.id.tvResendOtp);

        // Thêm underline cho text "Gửi lại mã OTP"
        SpannableString content = new SpannableString("Gửi lại mã OTP");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvResendOtp.setText(content);

        // Xử lý nút Xác thực
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpCode = etOtpCode.getText().toString().trim();

                if (otpCode.isEmpty() || otpCode.length() != 6) {
                    Toast.makeText(OtpVerificationActivity.this, "Vui lòng nhập mã OTP 6 chữ số", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyOtp(otpCode);
            }
        });

        // Xử lý nút Gửi lại OTP
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp();
            }
        });
    }

    private void verifyOtp(String otpCode) {
        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Đang xác thực...");

        ApiService apiService = RetrofitClient.getApiService();
        VerifyOtpRequest request = new VerifyOtpRequest(userId, otpCode, purpose);
        Call<BaseResponse> call = apiService.verifyOtp(request);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác thực");

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Xác thực thành công
                        if ("verify_email".equals(purpose)) {
                            // Tự động hoàn tất đăng ký với fullName và password đã nhập
                            if (fullName != null && password != null && !fullName.isEmpty() && !password.isEmpty()) {
                                completeRegistration(userId, fullName, password);
                            } else {
                                Toast.makeText(OtpVerificationActivity.this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        } else if ("reset_password".equals(purpose)) {
                            // Chuyển sang màn hình đặt lại mật khẩu
                            Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("user_id", userId);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác thực");
                Toast.makeText(OtpVerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resendOtp() {
        tvResendOtp.setEnabled(false);
        tvResendOtp.setText("Đang gửi...");

        ApiService apiService = RetrofitClient.getApiService();
        SendOtpRequest request = new SendOtpRequest(email, purpose);
        Call<SendOtpResponse> call = apiService.sendOtp(request);

        call.enqueue(new Callback<SendOtpResponse>() {
            @Override
            public void onResponse(Call<SendOtpResponse> call, Response<SendOtpResponse> response) {
                tvResendOtp.setEnabled(true);
                tvResendOtp.setText("Gửi lại mã OTP");

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        userId = response.body().getUserId(); // Cập nhật user_id nếu có thay đổi
                        Toast.makeText(OtpVerificationActivity.this, "Mã OTP mới đã được gửi!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SendOtpResponse> call, Throwable t) {
                tvResendOtp.setEnabled(true);
                tvResendOtp.setText("Gửi lại mã OTP");
                Toast.makeText(OtpVerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void completeRegistration(int userId, String fullName, String password) {
        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Đang hoàn tất...");

        ApiService apiService = RetrofitClient.getApiService();
        RegisterWithOtpRequest request = new RegisterWithOtpRequest(userId, fullName, password);
        Call<AuthResponse> call = apiService.registerWithOtp(request);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác thực");

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(OtpVerificationActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển về màn hình đăng nhập
                        Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác thực");
                Toast.makeText(OtpVerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

