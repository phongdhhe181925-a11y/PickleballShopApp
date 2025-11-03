package com.example.pickleballshopapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // Tên file lưu trữ (giống như tên file .txt)
    private static final String PREF_NAME = "PickleballAppPref";

    // Khóa (key) để lưu dữ liệu
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // Chế độ riêng tư: chỉ app này được đọc
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // --- HÀM 1: LƯU PHIÊN ĐĂNG NHẬP ---
    public void createLoginSession(String userId, String fullName, String email, String phone) {
        // Lưu trạng thái đăng nhập = true
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Lưu thông tin user
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email != null ? email : "");
        editor.putString(KEY_PHONE, phone != null ? phone : "");

        // Commit (lưu) thay đổi
        editor.commit();
    }

    // --- HÀM 2: KIỂM TRA ĐÃ ĐĂNG NHẬP CHƯA ---
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN, false); // Mặc định là false (chưa đăng nhập)
    }

    // --- HÀM 3: LẤY TÊN USER ---
    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, "Khách"); // Mặc định là "Khách"
    }

    // --- HÀM 4: LẤY ID USER ---
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    // --- HÀM LẤY EMAIL ---
    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    // --- HÀM LẤY PHONE ---
    public String getPhone() {
        return pref.getString(KEY_PHONE, "");
    }

    // --- HÀM UPDATE THÔNG TIN NGƯỜI DÙNG ---
    public void updateFullName(String fullName) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.commit();
    }

    public void updateUserInfo(String fullName, String email, String phone) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email != null ? email : "");
        editor.putString(KEY_PHONE, phone != null ? phone : "");
        editor.commit();
    }

    // --- HÀM 5: ĐĂNG XUẤT ---
    public void logoutUser(){
        // Xóa tất cả dữ liệu
        editor.clear();
        editor.commit();

        // (Sau này bạn có thể thêm code để chuyển về màn hình Login ở đây)
    }
}