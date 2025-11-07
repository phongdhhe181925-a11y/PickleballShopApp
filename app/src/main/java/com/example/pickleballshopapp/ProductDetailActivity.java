package com.example.pickleballshopapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    
    private Product product;
    private Product fullProduct; // Sản phẩm đầy đủ từ API
    
    private ImageView detailMainImageView;
    private ImageButton btnPrevImage;
    private ImageButton btnNextImage;
    private LinearLayout imageThumbnailsContainer;
    private TextView detailBrandTextView;
    private TextView detailNameTextView;
    private TextView detailPriceTextView;
    private LinearLayout optionsContainer;
    private TextView stockStatusTextView;
    private TextView detailDescTextView;
    private TextView detailSpecsTextView;
    private TextView descriptionTab;
    private TextView specsTab;
    
    private int currentQuantity = 1;
    private int currentSelectedTab = 0; // 0 = mô tả, 1 = thông số
    private int currentImageIndex = 0; // Vị trí ảnh hiện tại
    private List<Product.ImageItem> currentImages = new ArrayList<>(); // Danh sách ảnh hiện tại
    
    // Map để nhóm các options theo option_name và lưu button containers
    private Map<String, List<LinearLayout>> optionGroups = new HashMap<>();
    private Map<String, List<ProductOption>> optionGroupsByOption = new HashMap<>();
    // Map để lưu reference option cho mỗi button
    private Map<Button, ProductOption> buttonToOptionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        // Setup footer
        View footerView = findViewById(R.id.footer);
        if (footerView != null) {
            new FooterHelper(this, footerView);
        }

        // Ánh xạ views
        detailMainImageView = findViewById(R.id.detailMainImageView);
        btnPrevImage = findViewById(R.id.btnPrevImage);
        btnNextImage = findViewById(R.id.btnNextImage);
        imageThumbnailsContainer = findViewById(R.id.imageThumbnailsContainer);
        detailBrandTextView = findViewById(R.id.detailBrandTextView);
        
        // Setup navigation buttons
        btnPrevImage.setOnClickListener(v -> showPreviousImage());
        btnNextImage.setOnClickListener(v -> showNextImage());
        detailNameTextView = findViewById(R.id.detailNameTextView);
        detailPriceTextView = findViewById(R.id.detailPriceTextView);
        optionsContainer = findViewById(R.id.optionsContainer);
        stockStatusTextView = findViewById(R.id.stockStatusTextView);
        detailDescTextView = findViewById(R.id.detailDescTextView);
        detailSpecsTextView = findViewById(R.id.detailSpecsTextView);
        descriptionTab = findViewById(R.id.descriptionTab);
        specsTab = findViewById(R.id.specsTab);
        
        Button addToCartButton = findViewById(R.id.addToCartButton);
        Button buyNowButton = findViewById(R.id.btnBuyNow);
        ImageButton btnDecrease = findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = findViewById(R.id.btnIncrease);
        TextView tvQuantity = findViewById(R.id.tvQuantity);

        // Lấy product từ intent (có thể chỉ có thông tin cơ bản)
        product = (Product) getIntent().getSerializableExtra("PRODUCT_DETAIL");

        if (product != null) {
            // Gọi API để lấy chi tiết đầy đủ
            fetchProductDetail(Integer.parseInt(product.getId()));
            
            // Setup quantity controls
            tvQuantity.setText(String.valueOf(currentQuantity));

            btnIncrease.setOnClickListener(v -> {
                // Chưa chọn đủ option value: không làm gì và không hiện thông báo
                if (!areAllOptionsSelected()) {
                    return;
                }
                int maxQty = getMaxQuantity();
                if (currentQuantity < maxQty) {
                    currentQuantity++;
                    tvQuantity.setText(String.valueOf(currentQuantity));
                } else {
                    Toast.makeText(this, "Không thể mua quá số lượng tồn kho", Toast.LENGTH_SHORT).show();
                }
            });

            btnDecrease.setOnClickListener(v -> {
                // Chưa chọn đủ option value: không làm gì và không hiện thông báo
                if (!areAllOptionsSelected()) {
                    return;
                }
                if (currentQuantity > 1) {
                    currentQuantity--;
                    tvQuantity.setText(String.valueOf(currentQuantity));
                }
            });

            addToCartButton.setOnClickListener(v -> {
                if (!areAllOptionsSelected()) {
                    Toast.makeText(ProductDetailActivity.this, "Vui lòng chọn đầy đủ các tùy chọn sản phẩm trước khi thêm vào giỏ hàng!", Toast.LENGTH_LONG).show();
                    return;
                }
                
                // Kiểm tra stock trước khi thêm vào giỏ hàng
                Product currentProduct = (fullProduct != null) ? fullProduct : product;
                if (currentProduct != null) {
                    int stockQty = getCurrentStockQuantityFromSelectedOptions(currentProduct);
                    if (stockQty <= 0) {
                        Toast.makeText(ProductDetailActivity.this, "Sản phẩm đã hết hàng không thể thêm vào giỏ hàng", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                
                if (fullProduct != null) {
                    CartManager.addToCart(fullProduct, currentQuantity);
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    ToolbarUtils.showOverlayFragment(ProductDetailActivity.this, new CartFragment());
                } else {
                    // Fallback nếu chưa load được full product
                    CartManager.addToCart(product, currentQuantity);
                Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                ToolbarUtils.showOverlayFragment(ProductDetailActivity.this, new CartFragment());
                }
            });

            buyNowButton.setOnClickListener(v -> {
                if (!areAllOptionsSelected()) {
                    Toast.makeText(ProductDetailActivity.this, "Vui lòng chọn đầy đủ các tùy chọn sản phẩm trước khi mua ngay!", Toast.LENGTH_LONG).show();
                    return;
                }
                
                // Kiểm tra stock trước khi mua ngay
                Product currentProduct = (fullProduct != null) ? fullProduct : product;
                if (currentProduct != null) {
                    int stockQty = getCurrentStockQuantityFromSelectedOptions(currentProduct);
                    if (stockQty <= 0) {
                        Toast.makeText(ProductDetailActivity.this, "Sản phẩm đã hết hàng không thể mua ngay", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                
                Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                if (fullProduct != null) {
                    intent.putExtra("BUY_NOW_PRODUCT", fullProduct);
                } else {
                intent.putExtra("BUY_NOW_PRODUCT", product);
                }
                intent.putExtra("BUY_NOW_QTY", currentQuantity);
                startActivity(intent);
            });
            
            // Setup tabs
            setupTabs();
        }
    }
    
    private void fetchProductDetail(int productId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProductDetailResponse> call = apiService.getProductDetail(productId);
        
        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    fullProduct = response.body().getData();
                    displayProductDetails(fullProduct);
                } else {
                    Log.e(TAG, "Failed to get product detail: " + response.code());
                    // Fallback: hiển thị với thông tin cơ bản từ product
                    displayProductDetails(product);
                }
            }
            
            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching product detail", t);
                Toast.makeText(ProductDetailActivity.this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                // Fallback: hiển thị với thông tin cơ bản
                displayProductDetails(product);
            }
        });
    }
    
    private void displayProductDetails(Product p) {
        // Hiển thị thương hiệu
        if (p.getBrand() != null && !p.getBrand().isEmpty()) {
            detailBrandTextView.setText(p.getBrand());
            detailBrandTextView.setVisibility(View.VISIBLE);
        } else {
            detailBrandTextView.setVisibility(View.GONE);
        }
        
        // Hiển thị tên sản phẩm
        detailNameTextView.setText(p.getName());
        
        // Hiển thị giá tiền
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        double finalPrice = p.getPrice();
        
        // Tính giá với price adjustment từ options đã chọn
        if (p.getOptions() != null && !p.getOptions().isEmpty()) {
            for (ProductOption option : p.getOptions()) {
                if (option.getSelectedValue() != null) {
                    finalPrice += option.getSelectedValue().getPriceAdjustment();
                }
            }
        }
        detailPriceTextView.setText(currencyFormatter.format(finalPrice));
        
        // Hiển thị ảnh
        setupImages(p);
        
        // Hiển thị options
        setupOptions(p);
        
        // Hiển thị thông báo tồn kho
        updateStockStatus(p);
        
        // Hiển thị tabs và nội dung
        setupTabsContent(p);
        
        // Cập nhật trạng thái buttons sau khi setup options
        updateButtonsState();
    }
    
    private void setupImages(Product p) {
        List<Product.ImageItem> images = p.getImages();
        currentImages.clear();
        
        if (images != null && !images.isEmpty()) {
            currentImages = new ArrayList<>(images);
            currentImageIndex = 0;
            
            // Hiển thị ảnh đầu tiên
            showImageAtIndex(0);
            
            // Hiển thị các ảnh nhỏ
            imageThumbnailsContainer.removeAllViews();
            for (int i = 0; i < images.size(); i++) {
                final int index = i;
                Product.ImageItem imageItem = images.get(i);
                
                ImageView thumbnail = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                thumbnail.setLayoutParams(params);
                thumbnail.getLayoutParams().width = 80;
                thumbnail.getLayoutParams().height = 80;
                thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                thumbnail.setPadding(4, 4, 4, 4);
                thumbnail.setBackgroundResource(R.drawable.btn_outline_black);
                
                Glide.with(this)
                        .load(imageItem.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(thumbnail);
                
                thumbnail.setOnClickListener(v -> {
                    // Chuyển ảnh chính khi click vào ảnh nhỏ
                    currentImageIndex = index;
                    showImageAtIndex(index);
                });
                
                imageThumbnailsContainer.addView(thumbnail);
            }
            
            // Cập nhật visibility của nút điều hướng
            updateNavigationButtons();
        } else if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            // Fallback: dùng image_url nếu không có images
            Glide.with(this)
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(detailMainImageView);
            
            // Ẩn nút điều hướng nếu chỉ có 1 ảnh
            btnPrevImage.setVisibility(View.GONE);
            btnNextImage.setVisibility(View.GONE);
        }
    }
    
    private void showImageAtIndex(int index) {
        if (currentImages == null || currentImages.isEmpty() || index < 0 || index >= currentImages.size()) {
            return;
        }
        
        currentImageIndex = index;
        Product.ImageItem imageItem = currentImages.get(index);
        
        Glide.with(this)
                .load(imageItem.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(detailMainImageView);
        
        updateNavigationButtons();
    }
    
    private void showPreviousImage() {
        if (currentImages == null || currentImages.isEmpty()) {
            return;
        }
        
        if (currentImageIndex > 0) {
            showImageAtIndex(currentImageIndex - 1);
        } else {
            // Quay về ảnh cuối
            showImageAtIndex(currentImages.size() - 1);
        }
    }
    
    private void showNextImage() {
        if (currentImages == null || currentImages.isEmpty()) {
            return;
        }
        
        if (currentImageIndex < currentImages.size() - 1) {
            showImageAtIndex(currentImageIndex + 1);
        } else {
            // Quay về ảnh đầu
            showImageAtIndex(0);
        }
    }
    
    private void updateNavigationButtons() {
        if (currentImages == null || currentImages.size() <= 1) {
            // Chỉ có 1 ảnh hoặc không có ảnh: ẩn nút
            btnPrevImage.setVisibility(View.GONE);
            btnNextImage.setVisibility(View.GONE);
        } else {
            // Có nhiều ảnh: hiển thị nút
            btnPrevImage.setVisibility(View.VISIBLE);
            btnNextImage.setVisibility(View.VISIBLE);
        }
    }
    
    private void setupOptions(Product p) {
        optionsContainer.removeAllViews();
        optionGroups.clear();
        optionGroupsByOption.clear();
        buttonToOptionMap.clear();
        
        if (p.getOptions() != null && !p.getOptions().isEmpty()) {
            // Nhóm các options theo option_type (loại) thay vì option_name
            // Các options cùng loại (ví dụ: cùng là "size") sẽ được nhóm lại
            for (ProductOption option : p.getOptions()) {
                String optionType = option.getOptionType() != null ? option.getOptionType() : "custom";
                if (!optionGroupsByOption.containsKey(optionType)) {
                    optionGroupsByOption.put(optionType, new ArrayList<>());
                }
                optionGroupsByOption.get(optionType).add(option);
            }
            
            // Tạo UI cho từng option
            for (ProductOption option : p.getOptions()) {
                // Tạo label cho option - hiển thị đúng tên của option
                TextView optionLabel = new TextView(this);
                optionLabel.setText(option.getOptionName() + ":");
                optionLabel.setTextSize(16);
                optionLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                optionLabel.setPadding(0, 16, 0, 8);
                
                optionsContainer.addView(optionLabel);
                
                // Tạo container horizontal cho các buttons - wrap content để tự động xuống dòng
                LinearLayout buttonsContainer = new LinearLayout(this);
                buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
                buttonsContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                buttonsContainer.setPadding(0, 0, 0, 16);
                // Cho phép wrap content - nếu không đủ chỗ sẽ tự động xuống dòng
                buttonsContainer.setBaselineAligned(false);
                
                // Lưu button container vào nhóm theo option_type
                String optionType = option.getOptionType() != null ? option.getOptionType() : "custom";
                if (!optionGroups.containsKey(optionType)) {
                    optionGroups.put(optionType, new ArrayList<>());
                }
                
                List<ProductOptionValue> values = option.getValues();
                
                if (values != null && !values.isEmpty()) {
                    // Chia thành nhiều hàng, mỗi hàng tối đa 4 buttons
                    int maxButtonsPerRow = 4;
                    int totalButtons = values.size();
                    int rowCount = (int) Math.ceil((double) totalButtons / maxButtonsPerRow);
                    
                    // Lưu danh sách tất cả containers cho option này
                    List<LinearLayout> optionContainers = new ArrayList<>();
                    
                    // Add buttonsContainer (hàng đầu tiên) vào optionsContainer trước
                    optionsContainer.addView(buttonsContainer);
                    
                    for (int row = 0; row < rowCount; row++) {
                        // Tạo container cho mỗi hàng
                        LinearLayout rowContainer;
                        if (row == 0) {
                            // Hàng đầu tiên dùng buttonsContainer đã tạo
                            rowContainer = buttonsContainer;
                        } else {
                            // Tạo container mới cho hàng tiếp theo
                            rowContainer = new LinearLayout(this);
                            rowContainer.setOrientation(LinearLayout.HORIZONTAL);
                            rowContainer.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            rowContainer.setPadding(0, 0, 0, 8);
                            rowContainer.setBaselineAligned(false);
                            // Add vào optionsContainer sau hàng trước đó
                            optionsContainer.addView(rowContainer);
                        }
                        optionContainers.add(rowContainer);
                        // Lưu container vào nhóm
                        optionGroups.get(optionType).add(rowContainer);
                        
                        // Tính toán range của buttons cho hàng này
                        int startIndex = row * maxButtonsPerRow;
                        int endIndex = Math.min(startIndex + maxButtonsPerRow, totalButtons);
                        
                        // Tạo button cho mỗi value trong hàng này
                        for (int i = startIndex; i < endIndex; i++) {
                            ProductOptionValue value = values.get(i);
                            Button valueButton = new Button(this);
                        
                            // Text cho button - chỉ hiển thị giá trị, không hiển thị giá tăng
                            valueButton.setText(value.getValue());
                            
                            // Style: bo góc, màu trắng, viền đen
                            valueButton.setBackgroundResource(R.drawable.btn_outline_black);
                            valueButton.setTextColor(getResources().getColor(android.R.color.black));
                            valueButton.setTextSize(12); // Text size về lại như ban đầu
                            
                            // Layout params
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 8, 0); // Chỉ margin right
                            valueButton.setLayoutParams(params);
                            
                            // Padding cho button
                            int padding = (int) (8 * getResources().getDisplayMetrics().density);
                            int paddingVertical = (int) (6 * getResources().getDisplayMetrics().density);
                            valueButton.setPadding(padding, paddingVertical, padding, paddingVertical);
                            
                            // Tag để lưu value và option
                            valueButton.setTag(value);
                            buttonToOptionMap.put(valueButton, option); // Lưu reference option
                            
                            // Set trạng thái ban đầu (chưa chọn)
                            updateButtonStyle(valueButton, false);
                        
                            // Click listener
                            valueButton.setOnClickListener(v -> {
                            ProductOption currentOption = buttonToOptionMap.get(valueButton);
                            ProductOptionValue selectedValue = (ProductOptionValue) valueButton.getTag();
                            
                            // Kiểm tra xem button này đã được chọn chưa
                            boolean isAlreadySelected = currentOption.getSelectedValue() != null && 
                                                       currentOption.getSelectedValue().getValueId() == selectedValue.getValueId();
                            
                            // Lấy optionType từ currentOption để nhóm các options cùng loại
                            String currentOptionType = currentOption.getOptionType() != null ? currentOption.getOptionType() : "custom";
                            
                            // Tìm tất cả containers cho option này
                            List<LinearLayout> currentOptionContainers = new ArrayList<>();
                            List<LinearLayout> allContainers = optionGroups.get(currentOptionType);
                            if (allContainers != null) {
                                for (LinearLayout container : allContainers) {
                                    // Kiểm tra xem container này có chứa button của option này không
                                    for (int j = 0; j < container.getChildCount(); j++) {
                                        View child = container.getChildAt(j);
                                        if (child instanceof Button) {
                                            ProductOption btnOption = buttonToOptionMap.get(child);
                                            if (btnOption != null && btnOption.getOptionId() == currentOption.getOptionId()) {
                                                currentOptionContainers.add(container);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            
                            if (isAlreadySelected) {
                                // Đã chọn rồi: bỏ chọn
                                updateButtonStyle(valueButton, false);
                                currentOption.setSelectedValue(null);
                                
                                // Nếu có options cùng loại, enable lại các options khác trong nhóm
                                List<ProductOption> sameTypeOptions = optionGroupsByOption.get(currentOptionType);
                                if (sameTypeOptions != null && sameTypeOptions.size() > 1) {
                                    List<LinearLayout> containers = optionGroups.get(currentOptionType);
                                    if (containers != null) {
                                        for (LinearLayout container : containers) {
                                            for (int j = 0; j < container.getChildCount(); j++) {
                                                View child = container.getChildAt(j);
                                                if (child instanceof Button) {
                                                    child.setEnabled(true);
                                                    child.setAlpha(1.0f);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Chưa chọn: chọn button này
                                // Bỏ chọn tất cả buttons trong cùng option (tất cả các hàng)
                                for (LinearLayout container : currentOptionContainers) {
                                    for (int j = 0; j < container.getChildCount(); j++) {
                                        View child = container.getChildAt(j);
                                        if (child instanceof Button) {
                                            ProductOption btnOption = buttonToOptionMap.get(child);
                                            if (btnOption != null && btnOption.getOptionId() == currentOption.getOptionId()) {
                                                updateButtonStyle((Button) child, false);
                                            }
                                        }
                                    }
                                }
                                
                                // Chọn button này
                                updateButtonStyle(valueButton, true);
                                currentOption.setSelectedValue(selectedValue);
                                
                                // Nếu có options cùng loại, disable các options khác trong nhóm
                                List<ProductOption> sameTypeOptions = optionGroupsByOption.get(currentOptionType);
                                if (sameTypeOptions != null && sameTypeOptions.size() > 1) {
                                    for (ProductOption otherOption : sameTypeOptions) {
                                        if (otherOption.getOptionId() != currentOption.getOptionId()) {
                                            // Disable option khác
                                            otherOption.setSelectedValue(null);
                                            // Tìm và disable buttons của option khác
                                            List<LinearLayout> containers = optionGroups.get(currentOptionType);
                                            if (containers != null) {
                                                for (LinearLayout container : containers) {
                                                    boolean isCurrentOptionContainer = currentOptionContainers.contains(container);
                                                    if (!isCurrentOptionContainer) {
                                                        for (int k = 0; k < container.getChildCount(); k++) {
                                                            View child = container.getChildAt(k);
                                                            if (child instanceof Button) {
                                                                ProductOption btnOption = buttonToOptionMap.get(child);
                                                                if (btnOption != null && btnOption.getOptionId() == otherOption.getOptionId()) {
                                                                    child.setEnabled(false);
                                                                    child.setAlpha(0.5f);
                                                                    updateButtonStyle((Button) child, false);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Cập nhật lại trạng thái enable/disable của các buttons trong nhóm options cùng loại
                            updateOptionGroupsState(currentOptionType);
                            
                            // Cập nhật lại giá và stock status
                            updatePriceAndStock();
                            // Cập nhật trạng thái buttons
                            updateButtonsState();
                        });
                            
                            // Add button vào rowContainer
                            rowContainer.addView(valueButton);
                        }
                    }
                    
                    // Mặc định chưa chọn gì
                    option.setSelectedValue(null);
                }
            }
        }
    }
    
    /**
     * Cập nhật style của button khi được chọn/chưa chọn
     */
    private void updateButtonStyle(Button button, boolean isSelected) {
        if (isSelected) {
            // Khi được chọn: background đen (bo góc), text trắng
            button.setBackgroundResource(R.drawable.btn_solid_black);
            button.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            // Khi chưa chọn: background trắng, viền đen (bo góc)
            button.setBackgroundResource(R.drawable.btn_outline_black);
            button.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
    
    /**
     * Chỉ cập nhật giá và stock status khi user chọn option (không tạo lại UI)
     */
    private void updatePriceAndStock() {
        Product p = (fullProduct != null) ? fullProduct : product;
        if (p == null) return;
        
        // Cập nhật giá
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        double finalPrice = p.getPrice();
        
        // Tính giá với price adjustment từ options đã chọn
        if (p.getOptions() != null && !p.getOptions().isEmpty()) {
            for (ProductOption option : p.getOptions()) {
                if (option.getSelectedValue() != null) {
                    finalPrice += option.getSelectedValue().getPriceAdjustment();
                }
            }
        }
        detailPriceTextView.setText(currencyFormatter.format(finalPrice));
        
        // Cập nhật stock status
        updateStockStatus(p);
        updateButtonsState(); // Cập nhật trạng thái buttons sau khi kiểm tra stock
    }
    
    private void updateStockStatus(Product p) {
        // Kiểm tra xem sản phẩm có options không và đã chọn đủ options chưa
        if (p.getOptions() != null && !p.getOptions().isEmpty()) {
            // Có options: kiểm tra đã chọn đủ chưa (nếu có options cùng loại, chỉ cần chọn 1 trong mỗi nhóm)
            // Nhóm các options theo option_type (loại)
            Map<String, List<ProductOption>> groups = new HashMap<>();
            for (ProductOption option : p.getOptions()) {
                String optionType = option.getOptionType() != null ? option.getOptionType() : "custom";
                if (!groups.containsKey(optionType)) {
                    groups.put(optionType, new ArrayList<>());
                }
                groups.get(optionType).add(option);
            }
            
            // Kiểm tra mỗi nhóm có ít nhất 1 option được chọn không
            boolean allGroupsSelected = true;
            for (Map.Entry<String, List<ProductOption>> entry : groups.entrySet()) {
                List<ProductOption> groupOptions = entry.getValue();
                boolean hasSelected = false;
                for (ProductOption option : groupOptions) {
                    if (option.getSelectedValue() != null) {
                        hasSelected = true;
                        break;
                    }
                }
                if (!hasSelected) {
                    allGroupsSelected = false;
                    break;
                }
            }
            
            // Nếu chưa chọn đủ options, ẩn stock status
            if (!allGroupsSelected) {
                stockStatusTextView.setVisibility(View.GONE);
                return;
            }
        } else {
            // Không có options: ẩn stock status (vì stock được quản lý qua options)
            stockStatusTextView.setVisibility(View.GONE);
            return;
        }
        
        // Đã chọn đủ options: hiển thị stock status
        // Tính stock quantity từ option đã chọn (nếu có options giống nhau, lấy từ option đã chọn)
        int stockQty = getCurrentStockQuantityFromSelectedOptions(p);
        
        // Luôn hiển thị stock status khi đã chọn đủ options
        if (stockQty >= 10) {
            stockStatusTextView.setText("Vẫn còn hàng, sẵn sàng để giao");
            stockStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            stockStatusTextView.setVisibility(View.VISIBLE);
        } else if (stockQty < 10 && stockQty > 0) {
            stockStatusTextView.setText("Hãy nhanh lên! Chỉ còn " + stockQty + " sản phẩm trong kho");
            stockStatusTextView.setTextColor(getResources().getColor(android.R.color.black)); // Màu đen thay vì đỏ
            stockStatusTextView.setVisibility(View.VISIBLE);
        } else if (stockQty == 0) {
            stockStatusTextView.setText("Hết hàng");
            stockStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            stockStatusTextView.setVisibility(View.VISIBLE);
        } else {
            // Trường hợp stockQty < 0 (không hợp lệ), vẫn hiển thị để debug
            stockStatusTextView.setText("Số lượng không hợp lệ");
            stockStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            stockStatusTextView.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Tính stock quantity từ các option đã chọn
     * Nếu có options cùng loại, chỉ lấy quantity từ option đã chọn trong mỗi nhóm
     */
    private int getCurrentStockQuantityFromSelectedOptions(Product p) {
        if (p.getOptions() == null || p.getOptions().isEmpty()) {
            return 0;
        }
        
        // Nhóm các options theo option_type (loại) và lấy quantity từ option đã chọn trong mỗi nhóm
        Map<String, List<ProductOption>> groups = new HashMap<>();
        for (ProductOption option : p.getOptions()) {
            String optionType = option.getOptionType() != null ? option.getOptionType() : "custom";
            if (!groups.containsKey(optionType)) {
                groups.put(optionType, new ArrayList<>());
            }
            groups.get(optionType).add(option);
        }
        
        // Lấy quantity nhỏ nhất từ các option đã chọn
        int minStock = Integer.MAX_VALUE;
        for (Map.Entry<String, List<ProductOption>> entry : groups.entrySet()) {
            List<ProductOption> groupOptions = entry.getValue();
            for (ProductOption option : groupOptions) {
                if (option.getSelectedValue() != null) {
                    int qty = option.getSelectedValue().getQuantity();
                    if (qty < minStock) {
                        minStock = qty;
                    }
                    break; // Chỉ lấy quantity từ option đầu tiên được chọn trong nhóm
                }
            }
        }
        
        return minStock == Integer.MAX_VALUE ? 0 : minStock;
    }
    
    private void setupTabs() {
        descriptionTab.setOnClickListener(v -> {
            if (currentSelectedTab != 0) {
                currentSelectedTab = 0;
                updateTabsVisibility();
            }
        });
        
        specsTab.setOnClickListener(v -> {
            if (currentSelectedTab != 1) {
                currentSelectedTab = 1;
                updateTabsVisibility();
            }
        });
    }
    
    private void setupTabsContent(Product p) {
        // Kiểm tra xem có tab nào không
        boolean hasDesc = p.hasDescriptionTab() && p.getDescription() != null && !p.getDescription().isEmpty();
        boolean hasSpecs = p.hasSpecsTab() && p.getTechnicalSpecs() != null && !p.getTechnicalSpecs().isEmpty();
        
        if (!hasDesc && !hasSpecs) {
            // Không có tab nào, ẩn cả container
            findViewById(R.id.productInfoLabelTextView).setVisibility(View.GONE);
            findViewById(R.id.tabsContainer).setVisibility(View.GONE);
            detailDescTextView.setVisibility(View.GONE);
            detailSpecsTextView.setVisibility(View.GONE);
            return;
        }
        
        // Hiển thị label
        findViewById(R.id.productInfoLabelTextView).setVisibility(View.VISIBLE);
        
        if (hasDesc && hasSpecs) {
            // Có cả 2 tabs
            findViewById(R.id.tabsContainer).setVisibility(View.VISIBLE);
            descriptionTab.setVisibility(View.VISIBLE);
            specsTab.setVisibility(View.VISIBLE);
            detailDescTextView.setText(p.getDescription());
            detailSpecsTextView.setText(p.getTechnicalSpecs());
        } else if (hasDesc) {
            // Chỉ có tab mô tả
            findViewById(R.id.tabsContainer).setVisibility(View.GONE);
            detailDescTextView.setText(p.getDescription());
            detailDescTextView.setVisibility(View.VISIBLE);
            detailSpecsTextView.setVisibility(View.GONE);
            currentSelectedTab = 0;
        } else if (hasSpecs) {
            // Chỉ có tab thông số
            findViewById(R.id.tabsContainer).setVisibility(View.GONE);
            detailDescTextView.setVisibility(View.GONE);
            detailSpecsTextView.setText(p.getTechnicalSpecs());
            detailSpecsTextView.setVisibility(View.VISIBLE);
            currentSelectedTab = 1;
        }
        
        updateTabsVisibility();
    }
    
    private void updateTabsVisibility() {
        if (fullProduct == null) return;
        
        boolean hasDesc = fullProduct.hasDescriptionTab() && fullProduct.getDescription() != null && !fullProduct.getDescription().isEmpty();
        boolean hasSpecs = fullProduct.hasSpecsTab() && fullProduct.getTechnicalSpecs() != null && !fullProduct.getTechnicalSpecs().isEmpty();
        
        if (hasDesc && hasSpecs) {
            // Có cả 2 tabs, hiển thị theo tab đã chọn
            if (currentSelectedTab == 0) {
                detailDescTextView.setVisibility(View.VISIBLE);
                detailSpecsTextView.setVisibility(View.GONE);
                descriptionTab.setBackgroundColor(getResources().getColor(android.R.color.white));
                specsTab.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            } else {
                detailDescTextView.setVisibility(View.GONE);
                detailSpecsTextView.setVisibility(View.VISIBLE);
                descriptionTab.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                specsTab.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        }
    }
    
    private int getMaxQuantity() {
        if (fullProduct != null) {
            return fullProduct.getCurrentStockQuantity();
        } else if (product != null) {
            return product.getStockQuantity();
        }
        return 999;
    }
    
    /**
     * Kiểm tra xem tất cả options đã được chọn chưa
     * Nếu có options cùng loại (cùng option_type), chỉ cần chọn 1 trong mỗi nhóm
     */
    private boolean areAllOptionsSelected() {
        Product p = (fullProduct != null) ? fullProduct : product;
        if (p == null) return true; // Không có product thì cho phép (fallback)
        
        if (p.getOptions() == null || p.getOptions().isEmpty()) {
            // Không có options thì cho phép
            return true;
        }
        
        // Nhóm các options theo option_type (loại)
        Map<String, List<ProductOption>> groups = new HashMap<>();
        for (ProductOption option : p.getOptions()) {
            String optionType = option.getOptionType() != null ? option.getOptionType() : "custom";
            if (!groups.containsKey(optionType)) {
                groups.put(optionType, new ArrayList<>());
            }
            groups.get(optionType).add(option);
        }
        
        // Kiểm tra mỗi nhóm có ít nhất 1 option được chọn không
        for (Map.Entry<String, List<ProductOption>> entry : groups.entrySet()) {
            List<ProductOption> groupOptions = entry.getValue();
            boolean hasSelected = false;
            for (ProductOption option : groupOptions) {
                if (option.getSelectedValue() != null) {
                    hasSelected = true;
                    break;
                }
            }
            if (!hasSelected) {
                return false; // Có ít nhất 1 nhóm chưa chọn option nào
            }
        }
        
        return true; // Tất cả nhóm đã chọn
    }
    
    /**
     * Cập nhật trạng thái enable/disable của các buttons trong nhóm options cùng loại
     */
    private void updateOptionGroupsState(String optionType) {
        List<LinearLayout> containers = optionGroups.get(optionType);
        List<ProductOption> sameTypeOptions = optionGroupsByOption.get(optionType);
        
        if (containers == null || sameTypeOptions == null) return;
        
        // Tìm option nào đã được chọn trong nhóm này
        ProductOption selectedOption = null;
        for (ProductOption option : sameTypeOptions) {
            if (option.getSelectedValue() != null) {
                selectedOption = option;
                break;
            }
        }
        
        // Enable/disable các buttons dựa trên việc có option nào được chọn không
        for (LinearLayout container : containers) {
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                if (child instanceof Button) {
                    Button button = (Button) child;
                    ProductOption option = buttonToOptionMap.get(button);
                    
                    if (selectedOption != null && option != null) {
                        // Có option được chọn trong nhóm
                        if (option.getOptionId() == selectedOption.getOptionId()) {
                            // Option này đã được chọn: enable
                            button.setEnabled(true);
                            button.setAlpha(1.0f);
                        } else {
                            // Option khác đã được chọn: disable
                            button.setEnabled(false);
                            button.setAlpha(0.5f);
                        }
                    } else {
                        // Không có option nào được chọn: enable tất cả
                        button.setEnabled(true);
                        button.setAlpha(1.0f);
                    }
                }
            }
        }
    }
    
    /**
     * Cập nhật trạng thái enable/disable của buttons
     */
    private void updateButtonsState() {
        Button addToCartButton = findViewById(R.id.addToCartButton);
        Button buyNowButton = findViewById(R.id.btnBuyNow);
        
        boolean allSelected = areAllOptionsSelected();
        
        // Kiểm tra stock quantity
        boolean hasStock = true;
        Product currentProduct = (fullProduct != null) ? fullProduct : product;
        if (currentProduct != null && allSelected) {
            int stockQty = getCurrentStockQuantityFromSelectedOptions(currentProduct);
            hasStock = (stockQty > 0);
        }
        
        // Button chỉ enable khi đã chọn đủ options VÀ còn hàng
        boolean shouldEnable = allSelected && hasStock;
        
        addToCartButton.setEnabled(shouldEnable);
        buyNowButton.setEnabled(shouldEnable);
        
        // Thay đổi màu để người dùng biết button bị disable
        if (shouldEnable) {
            addToCartButton.setAlpha(1.0f);
            buyNowButton.setAlpha(1.0f);
        } else {
            addToCartButton.setAlpha(0.5f);
            buyNowButton.setAlpha(0.5f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return true;
    }
}
