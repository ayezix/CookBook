package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoryResponse {
    @SerializedName("categories")
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public static class Category implements java.io.Serializable {
        @SerializedName("idCategory")
        private String id;

        @SerializedName("strCategory")
        private String name;

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
    }
} 