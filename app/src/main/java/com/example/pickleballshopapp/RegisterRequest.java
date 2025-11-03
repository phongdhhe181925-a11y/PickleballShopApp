package com.example.pickleballshopapp;

// Class này chỉ dùng để GỬI dữ liệu, không cần @SerializedName
public class RegisterRequest {
    private String full_name;
    private String email;
    private String password;

    // === HÀM KHỞI TẠO (CONSTRUCTOR) BẠN BỊ THIẾU ===
    // Nó nhận 3 String và gán vào các biến
    public RegisterRequest(String full_name, String email, String password) {
        this.full_name = full_name;
        this.email = email;
        this.password = password;
    }
    // =============================================
}