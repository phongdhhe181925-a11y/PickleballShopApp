package com.example.pickleballshopapp;

public class CancelOrderRequest {
    private int user_id;
    private int order_id;
    private String reason;

    public CancelOrderRequest(int userId, int orderId, String reason) {
        this.user_id = userId;
        this.order_id = orderId;
        this.reason = reason != null ? reason : "";
    }
}















