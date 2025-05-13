package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiRecipe {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("readyInMinutes")
    private int readyInMinutes;

    @SerializedName("servings")
    private int servings;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("extendedIngredients")
    private List<ApiIngredient> ingredients;

    @SerializedName("dishTypes")
    private List<String> dishTypes;

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public int getReadyInMinutes() { return readyInMinutes; }
    public int getServings() { return servings; }
    public String getInstructions() { return instructions; }
    public List<ApiIngredient> getIngredients() { return ingredients; }
    public List<String> getDishTypes() { return dishTypes; }
} 