package com.example.pickleballshopapp;

public class UpdateUserRequest {
    private int user_id;
    private String full_name;
    private String email;
    private String phone;

    public UpdateUserRequest(int userId, String fullName, String email, String phone) {
        this.user_id = userId;
        this.full_name = fullName;
        this.email = email;
        this.phone = phone;
    }
}



