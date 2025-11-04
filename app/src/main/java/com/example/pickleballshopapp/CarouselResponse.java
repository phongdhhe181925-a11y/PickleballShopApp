package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CarouselResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<CarouselImage> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<CarouselImage> getData() {
        return data;
    }

    public void setData(List<CarouselImage> data) {
        this.data = data;
    }
}






