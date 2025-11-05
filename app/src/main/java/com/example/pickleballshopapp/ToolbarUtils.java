package com.example.pickleballshopapp;

import android.content.Intent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public final class ToolbarUtils {

    private ToolbarUtils() {}

    public static void setupCommonToolbar(AppCompatActivity activity, @Nullable Toolbar toolbar) {
        if (toolbar == null) return;

        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        View logo = toolbar.findViewById(R.id.toolbar_logo);
        View searchAction = toolbar.findViewById(R.id.action_search);
        View cartAction = toolbar.findViewById(R.id.action_cart);
        View accountAction = toolbar.findViewById(R.id.action_account);

        if (logo != null) {
            logo.setOnClickListener(v -> {
                // Dọn sạch back stack fragment (nếu có overlay như Cart/Search)
                activity.getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            });
        }

        if (searchAction != null) {
            searchAction.setOnClickListener(v -> showOverlayFragment(activity, new SearchFragment()));
        }

        if (cartAction != null) {
             cartAction.setOnClickListener(v -> {
                if (activity instanceof CartActivity) {
                    return;
                }
                showOverlayFragment(activity, new CartFragment());
            });
        }

        if (accountAction != null) {
            accountAction.setOnClickListener(v -> {
                SessionManager sm = new SessionManager(activity);
                boolean isLoggedIn = sm.isLoggedIn();

                if (isLoggedIn && activity instanceof ProfileActivity) {
                    return;
                }

                 if (!isLoggedIn && activity instanceof LoginActivity) {
                    return;
                }

                Intent i = new Intent(activity, isLoggedIn ? ProfileActivity.class : LoginActivity.class);
                activity.startActivity(i);
            });
        }
    }

    public static void showOverlayFragment(AppCompatActivity activity, Fragment fragment) {
        View container = activity.findViewById(R.id.overlay_container);
        if (container != null) {
            Fragment existingFragment = activity.getSupportFragmentManager().findFragmentById(R.id.overlay_container);
            if (existingFragment != null) {
                // ĐÃ có overlay → không cho mở thêm fragment khác cho đến khi đóng overlay hiện tại
                return;
            }
            // Hiển thị container trước khi add fragment
            container.setVisibility(View.VISIBLE);
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                    .add(R.id.overlay_container, fragment)
                    .commit();
        } else {
            String tag = (fragment instanceof SearchFragment) ? "SEARCH" : (fragment instanceof CartFragment) ? "CART" : null;
            navigateToMain(activity, tag);
        }
    }

    private static void navigateToMain(AppCompatActivity activity, @Nullable String navigateTo) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (navigateTo != null) {
            intent.putExtra("NAVIGATE_TO", navigateTo);
        }
        activity.startActivity(intent);
    }
}
