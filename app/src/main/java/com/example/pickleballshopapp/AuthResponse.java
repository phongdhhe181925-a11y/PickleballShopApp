package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}