package com.example.cookbook.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid;
    private String email;
    private List<String> favoriteRecipes;
    private List<String> customIngredients;

    public User() {
        // Required empty constructor for Firestore
        favoriteRecipes = new ArrayList<>();
        customIngredients = new ArrayList<>();
    }

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.favoriteRecipes = new ArrayList<>();
        this.customIngredients = new ArrayList<>();
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getFavoriteRecipes() { return favoriteRecipes; }
    public void setFavoriteRecipes(List<String> favoriteRecipes) { this.favoriteRecipes = favoriteRecipes; }

    public List<String> getCustomIngredients() { return customIngredients; }
    public void setCustomIngredients(List<String> customIngredients) { this.customIngredients = customIngredients; }

    // Helper methods
    public void addFavoriteRecipe(String recipeId) {
        if (!favoriteRecipes.contains(recipeId)) {
            favoriteRecipes.add(recipeId);
        }
    }

    public void removeFavoriteRecipe(String recipeId) {
        favoriteRecipes.remove(recipeId);
    }

    public void addCustomIngredient(String ingredient) {
        if (!customIngredients.contains(ingredient)) {
            customIngredients.add(ingredient);
        }
    }

    public void removeCustomIngredient(String ingredient) {
        customIngredients.remove(ingredient);
    }
} 