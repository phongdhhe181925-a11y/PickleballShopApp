package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    // Getters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
