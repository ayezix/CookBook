package com.example.cookbook.api;

import com.example.cookbook.api.model.ApiRecipeResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApiService {
    @GET("recipes/complexSearch")
    Call<ApiRecipeResponse> searchRecipes(
        @Query("apiKey") String apiKey,
        @Query("query") String query,
        @Query("number") int number,
        @Query("addRecipeInformation") boolean addRecipeInformation
    );

    @GET("recipes/random")
    Call<ApiRecipeResponse> getRandomRecipes(
        @Query("apiKey") String apiKey,
        @Query("number") int number,
        @Query("tags") String tags
    );
} 