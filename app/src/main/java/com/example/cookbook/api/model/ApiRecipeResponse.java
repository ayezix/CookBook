package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiRecipeResponse {
    @SerializedName("results")
    private List<ApiRecipe> results;

    @SerializedName("recipes")
    private List<ApiRecipe> recipes;

    public List<ApiRecipe> getResults() {
        return results != null ? results : recipes;
    }
} 