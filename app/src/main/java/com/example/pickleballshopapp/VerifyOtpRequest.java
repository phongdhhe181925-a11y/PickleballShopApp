package com.example.pickleballshopapp;

public class VerifyOtpRequest {
    private int user_id;
    private String otp_code;
    private String purpose; // "verify_email" hoặc "reset_password"

    public VerifyOtpRequest(int userId, String otpCode, String purpose) {
        this.user_id = userId;
        this.otp_code = otpCode;
        this.purpose = purpose;
    }
}


