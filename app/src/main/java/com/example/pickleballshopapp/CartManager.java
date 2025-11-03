package com.example.pickleballshopapp;

import java.util.ArrayList;
import java.util.List;

// Đây là lớp đặc biệt dùng để quản lý giỏ hàng
// Nó dùng "static" để dữ liệu giỏ hàng được chia sẻ
// giữa tất cả các Activity (màn hình)

public class CartManager {

    // 1. Khởi tạo danh sách giỏ hàng
    // "static" nghĩa là danh sách này chỉ có 1 và dùng chung cho toàn bộ app
    private static List<Product> cartItems = new ArrayList<>();

    // 2. Hàm để thêm sản phẩm vào giỏ
    public static void addToCart(Product product, int quantity) {
        // Kiểm tra xem sản phẩm với cùng ID và cùng selected options đã có trong giỏ chưa
        List<Integer> newProductOptionIds = product.getSelectedOptionValueIds();
        
        for (Product p : cartItems) {
            if (p.getId().equals(product.getId())) {
                // So sánh selected options
                List<Integer> existingProductOptionIds = p.getSelectedOptionValueIds();
                
                // Nếu cả 2 đều null hoặc rỗng (không có options) hoặc có cùng selected options
                if (areOptionIdsEqual(newProductOptionIds, existingProductOptionIds)) {
                    // Cùng sản phẩm và cùng options: tăng số lượng
                    p.setQuantity(p.getQuantity() + quantity);
                    return;
                }
                // Nếu khác options: tiếp tục tìm hoặc thêm mới
            }
        }
        
        // Chưa có sản phẩm này với options này: thêm mới
        Product productCopy = new Product(product);
        productCopy.setQuantity(quantity);
        cartItems.add(productCopy);
    }
    
    // Hàm so sánh 2 danh sách option value IDs
    private static boolean areOptionIdsEqual(List<Integer> list1, List<Integer> list2) {
        if (list1 == null && list2 == null) {
            return true; // Cả 2 đều không có options hoặc chưa chọn đủ options
        }
        if (list1 == null || list2 == null) {
            return false; // Một cái có, một cái không
        }
        if (list1.size() != list2.size()) {
            return false; // Khác số lượng options
        }
        // So sánh từng ID (sắp xếp để so sánh, vì thứ tự có thể khác)
        List<Integer> sorted1 = new ArrayList<>(list1);
        List<Integer> sorted2 = new ArrayList<>(list2);
        sorted1.sort(null);
        sorted2.sort(null);
        
        for (int i = 0; i < sorted1.size(); i++) {
            if (!sorted1.get(i).equals(sorted2.get(i))) {
                return false;
            }
        }
        return true; // Tất cả đều giống nhau
    }

    // Overload giữ cho code cũ không lỗi
    public static void addToCart(Product product) {
        addToCart(product, 1);
    }

    // 3. Hàm để lấy tất cả sản phẩm trong giỏ
    public static List<Product> getCartItems() {
        return cartItems;
    }

    // (Sau này bạn có thể thêm hàm xóa, hàm tính tổng tiền...)
}