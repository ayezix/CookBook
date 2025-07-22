package com.example.cookbook.model;

import java.util.List;
import java.util.ArrayList;

/**
 * RecipeFilter - Data model for recipe filtering and search criteria.
 * 
 * This class represents filtering criteria used to search and filter recipes
 * in the CookBook application. It supports multiple filter types and can
 * handle both single values and multiple values for filtering.
 * 
 * Key Features:
 * - Multiple filter types (category, area, ingredient, search)
 * - Support for single and multiple values
 * - Helper methods for common filter types
 * - Dietary restriction filters (vegan, vegetarian, gluten-free)
 * 
 * Usage:
 * - Recipe search functionality
 * - Filter dialog implementation
 * - API query construction
 * - Local recipe filtering
 */
public class RecipeFilter {
    
    /**
     * Enum defining the different types of filters available.
     * 
     * Each filter type corresponds to a different way of filtering recipes:
     * - CATEGORY: Filter by recipe category (e.g., "Breakfast", "Dinner")
     * - AREA: Filter by cuisine/area (e.g., "Italian", "Mexican")
     * - INGREDIENT: Filter by ingredient presence
     * - SEARCH: General text search across recipe names
     */
    public enum FilterType {
        CATEGORY,    // Filter by recipe category
        AREA,        // Filter by cuisine/area
        INGREDIENT,  // Filter by ingredient
        SEARCH       // General text search
    }

    // Filter properties
    private FilterType type;           // Type of filter to apply
    private String value;              // Primary filter value
    private List<String> values;       // Multiple filter values (for future use)

    /**
     * Constructor for creating a filter with a single value.
     * 
     * This constructor is used for most filtering scenarios where
     * a single category, area, ingredient, or search term is specified.
     * 
     * @param type The type of filter to apply
     * @param value The filter value (e.g., "Italian" for area, "Chicken" for ingredient)
     */
    public RecipeFilter(FilterType type, String value) {
        this.type = type;
        this.value = value;
        this.values = new ArrayList<>();
        if (value != null) {
            this.values.add(value);
        }
    }

    /**
     * Constructor for creating a filter with multiple values.
     * 
     * This constructor supports filtering with multiple criteria.
     * The first value in the list becomes the primary value.
     * 
     * @param type The type of filter to apply
     * @param values List of filter values
     */
    public RecipeFilter(FilterType type, List<String> values) {
        this.type = type;
        this.values = values != null ? values : new ArrayList<>();
        this.value = values != null && !values.isEmpty() ? values.get(0) : null;
    }

    // Getters
    /**
     * Gets the type of filter.
     * 
     * @return The filter type (CATEGORY, AREA, INGREDIENT, or SEARCH)
     */
    public FilterType getType() { return type; }
    
    /**
     * Gets the primary filter value.
     * 
     * @return The main filter value
     */
    public String getValue() { return value; }
    
    /**
     * Gets all filter values.
     * 
     * @return List of all filter values
     */
    public List<String> getValues() { return values; }

    // Helper methods for common filters
    
    /**
     * Creates a filter for recipes by category.
     * 
     * @param category The recipe category (e.g., "Breakfast", "Dinner", "Dessert")
     * @return RecipeFilter configured for category filtering
     */
    public static RecipeFilter byCategory(String category) {
        return new RecipeFilter(FilterType.CATEGORY, category);
    }

    /**
     * Creates a filter for recipes by cuisine/area.
     * 
     * @param area The cuisine area (e.g., "Italian", "Mexican", "American")
     * @return RecipeFilter configured for area filtering
     */
    public static RecipeFilter byArea(String area) {
        return new RecipeFilter(FilterType.AREA, area);
    }

    /**
     * Creates a filter for recipes by ingredient.
     * 
     * @param ingredient The ingredient to filter by
     * @return RecipeFilter configured for ingredient filtering
     */
    public static RecipeFilter byIngredient(String ingredient) {
        return new RecipeFilter(FilterType.INGREDIENT, ingredient);
    }

    /**
     * Creates a filter for general text search.
     * 
     * @param query The search query
     * @return RecipeFilter configured for text search
     */
    public static RecipeFilter bySearch(String query) {
        return new RecipeFilter(FilterType.SEARCH, query);
    }

    // Dietary restriction filters
    
    /**
     * Creates a filter for vegan recipes.
     * 
     * Note: This is a simplified implementation that filters by "Vegetarian"
     * category. A more sophisticated implementation would check individual
     * ingredients for animal products.
     * 
     * @return RecipeFilter configured for vegan recipes
     */
    public static RecipeFilter veganOnly() {
        return new RecipeFilter(FilterType.CATEGORY, "Vegetarian"); // Closest category
    }

    /**
     * Creates a filter for vegetarian recipes.
     * 
     * @return RecipeFilter configured for vegetarian recipes
     */
    public static RecipeFilter vegetarianOnly() {
        return new RecipeFilter(FilterType.CATEGORY, "Vegetarian");
    }

    /**
     * Creates a filter for gluten-free recipes.
     * 
     * Note: This is a simplified implementation that filters by "Miscellaneous"
     * category. A more sophisticated implementation would check individual
     * ingredients for gluten content.
     * 
     * @return RecipeFilter configured for gluten-free recipes
     */
    public static RecipeFilter glutenFreeOnly() {
        return new RecipeFilter(FilterType.CATEGORY, "Miscellaneous"); // We'll filter locally
    }
} 