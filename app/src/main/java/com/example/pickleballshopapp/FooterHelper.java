package com.example.pickleballshopapp;

import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FooterHelper {

    private AppCompatActivity activity;
    private View footerView;

    // Category dropdown
    private LinearLayout footerCategoryHeader;
    private LinearLayout footerCategoryContent;
    private ImageView footerCategoryArrow;
    private boolean isCategoryExpanded = false;

    // Address dropdown
    private LinearLayout footerAddressHeader;
    private LinearLayout footerAddressContent;
    private ImageView footerAddressArrow;
    private boolean isAddressExpanded = false;

    // Category items
    private TextView footerCategoryRacket;
    private TextView footerCategoryShoes;
    private TextView footerCategoryBalls;

    // Logo
    private ImageView footerLogo;

    // Link text views
    private TextView footerHotline;
    private TextView footerZalo;
    private TextView footerEmail;

    public FooterHelper(AppCompatActivity activity, View footerView) {
        this.activity = activity;
        this.footerView = footerView;
        initViews();
        setupClickListeners();
        setupUnderlineTexts();
    }

    private void initViews() {
        // Category dropdown
        footerCategoryHeader = footerView.findViewById(R.id.footerCategoryHeader);
        footerCategoryContent = footerView.findViewById(R.id.footerCategoryContent);
        footerCategoryArrow = footerView.findViewById(R.id.footerCategoryArrow);
        footerCategoryRacket = footerView.findViewById(R.id.footerCategoryRacket);
        footerCategoryShoes = footerView.findViewById(R.id.footerCategoryShoes);
        footerCategoryBalls = footerView.findViewById(R.id.footerCategoryBalls);

        // Address dropdown
        footerAddressHeader = footerView.findViewById(R.id.footerAddressHeader);
        footerAddressContent = footerView.findViewById(R.id.footerAddressContent);
        footerAddressArrow = footerView.findViewById(R.id.footerAddressArrow);

        // Logo
        footerLogo = footerView.findViewById(R.id.footerLogo);

        // Link text views
        footerHotline = footerView.findViewById(R.id.footerHotline);
        footerZalo = footerView.findViewById(R.id.footerZalo);
        footerEmail = footerView.findViewById(R.id.footerEmail);
    }

    private void setupUnderlineTexts() {
        // Hotline
        String hotlineText = footerHotline.getText().toString();
        SpannableString hotlineSpannable = new SpannableString(hotlineText);
        hotlineSpannable.setSpan(new UnderlineSpan(), 0, hotlineText.length(), 0);
        footerHotline.setText(hotlineSpannable);

        // Zalo
        String zaloText = footerZalo.getText().toString();
        SpannableString zaloSpannable = new SpannableString(zaloText);
        zaloSpannable.setSpan(new UnderlineSpan(), 0, zaloText.length(), 0);
        footerZalo.setText(zaloSpannable);

        // Email
        String emailText = footerEmail.getText().toString();
        SpannableString emailSpannable = new SpannableString(emailText);
        emailSpannable.setSpan(new UnderlineSpan(), 0, emailText.length(), 0);
        footerEmail.setText(emailSpannable);
    }

    private void setupClickListeners() {
        // Category dropdown toggle
        footerCategoryHeader.setOnClickListener(v -> toggleCategoryDropdown());

        // Category items click
        footerCategoryRacket.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ProductListActivity.class);
            intent.putExtra("category", "racket");
            activity.startActivity(intent);
        });

        footerCategoryShoes.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ProductListActivity.class);
            intent.putExtra("category", "shoes");
            activity.startActivity(intent);
        });

        footerCategoryBalls.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ProductListActivity.class);
            intent.putExtra("category", "balls");
            activity.startActivity(intent);
        });

        // Address dropdown toggle
        footerAddressHeader.setOnClickListener(v -> toggleAddressDropdown());

        // Logo click - navigate to home
        footerLogo.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        });

        // Social icons - static links (no action needed as per requirements)
        // Hotline, Zalo, Email are also static (no action)
    }

    private void toggleCategoryDropdown() {
        if (isCategoryExpanded) {
            collapseView(footerCategoryContent);
            footerCategoryArrow.setRotation(0f);
        } else {
            expandView(footerCategoryContent);
            footerCategoryArrow.setRotation(180f);
        }
        isCategoryExpanded = !isCategoryExpanded;
    }

    private void toggleAddressDropdown() {
        if (isAddressExpanded) {
            collapseView(footerAddressContent);
            footerAddressArrow.setRotation(0f);
        } else {
            expandView(footerAddressContent);
            footerAddressArrow.setRotation(180f);
        }
        isAddressExpanded = !isAddressExpanded;
    }

    private void expandView(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(300);
        v.startAnimation(a);
    }

    private void collapseView(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(300);
        v.startAnimation(a);
    }
}

