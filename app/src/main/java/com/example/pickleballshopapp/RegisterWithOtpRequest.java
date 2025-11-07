package com.example.pickleballshopapp;

public class RegisterWithOtpRequest {
    private int user_id;
    private String full_name;
    private String password;

    public RegisterWithOtpRequest(int userId, String fullName, String password) {
        this.user_id = userId;
        this.full_name = fullName;
        this.password = password;
    }
}



