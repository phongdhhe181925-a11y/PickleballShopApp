package com.example.pickleballshopapp;

import java.util.List;

public class OrderDetailsResponse {
    private boolean success;
    private List<OrderDetailDto> data;
    private String message;

    public boolean isSuccess() { return success; }
    public List<OrderDetailDto> getData() { return data; }
    public String getMessage() { return message; }
}


















