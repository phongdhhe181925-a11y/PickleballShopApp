package com.example.pickleballshopapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static ApiService apiService;

    // Lưu ý: Emulator Android Studio: http://10.0.2.2/pickleball_api/
    // Có thể đổi sang IP LAN nếu chạy trên thiết bị thật
    private static final String BASE_URL = "http://10.0.2.2/pickleball_api/";

    public static ApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}


