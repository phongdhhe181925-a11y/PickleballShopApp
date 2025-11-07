package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
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
    
    // Nút "Xem tất cả"
    private android.widget.Button btnViewAllNewArrival;
    private android.widget.Button btnViewAllBestSeller;
    private android.widget.Button btnViewAllShoes;
    private android.widget.Button btnViewAllBalls;
    
    // Carousel variables
    private ViewPager2 carouselViewPager;
    private LinearLayout carouselIndicators;
    private Handler carouselHandler;
    private Runnable carouselRunnable;
    private CarouselAdapter carouselAdapter;
    private ViewPager2.OnPageChangeCallback carouselPageChangeCallback;

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

        // Setup footer
        View footerView = view.findViewById(R.id.footer);
        if (footerView != null && getActivity() != null) {
            new FooterHelper((androidx.appcompat.app.AppCompatActivity) getActivity(), footerView);
        }

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
        
        // Ánh xạ nút "Xem tất cả"
        btnViewAllNewArrival = view.findViewById(R.id.btnViewAllNewArrival);
        btnViewAllBestSeller = view.findViewById(R.id.btnViewAllBestSeller);
        btnViewAllShoes = view.findViewById(R.id.btnViewAllShoes);
        btnViewAllBalls = view.findViewById(R.id.btnViewAllBalls);
        
        // Setup click cho nút "Xem tất cả"
        btnViewAllNewArrival.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ProductListActivity.class);
            i.putExtra("category", "racket");
            i.putExtra("filter", "new_arrival");
            startActivity(i);
        });
        
        btnViewAllBestSeller.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ProductListActivity.class);
            i.putExtra("category", "racket");
            i.putExtra("filter", "best_seller");
            startActivity(i);
        });
        
        btnViewAllShoes.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ProductListActivity.class);
            i.putExtra("category", "shoes");
            startActivity(i);
        });
        
        btnViewAllBalls.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ProductListActivity.class);
            i.putExtra("category", "balls");
            startActivity(i);
        });
        
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

        // Ánh xạ carousel views
        carouselViewPager = view.findViewById(R.id.carouselViewPager);
        carouselIndicators = view.findViewById(R.id.carouselIndicators);
        carouselHandler = new Handler();

        // Gọi các hàm để lấy dữ liệu
        fetchCarousel();
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
                        btnViewAllNewArrival.setVisibility(View.VISIBLE);
                    } else {
                        newArrivalRecyclerView.setVisibility(View.GONE);
                        newArrivalLabel.setVisibility(View.GONE);
                        btnViewAllNewArrival.setVisibility(View.GONE);
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
                        btnViewAllBestSeller.setVisibility(View.VISIBLE);
                    } else {
                        bestSellerRecyclerView.setVisibility(View.GONE);
                        bestSellerLabel.setVisibility(View.GONE);
                        btnViewAllBestSeller.setVisibility(View.GONE);
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
    
    // Fetch Carousel Images
    private void fetchCarousel() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<CarouselResponse> call = apiService.getCarouselImages();

        call.enqueue(new Callback<CarouselResponse>() {
            @Override
            public void onResponse(Call<CarouselResponse> call, Response<CarouselResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<CarouselImage> images = response.body().getData();
                    if (images != null && !images.isEmpty()) {
                        setupCarousel(images);
                        carouselViewPager.setVisibility(View.VISIBLE);
                        carouselIndicators.setVisibility(View.VISIBLE);
                    } else {
                        carouselViewPager.setVisibility(View.GONE);
                        carouselIndicators.setVisibility(View.GONE);
                    }
                } else {
                    carouselViewPager.setVisibility(View.GONE);
                    carouselIndicators.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CarouselResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("API_FAILURE", "Error fetching Carousel: " + t.getMessage());
                carouselViewPager.setVisibility(View.GONE);
                carouselIndicators.setVisibility(View.GONE);
            }
        });
    }

    // Setup carousel với auto-scroll
    private void setupCarousel(List<CarouselImage> images) {
        if (images == null || images.isEmpty() || !isAdded()) return;

        carouselAdapter = new CarouselAdapter(images);
        carouselViewPager.setAdapter(carouselAdapter);

        // Setup indicators
        setupIndicators(images.size());

        // Auto-scroll mỗi 5 giây
        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                if (carouselViewPager != null && carouselAdapter != null && isAdded()) {
                    int currentItem = carouselViewPager.getCurrentItem();
                    int totalItems = carouselAdapter.getItemCount();
                    if (totalItems > 0) {
                        carouselViewPager.setCurrentItem((currentItem + 1) % totalItems, true);
                    }
                    if (carouselHandler != null) {
                        carouselHandler.postDelayed(this, 5000);
                    }
                }
            }
        };
        carouselHandler.postDelayed(carouselRunnable, 5000);

        // Dừng auto-scroll khi user vuốt, sau đó restart
        carouselPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (carouselHandler != null && carouselRunnable != null) {
                    carouselHandler.removeCallbacks(carouselRunnable);
                    carouselHandler.postDelayed(carouselRunnable, 5000);
                    updateIndicators(position);
                }
            }
        };
        carouselViewPager.registerOnPageChangeCallback(carouselPageChangeCallback);
    }

    // Setup indicators (chấm tròn)
    private void setupIndicators(int count) {
        if (carouselIndicators == null || !isAdded()) return;

        carouselIndicators.removeAllViews();
        for (int i = 0; i < count; i++) {
            View indicator = new View(requireContext());
            int size = (int) (8 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins((int) (8 * getResources().getDisplayMetrics().density), 0, 
                            (int) (8 * getResources().getDisplayMetrics().density), 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(R.drawable.indicator_unselected);
            carouselIndicators.addView(indicator);
        }
        if (count > 0) {
            updateIndicators(0);
        }
    }

    private void updateIndicators(int position) {
        if (carouselIndicators == null || !isAdded()) return;

        for (int i = 0; i < carouselIndicators.getChildCount(); i++) {
            View indicator = carouselIndicators.getChildAt(i);
            indicator.setBackgroundResource(i == position ?
                    R.drawable.indicator_selected : R.drawable.indicator_unselected);
        }
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
                        btnViewAllShoes.setVisibility(View.VISIBLE);
                        updateNavigationButtons(shoesRecyclerView, btnShoesPrev, btnShoesNext);
                    } else {
                        shoesRecyclerView.setVisibility(View.GONE);
                        shoesLabel.setVisibility(View.GONE);
                        btnViewAllShoes.setVisibility(View.GONE);
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
                        btnViewAllBalls.setVisibility(View.VISIBLE);
                        updateNavigationButtons(ballsRecyclerView, btnBallsPrev, btnBallsNext);
                    } else {
                        ballsRecyclerView.setVisibility(View.GONE);
                        ballsLabel.setVisibility(View.GONE);
                        btnViewAllBalls.setVisibility(View.GONE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cleanup carousel handler
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
        }
        if (carouselViewPager != null && carouselPageChangeCallback != null) {
            carouselViewPager.unregisterOnPageChangeCallback(carouselPageChangeCallback);
        }
    }

}
