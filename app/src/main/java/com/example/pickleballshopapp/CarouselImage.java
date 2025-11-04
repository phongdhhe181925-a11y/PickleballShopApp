package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;

public class CarouselImage {
    @SerializedName("id")
    private int id;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("display_order")
    private int display_order;

    @SerializedName("overlay_text")
    private String overlay_text;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(int display_order) {
        this.display_order = display_order;
    }

    public String getOverlay_text() {
        return overlay_text;
    }

    public void setOverlay_text(String overlay_text) {
        this.overlay_text = overlay_text;
    }
}

