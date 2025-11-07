package com.example.pickleballshopapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // Định nghĩa endpoint. Tên file "get_products.php"
    // phải khớp với tên file PHP của bạn.
    @GET("get_products.php")
    Call<ProductResponse> getProducts();

    // Lấy sản phẩm có filter category/brand (nullable) + phân trang (tuỳ chọn)
    @GET("get_products.php")
    Call<ProductResponse> getProductsFiltered(
            @Query("category_id") Integer categoryId,
            @Query("brand_id") Integer brandId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    // Sau này bạn sẽ thêm các API khác ở đây, ví dụ:
    // @POST("login.php")
    // Call<LoginResponse> loginUser(@Field("email") String email, @Field("password") String password);
    // API Đăng nhập
    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // API Đăng ký
    @POST("register.php")
    Call<AuthResponse> registerUser(@Body RegisterRequest registerRequest);

    // ==============================

    // Cart APIs
    @GET("cart_get.php")
    Call<CartResponse> getCart(@Query("user_id") int userId);

    @POST("cart_add.php")
    Call<BaseResponse> addToCart(@Body CartAddRequest req);

    @POST("cart_update.php")
    Call<BaseResponse> updateCart(@Body CartUpdateRequest req);

    @POST("cart_remove.php")
    Call<BaseResponse> removeFromCart(@Body CartRemoveRequest req);

    @POST("cart_clear.php")
    Call<BaseResponse> clearCart(@Body UserOnlyRequest req);

    // Orders APIs
    @POST("order_checkout.php")
    Call<CheckoutResponse> checkout(@Body UserOnlyRequest req);

    @GET("orders_list.php")
    Call<OrdersResponse> getOrders(@Query("user_id") int userId);

    @POST("order_cancel.php")
    Call<BaseResponse> cancelOrder(@Body CancelOrderRequest req);

    @POST("user_update.php")
    Call<BaseResponse> updateUser(@Body UpdateUserRequest req);

    @GET("order_details.php")
    Call<OrderDetailsResponse> getOrderDetails(@Query("order_id") int orderId, @Query("user_id") int userId);

    // API Lấy chi tiết sản phẩm
    @GET("get_product_detail.php")
    Call<ProductDetailResponse> getProductDetail(@Query("product_id") int productId);

    // API Lấy New Arrival
    @GET("get_new_arrival.php")
    Call<ProductResponse> getNewArrival();

    // API Lấy Best Seller
    @GET("get_best_seller.php")
    Call<ProductResponse> getBestSeller();

    // API Lấy tất cả New Arrival (không giới hạn)
    @GET("get_all_new_arrival.php")
    Call<ProductResponse> getAllNewArrival();

    // API Lấy tất cả Best Seller (không giới hạn)
    @GET("get_all_best_seller.php")
    Call<ProductResponse> getAllBestSeller();

    // API Tìm kiếm sản phẩm
    @GET("search_products.php")
    Call<ProductResponse> searchProducts(@Query("keyword") String keyword);

    // API Lấy Giày
    @GET("get_shoes.php")
    Call<ProductResponse> getShoes();

    // API Lấy Bóng thi đấu
    @GET("get_balls.php")
    Call<ProductResponse> getBalls();

    // API Lấy Carousel Images
    @GET("get_carousel.php")
    Call<CarouselResponse> getCarouselImages();

    // Lấy brands theo category để hiển thị trong drawer
    @GET("get_brands.php")
    Call<BrandResponse> getBrandsByCategory(@Query("category_id") int categoryId);

    // OTP APIs
    @POST("send_otp.php")
    Call<SendOtpResponse> sendOtp(@Body SendOtpRequest request);

    @POST("verify_otp.php")
    Call<BaseResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("register_with_otp.php")
    Call<AuthResponse> registerWithOtp(@Body RegisterWithOtpRequest request);

    @POST("reset_password_with_otp.php")
    Call<BaseResponse> resetPasswordWithOtp(@Body ResetPasswordRequest request);
}
