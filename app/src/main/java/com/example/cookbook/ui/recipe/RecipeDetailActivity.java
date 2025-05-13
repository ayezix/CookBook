package com.example.cookbook.ui.recipe;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.model.Ingredient;
import com.example.cookbook.model.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {
    private static final String TAG = "RecipeDetailActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        try {
            Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
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
        } catch (Exception e) {
            Log.e(TAG, "Error displaying recipe details", e);
            Toast.makeText(this, "Error displaying recipe details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
} 