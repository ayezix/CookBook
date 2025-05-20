package com.example.cookbook.ui.recipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbook.R;
import com.example.cookbook.databinding.ActivityAddRecipeBinding;
import com.example.cookbook.model.Ingredient;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.util.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    
    private ActivityAddRecipeBinding binding;
    private FirebaseManager firebaseManager;
    private Uri selectedImageUri;
    private List<Ingredient> ingredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_add_recipe);

        firebaseManager = FirebaseManager.getInstance();
        setupSpinner();
        setupClickListeners();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipe_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnAddIngredient.setOnClickListener(v -> addIngredient());
        binding.btnUploadImage.setOnClickListener(v -> selectImage());
        binding.btnSave.setOnClickListener(v -> saveRecipe());
    }

    private void addIngredient() {
        String name = binding.etIngredientName.getText().toString().trim();
        String amount = binding.etIngredientAmount.getText().toString().trim();
        String unit = binding.etIngredientUnit.getText().toString().trim();

        if (name.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill in ingredient details", Toast.LENGTH_SHORT).show();
            return;
        }

        ingredients.add(new Ingredient(name, amount, unit));
        updateIngredientsDisplay();
        clearIngredientInputs();
    }

    private void updateIngredientsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            sb.append(String.format("%s %s %s\n", 
                    ingredient.getAmount(), 
                    ingredient.getUnit(), 
                    ingredient.getName()));
        }
        binding.tvIngredients.setText(sb.toString());
    }

    private void clearIngredientInputs() {
        binding.etIngredientName.setText("");
        binding.etIngredientAmount.setText("");
        binding.etIngredientUnit.setText("");
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Recipe Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.ivRecipe.setImageURI(selectedImageUri);
            binding.ivRecipe.setVisibility(View.VISIBLE);
        }
    }

    private void saveRecipe() {
        String title = binding.etTitle.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String instructions = binding.etInstructions.getText().toString().trim();

        if (title.isEmpty() || instructions.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        Recipe recipe = new Recipe(title, category, ingredients, instructions);

        if (selectedImageUri != null) {
            uploadImageAndSaveRecipe(recipe);
        } else {
            saveRecipeToFirestore(recipe);
        }
    }

    private void uploadImageAndSaveRecipe(Recipe recipe) {
        firebaseManager.uploadRecipeImage(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("AddRecipeActivity", "Image uploaded, fetching download URL...");
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d("AddRecipeActivity", "Download URL: " + uri);
                                recipe.setImageUrl(uri.toString());
                                saveRecipeToFirestore(recipe);
                            })
                            .addOnFailureListener(e -> {
                                binding.progressBar.setVisibility(View.GONE);
                                Log.e("AddRecipeActivity", "Failed to get download URL", e);
                                Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.e("AddRecipeActivity", "Image upload failed", e);
                    Toast.makeText(this, R.string.msg_image_upload_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void saveRecipeToFirestore(Recipe recipe) {
        firebaseManager.addRecipe(recipe)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, R.string.msg_recipe_saved, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.msg_recipe_save_failed, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 