package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private User data; // Dùng lại class User ở trên

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public User getData() { return data; }
}