package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IngredientResponse {
    @SerializedName("meals")
    private List<Ingredient> ingredients;

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public static class Ingredient {
        @SerializedName("idIngredient")
        private String id;

        @SerializedName("strIngredient")
        private String name;

        @SerializedName("strDescription")
        private String description;

        @SerializedName("strType")
        private String type;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getType() { return type; }
    }
} 