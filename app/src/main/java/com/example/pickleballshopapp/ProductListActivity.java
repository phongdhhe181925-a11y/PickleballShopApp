package com.example.pickleballshopapp;

import android.os.Bundle;
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

        String headerUrl = HeaderAssets.urlForCategory(category);
        String title = HeaderAssets.titleFor(category, brandName);

        Glide.with(this).load(headerUrl).into(headerImage);
        headerTitle.setText(title);

        productRecyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        productAdapter = new ProductListAdapter();
        productRecyclerView.setAdapter(productAdapter);

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


