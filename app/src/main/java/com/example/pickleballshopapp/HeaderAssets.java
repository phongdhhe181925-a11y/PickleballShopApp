package com.example.pickleballshopapp;

import androidx.annotation.Nullable;

public final class HeaderAssets {

    private HeaderAssets() {}

    public static final String RACKET = "https://pickleplay.vn/cdn/shop/files/7a4489bfb4bfa071faf229464213dc1_33a00c7a-6c67-481f-a2c5-21b9bed6e01e.png?v=1736403234&width=1600";
    public static final String BALLS  = "https://pickleplay.vn/cdn/shop/collections/LT_Pro_48_Pickleball.webp?v=1759898499&width=1200";
    public static final String SHOES  = "https://pickleplay.vn/cdn/shop/collections/KSwissxMcLaren-Homepage_Banner-Launch_2500x880_8edc87df-0016-4ffd-9623-bc5f64b38efb.webp?v=1736655355&width=2000";
    public static final String BEST_SELLER = "https://pickleplay.vn/cdn/shop/collections/Pickleball_Demo_Program_Page_Banner-min_950ae904-f5cb-464f-acaf-27dfb5ce25c4.webp?v=1735797377&width=1400";
    public static final String JOOLA = "https://pickleplay.vn/cdn/shop/collections/ben-johns-perseus-3_150x150xcrop.webp?v=1736654203&width=2000";
    public static final String ASICS = "https://pickleplay.vn/cdn/shop/collections/1071A091_004_SB_FR_GLB.webp?v=1738312345&width=1800";
    public static final String BABOLAT = "https://pickleplay.vn/cdn/shop/collections/babolat-jet-mach-3-toe-drag-protection-21923013-main.webp?v=1749794593&width=2000";
    public static final String ON = "https://pickleplay.vn/cdn/shop/collections/on_shoe.png?v=1738312123&width=1800";
    public static final String WILSON = "https://pickleplay.vn/cdn/shop/collections/Wilson-Rush-Pro-Ace-PickerTop-10-Giay-Pickleball-Tot-Nhat-Hoc-Vien-VNTA.jpg?v=1749794821&width=700";

    public static String urlForCategory(String category) {
        if ("racket".equalsIgnoreCase(category)) return RACKET;
        if ("balls".equalsIgnoreCase(category)) return BALLS;
        if ("shoes".equalsIgnoreCase(category)) return SHOES;
        return RACKET;
    }

    public static String titleFor(String category, @Nullable String brandName) {
        if (brandName != null && !brandName.isEmpty() && "racket".equalsIgnoreCase(category)) {
            return "Vợt Pickleball • " + brandName;
        }
        if (brandName != null && !brandName.isEmpty() && "shoes".equalsIgnoreCase(category)) {
            return "Giày Pickleball • " + brandName;
        }
        if ("racket".equalsIgnoreCase(category)) return "Vợt Pickleball";
        if ("balls".equalsIgnoreCase(category)) return "Bóng Pickleball";
        if ("shoes".equalsIgnoreCase(category)) return "Giày Pickleball";
        return "Sản phẩm";
    }
}


