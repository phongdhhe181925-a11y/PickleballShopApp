package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;

public class OrderDetailDto {
    private int product_id;
    private String product_name;
    private int quantity;
    private double price_at_purchase;
    private String image_url;
    private double subtotal;
    @SerializedName("selected_options_text")
    private String selected_options_text;

    public int getProduct_id() { return product_id; }
    public String getProduct_name() { return product_name; }
    public int getQuantity() { return quantity; }
    public double getPrice_at_purchase() { return price_at_purchase; }
    public String getImage_url() { return image_url; }
    public double getSubtotal() { return subtotal; }
    public String getSelected_options_text() { return selected_options_text; }
    public void setSelected_options_text(String selected_options_text) { this.selected_options_text = selected_options_text; }
}














