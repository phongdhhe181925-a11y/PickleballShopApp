package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ProductOptionValue implements Serializable {
    @SerializedName("value_id")
    private int valueId;

    @SerializedName("value")
    private String value;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price_adjustment")
    private double priceAdjustment;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("display_order")
    private int displayOrder;

    public ProductOptionValue() {
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAdjustment() {
        return priceAdjustment;
    }

    public void setPriceAdjustment(double priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}



