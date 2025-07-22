package com.example.cookbook.model;

import java.io.Serializable;

/**
 * Ingredient - Data model representing an ingredient in a recipe.
 * 
 * This class represents a single ingredient with its name, amount, and unit.
 * Ingredients are used within Recipe objects to define what is needed
 * to prepare a recipe.
 * 
 * Key Features:
 * - Serializable for data transfer between activities
 * - Firestore-compatible for database storage
 * - Simple structure with name, amount, and unit
 * 
 * Usage:
 * - Stored as part of Recipe objects
 * - Displayed in recipe detail screens
 * - Used for ingredient-based filtering
 * - Managed in AddRecipeActivity for recipe creation
 */
public class Ingredient implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Ingredient properties
    private String name;    // Ingredient name (e.g., "Flour", "Sugar", "Eggs")
    private String amount;  // Quantity amount (e.g., "2", "1/2", "3")
    private String unit;    // Unit of measurement (e.g., "cups", "tablespoons", "pieces")

    /**
     * Default constructor required for Firebase/Firestore serialization.
     * 
     * This constructor is used by Firestore when deserializing
     * ingredient data from the database.
     */
    public Ingredient() {
        // Required empty constructor for Firebase
    }

    /**
     * Constructor for creating an ingredient with name, amount, and unit.
     * 
     * This constructor is typically used when adding ingredients
     * to recipes in the AddRecipeActivity.
     * 
     * @param name The name of the ingredient (e.g., "Flour", "Sugar")
     * @param amount The quantity amount (e.g., "2", "1/2")
     * @param unit The unit of measurement (e.g., "cups", "tablespoons")
     */
    public Ingredient(String name, String amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    // Getters and Setters
    
    /**
     * Gets the name of the ingredient.
     * 
     * The ingredient name is displayed in recipe details and
     * used for ingredient-based filtering and search.
     * 
     * @return The ingredient name
     */
    public String getName() { return name; }
    
    /**
     * Sets the name of the ingredient.
     * 
     * @param name The ingredient name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the amount/quantity of the ingredient.
     * 
     * The amount represents how much of the ingredient is needed.
     * It can be a number, fraction, or other quantity representation.
     * 
     * @return The ingredient amount
     */
    public String getAmount() { return amount; }
    
    /**
     * Sets the amount/quantity of the ingredient.
     * 
     * @param amount The ingredient amount to set
     */
    public void setAmount(String amount) { this.amount = amount; }

    /**
     * Gets the unit of measurement for the ingredient.
     * 
     * The unit specifies how the amount should be measured
     * (e.g., cups, tablespoons, grams, pieces).
     * 
     * @return The unit of measurement
     */
    public String getUnit() { return unit; }
    
    /**
     * Sets the unit of measurement for the ingredient.
     * 
     * @param unit The unit of measurement to set
     */
    public void setUnit(String unit) { this.unit = unit; }
} 