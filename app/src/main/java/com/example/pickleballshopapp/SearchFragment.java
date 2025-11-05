package com.example.pickleballshopapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private ImageButton closeButton;
    private TextView clearSearchButton;
    private RecyclerView searchResultsRecyclerView;
    private LinearLayout searchResultsLabelContainer;
    private TextView searchResultsLabel;
    private LinearLayout searchEmptyContainer;
    private TextView searchEmptyTitle1;
    private TextView searchEmptyTitle2;
    private TextView searchEmptyHint1;
    private TextView searchEmptyHint2;
    private ProgressBar searchProgressBar;
    private TextView quickCatRackets;
    private TextView quickCatShoes;
    private TextView quickCatBalls;
    private LinearLayout quickCategoryRow;
    
    private SearchProductAdapter searchAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find Views
        searchView = view.findViewById(R.id.search_view);
        closeButton = view.findViewById(R.id.fragment_close_search_button);
        clearSearchButton = view.findViewById(R.id.clearSearchButton);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        searchResultsLabelContainer = view.findViewById(R.id.searchResultsLabelContainer);
        searchResultsLabel = view.findViewById(R.id.searchResultsLabel);
        searchEmptyContainer = view.findViewById(R.id.searchEmptyContainer);
        searchEmptyTitle1 = view.findViewById(R.id.searchEmptyTitle1);
        searchEmptyTitle2 = view.findViewById(R.id.searchEmptyTitle2);
        searchEmptyHint1 = view.findViewById(R.id.searchEmptyHint1);
        searchEmptyHint2 = view.findViewById(R.id.searchEmptyHint2);
        searchProgressBar = view.findViewById(R.id.searchProgressBar);
        quickCatRackets = view.findViewById(R.id.quickCatRackets);
        quickCatShoes = view.findViewById(R.id.quickCatShoes);
        quickCatBalls = view.findViewById(R.id.quickCatBalls);
        quickCategoryRow = view.findViewById(R.id.quickCategoryRow);

        // Setup RecyclerView với LinearLayoutManager (vertical) - mỗi sản phẩm 1 dòng
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchResultsRecyclerView.setHasFixedSize(true);

        // Handle Close button click – remove trực tiếp với hiệu ứng trượt ra
        closeButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(0, R.anim.slide_out_right, 0, 0)
                        .remove(this)
                        .commitAllowingStateLoss();
                getParentFragmentManager().executePendingTransactions();
                View oc = getActivity().findViewById(R.id.overlay_container);
                if (oc != null) oc.postDelayed(() -> oc.setVisibility(View.GONE), 250);
            }
        });

        // Handle Search Logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Hiển thị/ẩn nút "xóa" dựa trên text có hay không
                if (clearSearchButton != null) {
                    clearSearchButton.setVisibility(newText != null && !newText.isEmpty() ? View.VISIBLE : View.GONE);
                }
                
                // Hủy search cũ nếu có
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                if (newText.length() >= 2) {
                    // Khi bắt đầu tìm, ẩn gợi ý nhanh
                    if (quickCategoryRow != null) quickCategoryRow.setVisibility(View.GONE);
                    // Delay 500ms để tránh search quá nhiều khi user đang gõ
                    searchRunnable = () -> performSearch(newText);
                    searchHandler.postDelayed(searchRunnable, 500);
                } else if (newText.isEmpty()) {
                    // Xóa kết quả khi user xóa text
                    clearSearchResults();
                    // Hiển thị lại gợi ý nhanh khi không có từ khóa
                    if (quickCategoryRow != null) quickCategoryRow.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        
        // Setup nút "xóa" bên cạnh SearchView
        if (clearSearchButton != null) {
            clearSearchButton.setOnClickListener(v -> {
                searchView.setQuery("", false);
                clearSearchResults();
                clearSearchButton.setVisibility(View.GONE);
            });
        }
        
        // Focus vào search view khi mở
        searchView.setFocusable(true);
        searchView.requestFocus();

        // Quick category open handlers
        View.OnClickListener openCategory = v -> {
            String category = null;
            int id = v.getId();
            if (id == R.id.quickCatRackets) category = "racket";
            else if (id == R.id.quickCatShoes) category = "shoes";
            else if (id == R.id.quickCatBalls) category = "balls";
            if (category != null) {
                Intent i = new Intent(requireContext(), ProductListActivity.class);
                i.putExtra("category", category);
                startActivity(i);
            }
        };
        if (quickCatRackets != null) quickCatRackets.setOnClickListener(openCategory);
        if (quickCatShoes != null) quickCatShoes.setOnClickListener(openCategory);
        if (quickCatBalls != null) quickCatBalls.setOnClickListener(openCategory);
    }
    
    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            clearSearchResults();
            return;
        }
        
        String searchKeyword = keyword.trim();
        
        // Hiển thị progress bar
        searchProgressBar.setVisibility(View.VISIBLE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        searchResultsLabelContainer.setVisibility(View.GONE);
        searchEmptyContainer.setVisibility(View.GONE);
        
        // Gọi API search
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProductResponse> call = apiService.searchProducts(searchKeyword);
        
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!isAdded()) {
                    return;
                }
                
                searchProgressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> productList = response.body().getData();
                    
                    if (productList != null && !productList.isEmpty()) {
                        // Hiển thị kết quả với adapter riêng cho search (layout horizontal)
                        searchAdapter = new SearchProductAdapter(requireContext(), productList);
                        searchResultsRecyclerView.setAdapter(searchAdapter);
                        searchResultsRecyclerView.setVisibility(View.VISIBLE);
                        
                        // Hiển thị label "Sản phẩm" (màu xám nhạt)
                        searchResultsLabel.setText("Sản phẩm");
                        searchResultsLabelContainer.setVisibility(View.VISIBLE);
                        
                        searchEmptyContainer.setVisibility(View.GONE);
                        if (quickCategoryRow != null) quickCategoryRow.setVisibility(View.GONE);
                    } else {
                        // Không có kết quả
                        searchResultsRecyclerView.setVisibility(View.GONE);
                        searchResultsLabelContainer.setVisibility(View.GONE);
                        searchEmptyTitle2.setText("nào cho \"" + searchKeyword + "\".");
                        searchEmptyContainer.setVisibility(View.VISIBLE);
                        if (quickCategoryRow != null) quickCategoryRow.setVisibility(View.GONE);
                    }
                } else {
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    searchResultsLabelContainer.setVisibility(View.GONE);
                    searchEmptyTitle2.setText("nào cho \"\".");
                    searchEmptyContainer.setVisibility(View.VISIBLE);
                    if (quickCategoryRow != null) quickCategoryRow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                
                searchProgressBar.setVisibility(View.GONE);
                searchResultsRecyclerView.setVisibility(View.GONE);
                searchResultsLabelContainer.setVisibility(View.GONE);
                searchEmptyTitle2.setText("nào cho \"\".");
                searchEmptyContainer.setVisibility(View.VISIBLE);
                if (quickCategoryRow != null) quickCategoryRow.setVisibility(View.GONE);
                
                Log.e("SearchFragment", "Error searching products: " + t.getMessage());
            }
        });
    }
    
    private void clearSearchResults() {
        searchResultsRecyclerView.setVisibility(View.GONE);
        searchResultsLabelContainer.setVisibility(View.GONE);
        searchEmptyContainer.setVisibility(View.GONE);
        if (searchAdapter != null) {
            searchAdapter = null;
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler khi fragment bị destroy
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}