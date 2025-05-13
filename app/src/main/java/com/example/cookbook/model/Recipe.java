package com.example.cookbook.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String title;
    private String category;
    private List<Ingredient> ingredients;
    private String instructions;
    private String imageUrl;
    private String userId;
    private boolean isFavorite;
    private long createdAt;

    public Recipe() {
        // Required empty constructor for Firestore
        ingredients = new ArrayList<>();
        createdAt = System.currentTimeMillis();
    }

    public Recipe(String title, String category, List<Ingredient> ingredients, String instructions) {
        this.title = title;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
} 