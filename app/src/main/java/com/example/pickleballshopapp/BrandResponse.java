package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BrandResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<BrandSimple> data;

    public boolean isSuccess() { return success; }
    public List<BrandSimple> getData() { return data; }

    public static class BrandSimple {
        @SerializedName("id") public int id;
        @SerializedName("name") public String name;
        @SerializedName("logo_url") public String logoUrl;
    }
}


