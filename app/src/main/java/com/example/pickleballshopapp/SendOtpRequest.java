package com.example.pickleballshopapp;

public class SendOtpRequest {
    private String email;
    private String purpose; // "verify_email" hoặc "reset_password"

    public SendOtpRequest(String email, String purpose) {
        this.email = email;
        this.purpose = purpose;
    }
}






