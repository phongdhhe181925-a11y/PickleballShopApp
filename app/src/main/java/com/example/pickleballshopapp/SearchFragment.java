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
    private TextView searchEmptyTextView;
    private ProgressBar searchProgressBar;
    
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
        searchEmptyTextView = view.findViewById(R.id.searchEmptyTextView);
        searchProgressBar = view.findViewById(R.id.searchProgressBar);

        // Setup RecyclerView với LinearLayoutManager (vertical) - mỗi sản phẩm 1 dòng
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchResultsRecyclerView.setHasFixedSize(true);

        // Handle Close button click
        closeButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
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
                    // Delay 500ms để tránh search quá nhiều khi user đang gõ
                    searchRunnable = () -> performSearch(newText);
                    searchHandler.postDelayed(searchRunnable, 500);
                } else if (newText.isEmpty()) {
                    // Xóa kết quả khi user xóa text
                    clearSearchResults();
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
        searchEmptyTextView.setVisibility(View.GONE);
        
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
                        
                        searchEmptyTextView.setVisibility(View.GONE);
                    } else {
                        // Không có kết quả
                        searchResultsRecyclerView.setVisibility(View.GONE);
                        searchResultsLabelContainer.setVisibility(View.GONE);
                        searchEmptyTextView.setText("Không tìm thấy sản phẩm nào cho \"" + searchKeyword + "\"");
                        searchEmptyTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    searchResultsLabelContainer.setVisibility(View.GONE);
                    searchEmptyTextView.setText("Lỗi khi tìm kiếm. Vui lòng thử lại.");
                    searchEmptyTextView.setVisibility(View.VISIBLE);
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
                searchEmptyTextView.setText("Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại.");
                searchEmptyTextView.setVisibility(View.VISIBLE);
                
                Log.e("SearchFragment", "Error searching products: " + t.getMessage());
            }
        });
    }
    
    private void clearSearchResults() {
        searchResultsRecyclerView.setVisibility(View.GONE);
        searchResultsLabelContainer.setVisibility(View.GONE);
        searchEmptyTextView.setText("Nhập từ khóa để tìm kiếm sản phẩm");
        searchEmptyTextView.setVisibility(View.VISIBLE);
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