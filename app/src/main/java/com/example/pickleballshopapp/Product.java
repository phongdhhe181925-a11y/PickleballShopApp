package com.example.pickleballshopapp;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

public class Product implements Serializable {

    // @SerializedName dùng để khớp chính xác tên key trong JSON
    // ngay cả khi tên biến Java khác đi.

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("stock_quantity")
    private int stockQuantity;

    @SerializedName("description")
    private String description;

    @SerializedName("brand") // Khớp với tên cột trong CSDL/JSON
    private String brand;
    
    // Thuộc tính trạng thái
    @SerializedName("is_new")
    private int isNew; // Dùng int hoặc boolean

    @SerializedName("is_bestseller")
    private int isBestseller; // Dùng int hoặc boolean

    // Thuộc tính riêng
    @SerializedName("paddle_thickness_mm")
    private String paddleThickness; // Dùng String để dễ xử lý giá trị NULL

    @SerializedName("shoe_size_us")
    private String shoeSize;

    @SerializedName("packaging_count")
    private String packagingCount; // Dùng String để dễ xử lý giá trị NULL

    @SerializedName("category_id")
    private int categoryId; // Hoặc String tùy kiểu dữ liệu trong DB

    // Các trường mới cho product detail
    @SerializedName("images")
    private List<ImageItem> images; // Danh sách ảnh

    @SerializedName("options")
    private List<ProductOption> options; // Danh sách options

    @SerializedName("technical_specs")
    private String technicalSpecs; // Thông số kỹ thuật

    @SerializedName("has_description_tab")
    private int hasDescriptionTab; // Có tab mô tả (0 hoặc 1)

    @SerializedName("has_specs_tab")
    private int hasSpecsTab; // Có tab thông số kỹ thuật (0 hoặc 1)

    @SerializedName("total_stock")
    private int totalStock; // Tổng số lượng tồn kho (tính từ options hoặc stock_quantity)

    private int quantity = 1; // Số lượng mỗi sản phẩm trong giỏ hàng (mặc định 1)

    // Inner class cho ImageItem
    public static class ImageItem implements Serializable {
        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("display_order")
        private int displayOrder;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(int displayOrder) {
            this.displayOrder = displayOrder;
        }
    }

// ... (các getter khác)

    public int getCategoryId() { // Hoặc String
        return categoryId;
    }
    // Thêm getter cho các cột mới
    public int getIsNew() { return isNew; }
    public int getIsBestseller() { return isBestseller; }
    public String getPaddleThickness() { return paddleThickness; }
    public String getShoeSize() { return shoeSize; }
    public String getPackagingCount() { return packagingCount; }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
// ... (bên dưới các hàm getter khác như getDescription())

    // Hàm getter mới cho brand
    public String getBrand() {
        return brand;
    }

    // Copy constructor
    public Product(Product other) {
        this.id = other.id;
        this.name = other.name;
        this.price = other.price;
        this.imageUrl = other.imageUrl;
        this.stockQuantity = other.stockQuantity;
        this.description = other.description;
        this.brand = other.brand;
        this.isNew = other.isNew;
        this.isBestseller = other.isBestseller;
        this.paddleThickness = other.paddleThickness;
        this.shoeSize = other.shoeSize;
        this.packagingCount = other.packagingCount;
        this.categoryId = other.categoryId;
        this.quantity = other.quantity;
        this.images = other.images != null ? new ArrayList<>(other.images) : null;
        this.options = other.options != null ? new ArrayList<>(other.options) : null;
        this.technicalSpecs = other.technicalSpecs;
        this.hasDescriptionTab = other.hasDescriptionTab;
        this.hasSpecsTab = other.hasSpecsTab;
        this.totalStock = other.totalStock;
    }

    // Getter methods (để lấy dữ liệu ra)
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    // Getters và Setters cho các trường mới
    public List<ImageItem> getImages() {
        return images;
    }

    public void setImages(List<ImageItem> images) {
        this.images = images;
    }

    public List<ProductOption> getOptions() {
        return options;
    }

    public void setOptions(List<ProductOption> options) {
        this.options = options;
    }

    public String getTechnicalSpecs() {
        return technicalSpecs;
    }

    public void setTechnicalSpecs(String technicalSpecs) {
        this.technicalSpecs = technicalSpecs;
    }

    public boolean hasDescriptionTab() {
        return hasDescriptionTab == 1;
    }

    public void setHasDescriptionTab(int hasDescriptionTab) {
        this.hasDescriptionTab = hasDescriptionTab;
    }

    public boolean hasSpecsTab() {
        return hasSpecsTab == 1;
    }

    public void setHasSpecsTab(int hasSpecsTab) {
        this.hasSpecsTab = hasSpecsTab;
    }

    public int getTotalStock() {
        // Nếu không có options, dùng stock_quantity, nếu có thì dùng total_stock
        if (options == null || options.isEmpty()) {
            return stockQuantity;
        }
        return totalStock > 0 ? totalStock : stockQuantity;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    // Lấy ảnh đầu tiên hoặc imageUrl
    public String getMainImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getImageUrl();
        }
        return imageUrl;
    }

    // Lấy số lượng tồn kho hiện tại dựa trên options đã chọn
    // Số lượng kho luôn được quản lý qua Options, không dùng stockQuantity nữa
    public int getCurrentStockQuantity() {
        if (options == null || options.isEmpty()) {
            // Không có options: trả về 0 (phải có options để có stock)
            return 0;
        }
        
        // Kiểm tra xem tất cả options đã được chọn chưa
        for (ProductOption option : options) {
            if (option.getSelectedValue() == null) {
                // Có option chưa chọn: trả về 0
                return 0;
            }
        }
        
        // Tất cả options đã được chọn: lấy quantity nhỏ nhất từ các option value đã chọn
        // (vì mỗi option value có stock riêng, stock thực tế là min của các stock)
        int minStock = Integer.MAX_VALUE;
        for (ProductOption option : options) {
            if (option.getSelectedValue() != null) {
                int qty = option.getSelectedValue().getQuantity();
                if (qty < minStock) {
                    minStock = qty;
                }
            }
        }
        return minStock == Integer.MAX_VALUE ? 0 : minStock;
    }
    
    /**
     * Lấy danh sách ID của các option values đã được chọn
     * @return List<Integer> chứa các ID của option values, hoặc null nếu không có options hoặc chưa chọn đủ
     */
    public List<Integer> getSelectedOptionValueIds() {
        if (options == null || options.isEmpty()) {
            return null;
        }
        
        List<Integer> selectedIds = new ArrayList<>();
        
        // Kiểm tra xem tất cả options đã được chọn chưa
        for (ProductOption option : options) {
            if (option.getSelectedValue() == null) {
                // Có option chưa chọn: trả về null
                return null;
            }
            // Thêm ID của selected value vào danh sách
            selectedIds.add(option.getSelectedValue().getValueId());
        }
        
        return selectedIds.isEmpty() ? null : selectedIds;
    }
    
    /**
     * Lấy chuỗi text hiển thị các option values đã chọn
     * @return String chứa tên các option values đã chọn, hoặc null nếu không có
     */
    public String getSelectedOptionValuesText() {
        if (options == null || options.isEmpty()) {
            return null;
        }
        
        List<String> selectedValues = new ArrayList<>();
        
        // Lấy tên của các selected values
        for (ProductOption option : options) {
            if (option.getSelectedValue() != null) {
                selectedValues.add(option.getSelectedValue().getValue());
            }
        }
        
        if (selectedValues.isEmpty()) {
            return null;
        }
        
        // Nối các giá trị bằng dấu phẩy
        return String.join(", ", selectedValues);
    }
}
