package com.example.cookbook.api.model;

import com.google.gson.annotations.SerializedName;

public class ApiIngredient {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("amount")
    private double amount;

    @SerializedName("unit")
    private String unit;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public String getUnit() { return unit; }
} 