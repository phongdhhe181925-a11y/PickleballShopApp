package com.example.pickleballshopapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView newArrivalRecyclerView;
    private RecyclerView bestSellerRecyclerView;
    private RecyclerView shoesRecyclerView;
    private RecyclerView ballsRecyclerView;
    
    private ProductAdapter newArrivalAdapter;
    private ProductAdapter bestSellerAdapter;
    private HorizontalProductAdapter shoesAdapter;
    private HorizontalProductAdapter ballsAdapter;
    
    private ProgressBar progressBar;
    private TextView newArrivalLabel;
    private TextView bestSellerLabel;
    private TextView shoesLabel;
    private TextView ballsLabel;
    
    private ImageButton btnShoesPrev;
    private ImageButton btnShoesNext;
    private ImageButton btnBallsPrev;
    private ImageButton btnBallsNext;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ views
        newArrivalRecyclerView = view.findViewById(R.id.newArrivalRecyclerView);
        bestSellerRecyclerView = view.findViewById(R.id.bestSellerRecyclerView);
        shoesRecyclerView = view.findViewById(R.id.shoesRecyclerView);
        ballsRecyclerView = view.findViewById(R.id.ballsRecyclerView);
        progressBar = view.findViewById(R.id.homeProgressBar);
        newArrivalLabel = view.findViewById(R.id.newArrivalLabel);
        bestSellerLabel = view.findViewById(R.id.bestSellerLabel);
        shoesLabel = view.findViewById(R.id.shoesLabel);
        ballsLabel = view.findViewById(R.id.ballsLabel);
        btnShoesPrev = view.findViewById(R.id.btnShoesPrev);
        btnShoesNext = view.findViewById(R.id.btnShoesNext);
        btnBallsPrev = view.findViewById(R.id.btnBallsPrev);
        btnBallsNext = view.findViewById(R.id.btnBallsNext);
        
        // Reset counter
        apiCompletedCount = 0;
        
        // Hiển thị progress bar khi bắt đầu
        progressBar.setVisibility(View.VISIBLE);

        // Cài đặt RecyclerView cho New Arrival (Grid 2 cột - 2 sản phẩm trên 1 hàng)
        newArrivalRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Cài đặt RecyclerView cho Best Seller (Grid 2 cột - 2 sản phẩm trên 1 hàng)
        bestSellerRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Cài đặt RecyclerView cho Shoes (Horizontal)
        LinearLayoutManager shoesLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        shoesRecyclerView.setLayoutManager(shoesLayoutManager);
        
        // Cài đặt RecyclerView cho Balls (Horizontal)
        LinearLayoutManager ballsLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        ballsRecyclerView.setLayoutManager(ballsLayoutManager);
        
        // Thêm scroll listener để cập nhật navigation buttons khi scroll bằng tay
        shoesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateNavigationButtons(shoesRecyclerView, btnShoesPrev, btnShoesNext);
            }
        });
        
        ballsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateNavigationButtons(ballsRecyclerView, btnBallsPrev, btnBallsNext);
            }
        });

        // Setup navigation buttons cho Shoes
        btnShoesPrev.setOnClickListener(v -> scrollRecyclerView(shoesRecyclerView, -1));
        btnShoesNext.setOnClickListener(v -> scrollRecyclerView(shoesRecyclerView, 1));
        
        // Setup navigation buttons cho Balls
        btnBallsPrev.setOnClickListener(v -> scrollRecyclerView(ballsRecyclerView, -1));
        btnBallsNext.setOnClickListener(v -> scrollRecyclerView(ballsRecyclerView, 1));

        // Gọi các hàm để lấy dữ liệu
        fetchNewArrival();
        fetchBestSeller();
        fetchShoes();
        fetchBalls();
    }

    // Lấy danh sách New Arrival
    private void fetchNewArrival() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProductResponse> call = apiService.getNewArrival();

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> productList = response.body().getData();
                    
                    if (productList != null && !productList.isEmpty()) {
                        newArrivalAdapter = new ProductAdapter(requireContext(), productList);
                        newArrivalRecyclerView.setAdapter(newArrivalAdapter);
                        newArrivalRecyclerView.setVisibility(View.VISIBLE);
                        newArrivalLabel.setVisibility(View.VISIBLE);
                    } else {
                        newArrivalRecyclerView.setVisibility(View.GONE);
                        newArrivalLabel.setVisibility(View.GONE);
                    }
                } else {
                    newArrivalRecyclerView.setVisibility(View.GONE);
                    newArrivalLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();
                Log.e("API_FAILURE", "Error fetching New Arrival: " + t.getMessage());
                newArrivalRecyclerView.setVisibility(View.GONE);
                newArrivalLabel.setVisibility(View.GONE);
            }
        });
    }

    // Lấy danh sách Best Seller
    private void fetchBestSeller() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProductResponse> call = apiService.getBestSeller();

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> productList = response.body().getData();
                    
                    if (productList != null && !productList.isEmpty()) {
                        bestSellerAdapter = new ProductAdapter(requireContext(), productList);
                        bestSellerRecyclerView.setAdapter(bestSellerAdapter);
                        bestSellerRecyclerView.setVisibility(View.VISIBLE);
                        bestSellerLabel.setVisibility(View.VISIBLE);
                    } else {
                        bestSellerRecyclerView.setVisibility(View.GONE);
                        bestSellerLabel.setVisibility(View.GONE);
                    }
                } else {
                    bestSellerRecyclerView.setVisibility(View.GONE);
                    bestSellerLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();
                Log.e("API_FAILURE", "Error fetching Best Seller: " + t.getMessage());
                bestSellerRecyclerView.setVisibility(View.GONE);
                bestSellerLabel.setVisibility(View.GONE);
            }
        });
    }
    
    // Biến đếm số API đã hoàn thành
    private int apiCompletedCount = 0;
    private static final int TOTAL_APIS = 4; // New Arrival, Best Seller, Shoes, Balls
    
    // Ẩn progress bar khi cả 4 API đã load xong
    private void hideProgressBarIfAllLoaded() {
        apiCompletedCount++;
        // Nếu cả 4 API đã hoàn thành thì ẩn progress bar
        if (apiCompletedCount >= TOTAL_APIS) {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
    
    // Lấy danh sách Giày
    private void fetchShoes() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProductResponse> call = apiService.getShoes();

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> productList = response.body().getData();
                    
                    if (productList != null && !productList.isEmpty()) {
                        shoesAdapter = new HorizontalProductAdapter(requireContext(), productList);
                        shoesRecyclerView.setAdapter(shoesAdapter);
                        shoesRecyclerView.setVisibility(View.VISIBLE);
                        shoesLabel.setVisibility(View.VISIBLE);
                        updateNavigationButtons(shoesRecyclerView, btnShoesPrev, btnShoesNext);
                    } else {
                        shoesRecyclerView.setVisibility(View.GONE);
                        shoesLabel.setVisibility(View.GONE);
                        btnShoesPrev.setVisibility(View.GONE);
                        btnShoesNext.setVisibility(View.GONE);
                    }
                } else {
                    shoesRecyclerView.setVisibility(View.GONE);
                    shoesLabel.setVisibility(View.GONE);
                    btnShoesPrev.setVisibility(View.GONE);
                    btnShoesNext.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();
                Log.e("API_FAILURE", "Error fetching Shoes: " + t.getMessage());
                shoesRecyclerView.setVisibility(View.GONE);
                shoesLabel.setVisibility(View.GONE);
                btnShoesPrev.setVisibility(View.GONE);
                btnShoesNext.setVisibility(View.GONE);
            }
        });
    }
    
    // Lấy danh sách Bóng thi đấu
    private void fetchBalls() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProductResponse> call = apiService.getBalls();

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> productList = response.body().getData();
                    
                    if (productList != null && !productList.isEmpty()) {
                        ballsAdapter = new HorizontalProductAdapter(requireContext(), productList);
                        ballsRecyclerView.setAdapter(ballsAdapter);
                        ballsRecyclerView.setVisibility(View.VISIBLE);
                        ballsLabel.setVisibility(View.VISIBLE);
                        updateNavigationButtons(ballsRecyclerView, btnBallsPrev, btnBallsNext);
                    } else {
                        ballsRecyclerView.setVisibility(View.GONE);
                        ballsLabel.setVisibility(View.GONE);
                        btnBallsPrev.setVisibility(View.GONE);
                        btnBallsNext.setVisibility(View.GONE);
                    }
                } else {
                    ballsRecyclerView.setVisibility(View.GONE);
                    ballsLabel.setVisibility(View.GONE);
                    btnBallsPrev.setVisibility(View.GONE);
                    btnBallsNext.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                hideProgressBarIfAllLoaded();
                Log.e("API_FAILURE", "Error fetching Balls: " + t.getMessage());
                ballsRecyclerView.setVisibility(View.GONE);
                ballsLabel.setVisibility(View.GONE);
                btnBallsPrev.setVisibility(View.GONE);
                btnBallsNext.setVisibility(View.GONE);
            }
        });
    }
    
    // Scroll RecyclerView theo hướng (direction: -1 = left, 1 = right)
    private void scrollRecyclerView(RecyclerView recyclerView, int direction) {
        if (recyclerView == null) return;
        
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;
        
        int currentPosition = layoutManager.findFirstVisibleItemPosition();
        int itemWidth = 300 + 12; // width + margin của item
        
        if (direction < 0) {
            // Scroll left (previous)
            recyclerView.smoothScrollBy(-itemWidth, 0);
        } else {
            // Scroll right (next)
            recyclerView.smoothScrollBy(itemWidth, 0);
        }
        
        // Update navigation buttons sau khi scroll
        recyclerView.postDelayed(() -> {
            if (recyclerView == shoesRecyclerView) {
                updateNavigationButtons(shoesRecyclerView, btnShoesPrev, btnShoesNext);
            } else if (recyclerView == ballsRecyclerView) {
                updateNavigationButtons(ballsRecyclerView, btnBallsPrev, btnBallsNext);
            }
        }, 300);
    }
    
    // Cập nhật visibility của navigation buttons dựa trên scroll position
    private void updateNavigationButtons(RecyclerView recyclerView, ImageButton btnPrev, ImageButton btnNext) {
        if (recyclerView == null || btnPrev == null || btnNext == null) return;
        
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;
        
        int totalItems = layoutManager.getItemCount();
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        
        // Hiển thị buttons nếu có nhiều hơn 1 item
        if (totalItems > 1) {
            btnPrev.setVisibility(firstVisible > 0 ? View.VISIBLE : View.GONE);
            btnNext.setVisibility(lastVisible < totalItems - 1 ? View.VISIBLE : View.GONE);
        } else {
            btnPrev.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }
    }

}
