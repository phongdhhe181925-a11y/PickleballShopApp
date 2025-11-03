package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ProductOption implements Serializable {
    @SerializedName("option_id")
    private int optionId;

    @SerializedName("option_name")
    private String optionName;

    @SerializedName("option_type")
    private String optionType;

    @SerializedName("display_order")
    private int displayOrder;

    @SerializedName("values")
    private List<ProductOptionValue> values;

    private ProductOptionValue selectedValue; // Option value đã chọn

    public ProductOption() {
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<ProductOptionValue> getValues() {
        return values;
    }

    public void setValues(List<ProductOptionValue> values) {
        this.values = values;
    }

    public ProductOptionValue getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(ProductOptionValue selectedValue) {
        this.selectedValue = selectedValue;
    }
}



