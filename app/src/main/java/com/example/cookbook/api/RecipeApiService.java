package com.example.cookbook.api;

import com.example.cookbook.api.model.ApiRecipe;
import com.example.cookbook.api.model.ApiRecipeResponse;
import com.example.cookbook.api.model.CategoryResponse;
import com.example.cookbook.api.model.AreaResponse;
import com.example.cookbook.api.model.IngredientResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface RecipeApiService {
    @GET("search.php")
    Call<ApiRecipeResponse> searchRecipes(
        @Query("s") String query
    );

    @GET("lookup.php")
    Call<ApiRecipeResponse> getRecipeInformation(
        @Query("i") String id
    );

    @GET("random.php")
    Call<ApiRecipeResponse> getRandomRecipes();

    // Filtering endpoints
    @GET("filter.php")
    Call<ApiRecipeResponse> filterByCategory(
        @Query("c") String category
    );

    @GET("filter.php")
    Call<ApiRecipeResponse> filterByArea(
        @Query("a") String area
    );

    @GET("filter.php")
    Call<ApiRecipeResponse> filterByIngredient(
        @Query("i") String ingredient
    );

    // List endpoints for filter options
    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    @GET("list.php")
    Call<AreaResponse> getAreas(
        @Query("a") String list
    );

    @GET("list.php")
    Call<IngredientResponse> getIngredients(
        @Query("i") String list
    );
} 