package com.example.pickleballshopapp;

public class UserOnlyRequest {
    public int user_id;
    public String customer_address; // optional for checkout
    public String customer_phone;   // optional for checkout
    public String shipping_method;  // Phương thức vận chuyển: "standard", "fast", "express"
    public double shipping_fee;     // Phí vận chuyển
    public String payment_method;   // Phương thức thanh toán: "bank_transfer", "cod"
}













