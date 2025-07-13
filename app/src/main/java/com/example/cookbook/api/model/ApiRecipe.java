package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiRecipe {
    @SerializedName("idMeal")
    private String id;

    @SerializedName("strMeal")
    private String title;

    @SerializedName("strMealThumb")
    private String imageUrl;

    @SerializedName("strInstructions")
    private String instructions;

    @SerializedName("strCategory")
    private String category;

    @SerializedName("strArea")
    private String area;

    // TheMealDB stores ingredients as strIngredient1, strIngredient2, etc.
    // We'll add these fields to properly extract ingredients
    @SerializedName("strIngredient1")
    private String ingredient1;
    @SerializedName("strIngredient2")
    private String ingredient2;
    @SerializedName("strIngredient3")
    private String ingredient3;
    @SerializedName("strIngredient4")
    private String ingredient4;
    @SerializedName("strIngredient5")
    private String ingredient5;
    @SerializedName("strIngredient6")
    private String ingredient6;
    @SerializedName("strIngredient7")
    private String ingredient7;
    @SerializedName("strIngredient8")
    private String ingredient8;
    @SerializedName("strIngredient9")
    private String ingredient9;
    @SerializedName("strIngredient10")
    private String ingredient10;
    @SerializedName("strIngredient11")
    private String ingredient11;
    @SerializedName("strIngredient12")
    private String ingredient12;
    @SerializedName("strIngredient13")
    private String ingredient13;
    @SerializedName("strIngredient14")
    private String ingredient14;
    @SerializedName("strIngredient15")
    private String ingredient15;
    @SerializedName("strIngredient16")
    private String ingredient16;
    @SerializedName("strIngredient17")
    private String ingredient17;
    @SerializedName("strIngredient18")
    private String ingredient18;
    @SerializedName("strIngredient19")
    private String ingredient19;
    @SerializedName("strIngredient20")
    private String ingredient20;

    // TheMealDB stores measures as strMeasure1, strMeasure2, etc.
    @SerializedName("strMeasure1")
    private String measure1;
    @SerializedName("strMeasure2")
    private String measure2;
    @SerializedName("strMeasure3")
    private String measure3;
    @SerializedName("strMeasure4")
    private String measure4;
    @SerializedName("strMeasure5")
    private String measure5;
    @SerializedName("strMeasure6")
    private String measure6;
    @SerializedName("strMeasure7")
    private String measure7;
    @SerializedName("strMeasure8")
    private String measure8;
    @SerializedName("strMeasure9")
    private String measure9;
    @SerializedName("strMeasure10")
    private String measure10;
    @SerializedName("strMeasure11")
    private String measure11;
    @SerializedName("strMeasure12")
    private String measure12;
    @SerializedName("strMeasure13")
    private String measure13;
    @SerializedName("strMeasure14")
    private String measure14;
    @SerializedName("strMeasure15")
    private String measure15;
    @SerializedName("strMeasure16")
    private String measure16;
    @SerializedName("strMeasure17")
    private String measure17;
    @SerializedName("strMeasure18")
    private String measure18;
    @SerializedName("strMeasure19")
    private String measure19;
    @SerializedName("strMeasure20")
    private String measure20;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getInstructions() { return instructions; }
    public String getCategory() { return category; }
    public String getArea() { return area; }

    // Ingredient getters
    public String getIngredient1() { return ingredient1; }
    public String getIngredient2() { return ingredient2; }
    public String getIngredient3() { return ingredient3; }
    public String getIngredient4() { return ingredient4; }
    public String getIngredient5() { return ingredient5; }
    public String getIngredient6() { return ingredient6; }
    public String getIngredient7() { return ingredient7; }
    public String getIngredient8() { return ingredient8; }
    public String getIngredient9() { return ingredient9; }
    public String getIngredient10() { return ingredient10; }
    public String getIngredient11() { return ingredient11; }
    public String getIngredient12() { return ingredient12; }
    public String getIngredient13() { return ingredient13; }
    public String getIngredient14() { return ingredient14; }
    public String getIngredient15() { return ingredient15; }
    public String getIngredient16() { return ingredient16; }
    public String getIngredient17() { return ingredient17; }
    public String getIngredient18() { return ingredient18; }
    public String getIngredient19() { return ingredient19; }
    public String getIngredient20() { return ingredient20; }

    // Measure getters
    public String getMeasure1() { return measure1; }
    public String getMeasure2() { return measure2; }
    public String getMeasure3() { return measure3; }
    public String getMeasure4() { return measure4; }
    public String getMeasure5() { return measure5; }
    public String getMeasure6() { return measure6; }
    public String getMeasure7() { return measure7; }
    public String getMeasure8() { return measure8; }
    public String getMeasure9() { return measure9; }
    public String getMeasure10() { return measure10; }
    public String getMeasure11() { return measure11; }
    public String getMeasure12() { return measure12; }
    public String getMeasure13() { return measure13; }
    public String getMeasure14() { return measure14; }
    public String getMeasure15() { return measure15; }
    public String getMeasure16() { return measure16; }
    public String getMeasure17() { return measure17; }
    public String getMeasure18() { return measure18; }
    public String getMeasure19() { return measure19; }
    public String getMeasure20() { return measure20; }


    public List<String> getDishTypes() { 
        // Return category as a list for compatibility
        return category != null ? List.of(category) : List.of("Other"); 
    }
} 