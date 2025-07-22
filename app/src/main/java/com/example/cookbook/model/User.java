package com.example.cookbook.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User - Data model representing a user in the CookBook application.
 * 
 * This class represents a user account with basic profile information
 * and preferences. User data is stored in Firestore and associated
 * with recipes and favorites.
 * 
 * Key Features:
 * - Firestore-compatible for database storage
 * - Stores user preferences and favorites
 * - Links recipes to specific users
 * - Supports custom ingredients list
 * 
 * Usage:
 * - User authentication and profile management
 * - Recipe ownership and permissions
 * - Favorites management
 * - User preferences storage
 */
public class User {
    // User identification
    private String uid;              // Firebase Auth UID (unique user identifier)
    private String email;            // User's email address
    
    // User preferences and data
    private List<String> favoriteRecipes;    // List of favorite recipe IDs
    private List<String> customIngredients;  // List of custom ingredients added by user

    /**
     * Default constructor required for Firestore serialization.
     * 
     * This constructor initializes the user with empty lists for
     * favorites and custom ingredients. It's used by Firestore
     * when deserializing user data from the database.
     */
    public User() {
        // Required empty constructor for Firestore
        favoriteRecipes = new ArrayList<>();
        customIngredients = new ArrayList<>();
    }

    /**
     * Constructor for creating a new user with basic information.
     * 
     * This constructor is typically used when registering a new user.
     * It initializes the user with the provided UID and email,
     * and creates empty lists for favorites and custom ingredients.
     * 
     * @param uid The Firebase Auth UID for the user
     * @param email The user's email address
     */
    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.favoriteRecipes = new ArrayList<>();
        this.customIngredients = new ArrayList<>();
    }

    // Getters and Setters
    
    /**
     * Gets the Firebase Auth UID for this user.
     * 
     * The UID is the unique identifier assigned by Firebase Authentication.
     * It's used to:
     * - Link recipes to specific users
     * - Control access permissions
     * - Identify users in the database
     * 
     * @return The Firebase Auth UID
     */
    public String getUid() { return uid; }
    
    /**
     * Sets the Firebase Auth UID for this user.
     * 
     * @param uid The Firebase Auth UID to set
     */
    public void setUid(String uid) { this.uid = uid; }

    /**
     * Gets the email address for this user.
     * 
     * The email is used for:
     * - User identification and display
     * - Password reset functionality
     * - User profile information
     * 
     * @return The user's email address
     */
    public String getEmail() { return email; }
    
    /**
     * Sets the email address for this user.
     * 
     * @param email The email address to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the list of favorite recipe IDs for this user.
     * 
     * This list contains the IDs of recipes that the user has
     * marked as favorites. It's used to:
     * - Display favorite recipes in the favorites tab
     * - Show favorite status in recipe lists
     * - Sync favorites across devices
     * 
     * @return List of favorite recipe IDs
     */
    public List<String> getFavoriteRecipes() { return favoriteRecipes; }
    
    /**
     * Sets the list of favorite recipe IDs for this user.
     * 
     * @param favoriteRecipes The list of favorite recipe IDs to set
     */
    public void setFavoriteRecipes(List<String> favoriteRecipes) { this.favoriteRecipes = favoriteRecipes; }

    /**
     * Gets the list of custom ingredients for this user.
     * 
     * This list contains custom ingredients that the user has added
     * to their personal ingredient database. It can be used for:
     * - Auto-completion in recipe creation
     * - Personal ingredient preferences
     * - Quick ingredient selection
     * 
     * @return List of custom ingredient names
     */
    public List<String> getCustomIngredients() { return customIngredients; }
    
    /**
     * Sets the list of custom ingredients for this user.
     * 
     * @param customIngredients The list of custom ingredients to set
     */
    public void setCustomIngredients(List<String> customIngredients) { this.customIngredients = customIngredients; }
} 