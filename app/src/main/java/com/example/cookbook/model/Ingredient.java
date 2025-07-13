package com.example.cookbook.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String amount;
    private String unit;
    private boolean isCustom;

    public Ingredient() {
        // Required empty constructor for Firebase
    }

    public Ingredient(String name, String amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.isCustom = false;
    }

    public Ingredient(String name, String amount, String unit, boolean isCustom) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.isCustom = isCustom;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }
} 