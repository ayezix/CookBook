package com.example.cookbook.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import com.google.firebase.firestore.PropertyName;

/**
 * Recipe - Core data model representing a recipe in the CookBook application.
 * 
 * This class serves as the primary data structure for recipes throughout the application.
 * It can represent both user-created recipes and recipes imported from external APIs.
 * 
 * Key Features:
 * - Serializable for data transfer between activities
 * - Firestore-compatible with PropertyName annotations
 * - Supports both local and API-imported recipes
 * - Includes favorite status and user ownership
 * 
 * The Recipe model is used across all components:
 * - Recipe creation and editing
 * - Recipe display and listing
 * - Search and filtering
 * - Favorites management
 * - Data persistence in Firebase
 */
public class Recipe implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Core recipe information
    private String id;              // Unique identifier (Firestore document ID)
    private String title;           // Recipe name
    private String category;        // Recipe category (e.g., "Breakfast", "Dinner")
    private List<Ingredient> ingredients; // List of ingredients with amounts
    private String instructions;    // Step-by-step cooking instructions
    private String imageUrl;        // URL to recipe image
    
    // User and ownership information
    private String userId;          // ID of the user who created the recipe
    private boolean favorite;       // Whether recipe is marked as favorite
    private long createdAt;         // Timestamp when recipe was created
    private boolean importedFromApi; // Whether recipe came from external API

    /**
     * Default constructor required for Firestore serialization.
     * 
     * This constructor initializes the recipe with default values:
     * - Empty ingredients list
     * - Current timestamp for creation time
     * - Other fields remain null/false until set
     */
    public Recipe() {
        ingredients = new ArrayList<>();
        createdAt = System.currentTimeMillis();
    }

    /**
     * Constructor for creating a new recipe with basic information.
     * 
     * This constructor is typically used when creating new recipes
     * in the AddRecipeActivity. It sets the essential recipe data
     * and initializes the creation timestamp.
     * 
     * @param title The title/name of the recipe
     * @param category The recipe category (e.g., "Breakfast", "Dinner")
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

    // Getters and Setters with documentation
    
    /**
     * Gets the unique identifier for this recipe.
     * 
     * The ID is typically the Firestore document ID and is used
     * for database operations, updates, and deletions.
     * 
     * @return The recipe ID (Firestore document ID)
     */
    public String getId() { return id; }
    
    /**
     * Sets the unique identifier for this recipe.
     * 
     * @param id The recipe ID to set (usually Firestore document ID)
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the title of the recipe.
     * 
     * The title is displayed in recipe lists, search results,
     * and recipe detail screens.
     * 
     * @return The recipe title/name
     */
    public String getTitle() { return title; }
    
    /**
     * Sets the title of the recipe.
     * 
     * @param title The recipe title to set
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the category of the recipe.
     * 
     * Categories are used for filtering and organizing recipes.
     * Common categories include: "Breakfast", "Lunch", "Dinner", "Dessert", "Snack"
     * 
     * @return The recipe category
     */
    public String getCategory() { return category; }
    
    /**
     * Sets the category of the recipe.
     * 
     * @param category The recipe category to set
     */
    public void setCategory(String category) { this.category = category; }

    /**
     * Gets the list of ingredients for this recipe.
     * 
     * Each ingredient contains name, amount, and unit information.
     * This list is displayed in recipe details and used for
     * ingredient-based filtering.
     * 
     * @return List of ingredients with amounts and units
     */
    public List<Ingredient> getIngredients() { return ingredients; }
    
    /**
     * Sets the list of ingredients for this recipe.
     * 
     * @param ingredients The list of ingredients to set
     */
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    /**
     * Gets the cooking instructions for this recipe.
     * 
     * Instructions contain step-by-step directions for preparing
     * the recipe. They are displayed in recipe detail screens.
     * 
     * @return The recipe instructions
     */
    public String getInstructions() { return instructions; }
    
    /**
     * Sets the cooking instructions for this recipe.
     * 
     * @param instructions The recipe instructions to set
     */
    public void setInstructions(String instructions) { this.instructions = instructions; }

    /**
     * Gets the URL of the recipe image.
     * 
     * The image URL points to an external image (typically uploaded
     * to ImgBB or from TheMealDB API). Images are displayed in
     * recipe lists and detail screens.
     * 
     * @return The image URL
     */
    public String getImageUrl() { return imageUrl; }
    
    /**
     * Sets the URL of the recipe image.
     * 
     * @param imageUrl The image URL to set
     */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /**
     * Gets the user ID of the recipe creator.
     * 
     * The user ID is used to:
     * - Filter recipes by ownership
     * - Control edit/delete permissions
     * - Associate recipes with specific users
     * 
     * @return The user ID of the recipe creator
     */
    public String getUserId() { return userId; }
    
    /**
     * Sets the user ID of the recipe creator.
     * 
     * @param userId The user ID to set
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Checks if this recipe is marked as a favorite.
     * 
     * Favorite status is used to:
     * - Display recipes in the favorites tab
     * - Show favorite icons in recipe lists
     * - Filter recipes by favorite status
     * 
     * @return true if the recipe is favorited, false otherwise
     */
    @PropertyName("favorite")
    public boolean isFavorite() { return favorite; }
    
    /**
     * Sets the favorite status of this recipe.
     * 
     * @param favorite The favorite status to set
     */
    @PropertyName("favorite")
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    /**
     * Gets the creation timestamp of this recipe.
     * 
     * The creation timestamp is used for:
     * - Sorting recipes by creation date
     * - Tracking when recipes were added
     * - Data migration and cleanup operations
     * 
     * @return The creation timestamp in milliseconds
     */
    public long getCreatedAt() { return createdAt; }
    
    /**
     * Sets the creation timestamp of this recipe.
     * 
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    /**
     * Checks if this recipe was imported from an external API.
     * 
     * This flag is used to:
     * - Distinguish between user-created and API recipes
     * - Control edit permissions (API recipes cannot be edited)
     * - Apply different display logic for API vs local recipes
     * - Handle data migration and updates
     * 
     * @return true if imported from API, false if user-created
     */
    public boolean isImportedFromApi() { return importedFromApi; }
    
    /**
     * Sets whether this recipe was imported from an external API.
     * 
     * @param importedFromApi The import status to set
     */
    public void setImportedFromApi(boolean importedFromApi) { this.importedFromApi = importedFromApi; }
} 