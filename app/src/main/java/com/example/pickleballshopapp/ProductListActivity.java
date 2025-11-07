package com.example.pickleballshopapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class ProductListActivity extends AppCompatActivity {

    private ImageView headerImage;
    private TextView headerTitle;
    private RecyclerView productRecyclerView;
    private ProductListAdapter productAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        headerImage = findViewById(R.id.headerImage);
        headerTitle = findViewById(R.id.headerTitle);
        productRecyclerView = findViewById(R.id.productRecyclerView);

        String category = getIntent().getStringExtra("category"); // racket | shoes | balls
        String brandName = getIntent().getStringExtra("brandName"); // nullable
        String filter = getIntent().getStringExtra("filter"); // new_arrival | best_seller

        String headerUrl = HeaderAssets.urlForCategory(category);
        String title = HeaderAssets.titleFor(category, brandName);
        
        // Cập nhật title nếu có filter
        if ("new_arrival".equals(filter)) {
            title = "Mới về";
        } else if ("best_seller".equals(filter)) {
            title = "Bán chạy nhất";
        }

        Glide.with(this).load(headerUrl).into(headerImage);
        headerTitle.setText(title);

        // Setup footer
        View footerView = findViewById(R.id.footer);
        if (footerView != null) {
            new FooterHelper(this, footerView);
        }

        productRecyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        productAdapter = new ProductListAdapter();
        productRecyclerView.setAdapter(productAdapter);

        // Xử lý filter new_arrival hoặc best_seller
        if ("new_arrival".equals(filter)) {
            ApiClient.getApiService().getAllNewArrival()
                    .enqueue(new retrofit2.Callback<ProductResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<ProductResponse> call, retrofit2.Response<ProductResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                productAdapter.replace(response.body().getData());
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ProductResponse> call, Throwable t) {
                            // Optionally show a toast/log
                        }
                    });
        } else if ("best_seller".equals(filter)) {
            ApiClient.getApiService().getAllBestSeller()
                    .enqueue(new retrofit2.Callback<ProductResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<ProductResponse> call, retrofit2.Response<ProductResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                productAdapter.replace(response.body().getData());
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ProductResponse> call, Throwable t) {
                            // Optionally show a toast/log
                        }
                    });
        } else {
            // Xử lý bình thường
            Integer categoryId = null;
            if ("racket".equalsIgnoreCase(category)) categoryId = 1; // categories: 1=vợt, 2=bóng, 3=giày
            else if ("balls".equalsIgnoreCase(category)) categoryId = 2;
            else if ("shoes".equalsIgnoreCase(category)) categoryId = 3;

            Integer brandId = null;
            if (getIntent().hasExtra("brandId")) {
                int bid = getIntent().getIntExtra("brandId", 0);
                brandId = bid > 0 ? bid : null;
            }

            ApiClient.getApiService().getProductsFiltered(categoryId, brandId, 1, 50)
                    .enqueue(new retrofit2.Callback<ProductResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<ProductResponse> call, retrofit2.Response<ProductResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                productAdapter.replace(response.body().getData());
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ProductResponse> call, Throwable t) {
                            // Optionally show a toast/log
                        }
                    });
        }
    }
}


