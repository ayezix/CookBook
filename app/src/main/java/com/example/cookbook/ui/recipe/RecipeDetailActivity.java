package com.example.cookbook.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.model.Ingredient;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.util.FirebaseManager;
import com.google.android.material.button.MaterialButton;

public class RecipeDetailActivity extends AppCompatActivity {
    private static final String TAG = "RecipeDetailActivity";
    private Recipe recipe;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        firebaseManager = FirebaseManager.getInstance();

        try {
            recipe = (Recipe) getIntent().getSerializableExtra("recipe");
            if (recipe == null) {
                Log.e(TAG, "Recipe object is null");
                Toast.makeText(this, "Error: Recipe not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            TextView tvTitle = findViewById(R.id.tvTitle);
            TextView tvCategory = findViewById(R.id.tvCategory);
            TextView tvIngredients = findViewById(R.id.tvIngredients);
            TextView tvInstructions = findViewById(R.id.tvInstructions);
            ImageView ivRecipe = findViewById(R.id.ivRecipe);
            MaterialButton btnDeleteRecipe = findViewById(R.id.btnDeleteRecipe);
            MaterialButton btnEditRecipe = findViewById(R.id.btnEditRecipe);

            tvTitle.setText(recipe.getTitle());
            tvCategory.setText(recipe.getCategory());
            tvInstructions.setText(recipe.getInstructions());

            StringBuilder ingredientsText = new StringBuilder();
            if (recipe.getIngredients() != null) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ingredientsText.append("- ")
                        .append(ingredient.getAmount()).append(" ")
                        .append(ingredient.getUnit()).append(" ")
                        .append(ingredient.getName()).append("\n");
                }
            }
            tvIngredients.setText(ingredientsText.toString());

            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(this)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_recipe)
                    .error(R.drawable.placeholder_recipe)
                    .into(ivRecipe);
            }

            // Show edit button only for user-created recipes
            Log.d(TAG, "Recipe imported from API: " + recipe.isImportedFromApi());
            Log.d(TAG, "Recipe userId: " + recipe.getUserId());
            Log.d(TAG, "Current user ID: " + firebaseManager.getCurrentUserId());
            
            // Show edit button only for user-created recipes (not imported from API)
            // Also check if the recipe belongs to the current user
            boolean isUserCreated = !recipe.isImportedFromApi() && 
                                   recipe.getUserId() != null && 
                                   recipe.getUserId().equals(firebaseManager.getCurrentUserId());
            
            if (isUserCreated) {
                Log.d(TAG, "Showing edit button - recipe is user-created");
                btnEditRecipe.setVisibility(View.VISIBLE);
                btnEditRecipe.setOnClickListener(v -> {
                    Intent intent = new Intent(this, AddRecipeActivity.class);
                    intent.putExtra("edit_recipe", true);
                    intent.putExtra("recipe", recipe);
                    startActivity(intent);
                });
            } else {
                Log.d(TAG, "Hiding edit button - recipe is from API or not owned by user");
                btnEditRecipe.setVisibility(View.GONE);
            }

            // Set up delete button click listener
            btnDeleteRecipe.setOnClickListener(v -> showDeleteConfirmationDialog());

        } catch (Exception e) {
            Log.e(TAG, "Error displaying recipe details", e);
            Toast.makeText(this, "Error displaying recipe details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to delete this recipe? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> deleteRecipe())
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteRecipe() {
        if (recipe != null && recipe.getId() != null) {
            firebaseManager.deleteRecipe(recipe.getId())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                });
        }
    }
} 