package com.example.pickleballshopapp;

public class LoginRequest {
    private String email;
    private String password;

    // === HÀM KHỞI TẠO (CONSTRUCTOR) BẠN BỊ THIẾU ===
    // Nó nhận 2 String và gán vào các biến
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    // =============================================
}