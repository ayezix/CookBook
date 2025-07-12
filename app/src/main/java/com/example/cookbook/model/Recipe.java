package com.example.cookbook.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import com.google.firebase.firestore.PropertyName;

/**
 * Recipe model class representing a recipe in the CookBook application.
 * This class is used for both user-created recipes and recipes imported from external APIs.
 * Implements Serializable for data transfer between activities.
 */
public class Recipe implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String title;
    private String category;
    private List<Ingredient> ingredients;
    private String instructions;
    private String imageUrl;
    private String userId;
    private boolean favorite;
    private long createdAt;
    private boolean importedFromApi;

    /**
     * Default constructor required for Firestore serialization.
     * Initializes ingredients list and sets creation timestamp.
     */
    public Recipe() {
        ingredients = new ArrayList<>();
        createdAt = System.currentTimeMillis();
    }

    /**
     * Constructor for creating a new recipe with basic information.
     * 
     * @param title The title of the recipe
     * @param category The category of the recipe (e.g., "Breakfast", "Dinner")
     * @param ingredients List of ingredients required for the recipe
     * @param instructions Step-by-step cooking instructions
     */
    public Recipe(String title, String category, List<Ingredient> ingredients, String instructions) {
        this.title = title;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    /**
     * Gets the unique identifier for this recipe.
     * @return The recipe ID
     */
    public String getId() { return id; }
    
    /**
     * Sets the unique identifier for this recipe.
     * @param id The recipe ID to set
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the title of the recipe.
     * @return The recipe title
     */
    public String getTitle() { return title; }
    
    /**
     * Sets the title of the recipe.
     * @param title The recipe title to set
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the category of the recipe.
     * @return The recipe category
     */
    public String getCategory() { return category; }
    
    /**
     * Sets the category of the recipe.
     * @param category The recipe category to set
     */
    public void setCategory(String category) { this.category = category; }

    /**
     * Gets the list of ingredients for this recipe.
     * @return List of ingredients
     */
    public List<Ingredient> getIngredients() { return ingredients; }
    
    /**
     * Sets the list of ingredients for this recipe.
     * @param ingredients The list of ingredients to set
     */
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    /**
     * Gets the cooking instructions for this recipe.
     * @return The recipe instructions
     */
    public String getInstructions() { return instructions; }
    
    /**
     * Sets the cooking instructions for this recipe.
     * @param instructions The recipe instructions to set
     */
    public void setInstructions(String instructions) { this.instructions = instructions; }

    /**
     * Gets the URL of the recipe image.
     * @return The image URL
     */
    public String getImageUrl() { return imageUrl; }
    
    /**
     * Sets the URL of the recipe image.
     * @param imageUrl The image URL to set
     */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /**
     * Gets the user ID of the recipe creator.
     * @return The user ID
     */
    public String getUserId() { return userId; }
    
    /**
     * Sets the user ID of the recipe creator.
     * @param userId The user ID to set
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Checks if this recipe is marked as a favorite.
     * @return true if the recipe is favorited, false otherwise
     */
    @PropertyName("favorite")
    public boolean isFavorite() { return favorite; }
    
    /**
     * Sets the favorite status of this recipe.
     * @param favorite The favorite status to set
     */
    @PropertyName("favorite")
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    /**
     * Gets the creation timestamp of this recipe.
     * @return The creation timestamp in milliseconds
     */
    public long getCreatedAt() { return createdAt; }
    
    /**
     * Sets the creation timestamp of this recipe.
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    /**
     * Checks if this recipe was imported from an external API.
     * @return true if imported from API, false if user-created
     */
    public boolean isImportedFromApi() { return importedFromApi; }
    
    /**
     * Sets whether this recipe was imported from an external API.
     * @param importedFromApi The import status to set
     */
    public void setImportedFromApi(boolean importedFromApi) { this.importedFromApi = importedFromApi; }
} 