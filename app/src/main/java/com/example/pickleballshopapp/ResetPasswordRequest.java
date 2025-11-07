package com.example.pickleballshopapp;

public class ResetPasswordRequest {
    private int user_id;
    private String new_password;
    private String confirm_password;

    public ResetPasswordRequest(int userId, String newPassword, String confirmPassword) {
        this.user_id = userId;
        this.new_password = newPassword;
        this.confirm_password = confirmPassword;
    }
}


