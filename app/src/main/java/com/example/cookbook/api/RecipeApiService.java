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

/**
 * RecipeApiService - Retrofit interface defining TheMealDB API endpoints.
 * 
 * This interface contains all the API endpoint definitions for TheMealDB.
 * Each method represents a different API call that can be made to retrieve
 * recipe data, search recipes, or get filter options.
 * 
 * The interface uses Retrofit annotations to define:
 * - HTTP method (GET)
 * - Endpoint paths
 * - Query parameters
 * - Response types
 */
public interface RecipeApiService {
    
    /**
     * Searches for recipes by name.
     * 
     * Endpoint: GET /search.php?s={query}
     * 
     * This endpoint searches TheMealDB for recipes whose names contain
     * the specified query string. Returns a list of matching recipes.
     * 
     * @param query The search query (recipe name)
     * @return Call containing ApiRecipeResponse with matching recipes
     */
    @GET("search.php")
    Call<ApiRecipeResponse> searchRecipes(
        @Query("s") String query
    );

    /**
     * Gets detailed information about a specific recipe by ID.
     * 
     * Endpoint: GET /lookup.php?i={id}
     * 
     * This endpoint retrieves complete recipe details including ingredients,
     * instructions, and other metadata for a specific recipe ID.
     * 
     * @param id The unique recipe ID
     * @return Call containing ApiRecipeResponse with recipe details
     */
    @GET("lookup.php")
    Call<ApiRecipeResponse> getRecipeInformation(
        @Query("i") String id
    );

    // Filtering endpoints
    
    /**
     * Filters recipes by category.
     * 
     * Endpoint: GET /filter.php?c={category}
     * 
     * This endpoint returns all recipes that belong to the specified category
     * (e.g., "Breakfast", "Dinner", "Dessert").
     * 
     * @param category The recipe category to filter by
     * @return Call containing ApiRecipeResponse with filtered recipes
     */
    @GET("filter.php")
    Call<ApiRecipeResponse> filterByCategory(
        @Query("c") String category
    );

    /**
     * Filters recipes by cuisine/area.
     * 
     * Endpoint: GET /filter.php?a={area}
     * 
     * This endpoint returns all recipes from the specified cuisine or area
     * (e.g., "Italian", "Mexican", "American").
     * 
     * @param area The cuisine/area to filter by
     * @return Call containing ApiRecipeResponse with filtered recipes
     */
    @GET("filter.php")
    Call<ApiRecipeResponse> filterByArea(
        @Query("a") String area
    );

    /**
     * Filters recipes by ingredient.
     * 
     * Endpoint: GET /filter.php?i={ingredient}
     * 
     * This endpoint returns all recipes that contain the specified ingredient.
     * 
     * @param ingredient The ingredient to filter by
     * @return Call containing ApiRecipeResponse with filtered recipes
     */
    @GET("filter.php")
    Call<ApiRecipeResponse> filterByIngredient(
        @Query("i") String ingredient
    );

    // List endpoints for filter options
    
    /**
     * Gets all available recipe categories.
     * 
     * Endpoint: GET /categories.php
     * 
     * This endpoint returns a list of all recipe categories available
     * in TheMealDB (e.g., "Breakfast", "Lunch", "Dinner", "Dessert").
     * 
     * @return Call containing CategoryResponse with available categories
     */
    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    /**
     * Gets all available cuisine areas.
     * 
     * Endpoint: GET /list.php?a=list
     * 
     * This endpoint returns a list of all cuisine areas available
     * in TheMealDB (e.g., "Italian", "Mexican", "American").
     * 
     * @param list Must be "list" to get all areas
     * @return Call containing AreaResponse with available areas
     */
    @GET("list.php")
    Call<AreaResponse> getAreas(
        @Query("a") String list
    );

    /**
     * Gets all available ingredients.
     * 
     * Endpoint: GET /list.php?i=list
     * 
     * This endpoint returns a list of all ingredients available
     * in TheMealDB for filtering purposes.
     * 
     * @param list Must be "list" to get all ingredients
     * @return Call containing IngredientResponse with available ingredients
     */
    @GET("list.php")
    Call<IngredientResponse> getIngredients(
        @Query("i") String list
    );
} 