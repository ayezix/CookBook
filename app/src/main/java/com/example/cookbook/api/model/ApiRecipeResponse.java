package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiRecipeResponse {
    @SerializedName("meals")
    private List<ApiRecipe> meals;

    public List<ApiRecipe> getResults() {
        return meals;
    }
} 