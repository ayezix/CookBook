package com.example.cookbook.api;

import com.example.cookbook.api.model.ApiRecipe;
import com.example.cookbook.api.model.ApiRecipeResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface RecipeApiService {
    @GET("recipes/complexSearch")
    Call<ApiRecipeResponse> searchRecipes(
        @Query("apiKey") String apiKey,
        @Query("query") String query,
        @Query("number") int number,
        @Query("addRecipeInformation") boolean addRecipeInformation,
        @Query("instructionsRequired") boolean instructionsRequired,
        @Query("fillIngredients") boolean fillIngredients,
        @Query("addRecipeNutrition") boolean addRecipeNutrition
    );

    @GET("recipes/informationBulk")
    Call<List<ApiRecipe>> getRecipeInformation(
        @Query("apiKey") String apiKey,
        @Query("ids") String ids,
        @Query("includeNutrition") boolean includeNutrition
    );

    @GET("recipes/random")
    Call<ApiRecipeResponse> getRandomRecipes(
        @Query("apiKey") String apiKey,
        @Query("number") int number,
        @Query("tags") String tags
    );
} 