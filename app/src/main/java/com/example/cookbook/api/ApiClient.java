package com.example.cookbook.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiClient - Retrofit client configuration for TheMealDB API.
 * 
 * This class provides a singleton Retrofit client configured for making HTTP requests
 * to TheMealDB API. It handles:
 * - Base URL configuration
 * - HTTP client setup with logging
 * - JSON response parsing with GSON
 * - Service interface creation
 * 
 * The client is configured with:
 * - Base URL: https://www.themealdb.com/api/json/v1/1/
 * - HTTP logging for debugging
 * - GSON converter for JSON parsing
 */
public class ApiClient {
    /** Base URL for TheMealDB API */
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    
    /** Singleton Retrofit instance */
    private static Retrofit retrofit = null;

    /**
     * Gets the configured Retrofit client instance.
     * 
     * This method implements lazy initialization - the Retrofit client is only
     * created when first requested. The client is configured with:
     * - HTTP logging interceptor for debugging API calls
     * - OkHttp client with interceptors
     * - GSON converter for JSON parsing
     * 
     * @return Configured Retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create HTTP logging interceptor for debugging
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configure OkHttp client with logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Creates and returns a RecipeApiService instance.
     * 
     * This method creates a service interface that defines all the API endpoints
     * for TheMealDB. The service can be used to make HTTP requests to the API.
     * 
     * @return RecipeApiService instance for making API calls
     */
    public static RecipeApiService getRecipeService() {
        return getClient().create(RecipeApiService.class);
    }
} 