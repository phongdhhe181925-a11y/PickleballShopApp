package com.example.pickleballshopapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Adapter này kết nối ViewPager2 với các Fragment
public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về Fragment tương ứng với vị trí tab
        switch (position) {
            case 0:
                return new HomeFragment(); // Tab đầu tiên là Home
            case 1:
                // return new MenuFragment(); // Tab thứ hai (nếu bạn tạo MenuFragment)
                return new HomeFragment(); // Tạm thời dùng lại Home
            case 2:
                // return new SearchFragment(); // Tab thứ ba (nếu bạn tạo SearchFragment)
                return new HomeFragment(); // Tạm thời dùng lại Home
            case 3:
                return new CartFragment(); // Tab thứ tư là Cart
            case 4:
                // return new AccountFragment(); // Tab thứ năm (nếu bạn tạo AccountFragment)
                return new HomeFragment(); // Tạm thời dùng lại Home
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        // Số lượng tab bạn muốn hiển thị
        return 5; // Chúng ta có 5 tab (Home, Menu, Search, Cart, Account)
    }
}