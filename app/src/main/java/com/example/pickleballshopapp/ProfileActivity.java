package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup the common toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        SessionManager sessionManager = new SessionManager(this);

        EditText etFullName = findViewById(R.id.etProfileFullName);
        EditText etEmail = findViewById(R.id.etProfileEmail);
        EditText etPhone = findViewById(R.id.etProfilePhone);
        Button btnSave = findViewById(R.id.btnSaveProfile);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnOrderHistory = findViewById(R.id.btnOrderHistory);

        // Load thông tin hiện tại
        etFullName.setText(sessionManager.getFullName());
        etEmail.setText(sessionManager.getEmail());
        etPhone.setText(sessionManager.getPhone());

        btnSave.setOnClickListener(v -> {
            String newName = etFullName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(newEmail)) {
                Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra email format cơ bản
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validation số điện thoại (nếu có nhập)
            if (!TextUtils.isEmpty(newPhone)) {
                if (!isValidVietnamesePhone(newPhone)) {
                    Toast.makeText(this, "Số điện thoại không hợp lệ. Vui lòng nhập số bắt đầu bằng 09 và có đúng 10 chữ số", Toast.LENGTH_LONG).show();
                    etPhone.setText("");
                    etPhone.requestFocus();
                    return;
                }
            }

            int userId = 0;
            try {
                userId = Integer.parseInt(sessionManager.getUserId());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Lỗi tài khoản, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button để tránh double click
            btnSave.setEnabled(false);
            btnSave.setText("Đang lưu...");

            ApiService api = RetrofitClient.getApiService();
            api.updateUser(new UpdateUserRequest(userId, newName, newEmail, newPhone))
                    .enqueue(new retrofit2.Callback<BaseResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                            btnSave.setEnabled(true);
                            btnSave.setText("Lưu thay đổi");

                            if (response.isSuccessful() && response.body() != null) {
                                BaseResponse body = response.body();
                                if (body.isSuccess()) {
                                    sessionManager.updateUserInfo(newName, newEmail, newPhone);
                                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ProfileActivity.this, body.getMessage() != null ? body.getMessage() : "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<BaseResponse> call, Throwable t) {
                            btnSave.setEnabled(true);
                            btnSave.setText("Lưu thay đổi");
                            Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnOrderHistory.setOnClickListener(v -> {
            Intent i = new Intent(this, OrderHistoryActivity.class);
            startActivity(i);
        });
    }

    // Validation số điện thoại Việt Nam: bắt đầu bằng 09, đúng 10 chữ số
    private boolean isValidVietnamesePhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        // Loại bỏ khoảng trắng và ký tự đặc biệt
        String cleanPhone = phone.trim().replaceAll("[^0-9]", "");
        // Kiểm tra: bắt đầu bằng 09 và đúng 10 chữ số
        return cleanPhone.length() == 10 && cleanPhone.startsWith("09");
    }

    // This is no longer needed as ToolbarUtils handles navigation
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
