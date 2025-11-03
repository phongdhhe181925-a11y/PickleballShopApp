package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Product> data;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public List<Product> getData() {
        return data;
    }
}
