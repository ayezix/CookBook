package com.example.cookbook.model;

import java.util.List;
import java.util.ArrayList;

public class RecipeFilter {
    public enum FilterType {
        CATEGORY,
        AREA,
        INGREDIENT,
        SEARCH
    }

    private FilterType type;
    private String value;
    private List<String> values; // For multiple selections

    public RecipeFilter(FilterType type, String value) {
        this.type = type;
        this.value = value;
        this.values = new ArrayList<>();
        if (value != null) {
            this.values.add(value);
        }
    }

    public RecipeFilter(FilterType type, List<String> values) {
        this.type = type;
        this.values = values != null ? values : new ArrayList<>();
        this.value = values != null && !values.isEmpty() ? values.get(0) : null;
    }

    // Getters
    public FilterType getType() { return type; }
    public String getValue() { return value; }
    public List<String> getValues() { return values; }

    // Helper methods for common filters
    public static RecipeFilter byCategory(String category) {
        return new RecipeFilter(FilterType.CATEGORY, category);
    }

    public static RecipeFilter byArea(String area) {
        return new RecipeFilter(FilterType.AREA, area);
    }

    public static RecipeFilter byIngredient(String ingredient) {
        return new RecipeFilter(FilterType.INGREDIENT, ingredient);
    }

    public static RecipeFilter bySearch(String query) {
        return new RecipeFilter(FilterType.SEARCH, query);
    }

    // Vegan filter helper (we'll implement this with local logic)
    public static RecipeFilter veganOnly() {
        return new RecipeFilter(FilterType.CATEGORY, "Vegetarian"); // Closest category
    }

    // Vegetarian filter helper
    public static RecipeFilter vegetarianOnly() {
        return new RecipeFilter(FilterType.CATEGORY, "Vegetarian");
    }

    // Gluten-free filter helper
    public static RecipeFilter glutenFreeOnly() {
        return new RecipeFilter(FilterType.CATEGORY, "Miscellaneous"); // We'll filter locally
    }
} 