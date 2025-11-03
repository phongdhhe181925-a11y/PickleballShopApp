package com.example.pickleballshopapp;

public class OrderDto {
    private int id;
    private String order_date;
    private String status;
    private double total_money;
    private String customer_address;
    private String customer_phone;
    private String cancel_reason;
    private String cancelled_by;
    private String cancelled_at;

    public int getId() { return id; }
    public String getOrder_date() { return order_date; }
    public String getStatus() { return status; }
    public double getTotal_money() { return total_money; }
    public String getCustomer_address() { return customer_address; }
    public String getCustomer_phone() { return customer_phone; }
    public String getCancel_reason() { return cancel_reason; }
    public String getCancelled_by() { return cancelled_by; }
    public String getCancelled_at() { return cancelled_at; }
}


