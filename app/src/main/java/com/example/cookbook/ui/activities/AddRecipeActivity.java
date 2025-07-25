package com.example.cookbook.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ActivityAddRecipeBinding;
import com.example.cookbook.model.Ingredient;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.util.FirebaseManager;
import com.example.cookbook.util.ImgBBUploadManager;
import com.example.cookbook.ui.adapters.IngredientAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity implements IngredientAdapter.OnIngredientActionListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    
    private ActivityAddRecipeBinding binding;
    private FirebaseManager firebaseManager;
    private Uri selectedImageUri;
    private List<Ingredient> ingredients = new ArrayList<>();
    private Recipe editingRecipe = null;
    private boolean isEditMode = false;
    private IngredientAdapter ingredientAdapter;
    private int editingIngredientPosition = -1;

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
        setupRecyclerView();
        setupClickListeners();

        // Handle edit mode
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("edit_recipe", false);
        if (isEditMode && intent.hasExtra("recipe")) {
            editingRecipe = (Recipe) intent.getSerializableExtra("recipe");
            prefillFieldsForEdit(editingRecipe);
            getSupportActionBar().setTitle(R.string.title_edit_recipe);
        }
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipe_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        ingredientAdapter = new IngredientAdapter(ingredients, this);
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.rvIngredients.setAdapter(ingredientAdapter);
    }

    private void setupClickListeners() {
        binding.btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });
        binding.btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipe();
            }
        });
    }

    private void addIngredient() {
        String name = binding.etIngredientName.getText().toString().trim();
        String amount = binding.etIngredientAmount.getText().toString().trim();
        String unit = binding.etIngredientUnit.getText().toString().trim();

        if (name.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill in ingredient details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingIngredientPosition >= 0) {
            // Update existing ingredient
            ingredients.set(editingIngredientPosition, new Ingredient(name, amount, unit));
            editingIngredientPosition = -1;
            binding.btnAddIngredient.setText(R.string.btn_add_ingredient);
        } else {
            // Add new ingredient
            ingredients.add(new Ingredient(name, amount, unit));
        }
        
        ingredientAdapter.updateIngredients(ingredients);
        clearIngredientInputs();
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

    private void prefillFieldsForEdit(Recipe recipe) {
        binding.etTitle.setText(recipe.getTitle());
        // Set spinner selection
        if (!TextUtils.isEmpty(recipe.getCategory())) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) binding.spinnerCategory.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(recipe.getCategory())) {
                    binding.spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
        binding.etInstructions.setText(recipe.getInstructions());
        ingredients = new ArrayList<>(recipe.getIngredients());
        ingredientAdapter.updateIngredients(ingredients);
        if (!TextUtils.isEmpty(recipe.getImageUrl())) {
            binding.ivRecipe.setVisibility(View.VISIBLE);
            Glide.with(this)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.placeholder_recipe)
                .error(R.drawable.placeholder_recipe)
                .into(binding.ivRecipe);
        }
    }

    private void saveRecipe() {
        String title = binding.etTitle.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String instructions = binding.etInstructions.getText().toString().trim();

        if (title.isEmpty() || instructions.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        if (isEditMode && editingRecipe != null) {
            // Update existing recipe
            editingRecipe.setTitle(title);
            editingRecipe.setCategory(category);
            editingRecipe.setInstructions(instructions);
            editingRecipe.setIngredients(ingredients);
            if (selectedImageUri != null) {
                firebaseManager.uploadRecipeImage(selectedImageUri)
                    .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            editingRecipe.setImageUrl(imageUrl);
                            updateRecipeInFirestore(editingRecipe);
                        }
                    })
                    .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddRecipeActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                updateRecipeInFirestore(editingRecipe);
            }
        } else {
            // Add new recipe
            Recipe recipe = new Recipe();
            recipe.setTitle(title);
            recipe.setCategory(category);
            recipe.setInstructions(instructions);
            recipe.setIngredients(ingredients);
            Log.d("AddRecipeActivity", "Created recipe object: " + recipe.toString());
            Log.d("AddRecipeActivity", "Recipe details: title=" + recipe.getTitle() + 
                ", category=" + recipe.getCategory() + 
                ", ingredients count=" + (recipe.getIngredients() != null ? recipe.getIngredients().size() : 0) + 
                ", instructions length=" + (recipe.getInstructions() != null ? recipe.getInstructions().length() : 0));
            if (selectedImageUri != null) {
                // Upload image first
                firebaseManager.uploadRecipeImage(selectedImageUri)
                    .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            recipe.setImageUrl(imageUrl);
                            saveRecipeToFirestore(recipe);
                        }
                    })
                    .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddRecipeActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                saveRecipeToFirestore(recipe);
            }
        }
    }

    private void updateRecipeInFirestore(Recipe recipe) {
        firebaseManager.updateRecipe(recipe)
            .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddRecipeActivity.this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddRecipeActivity.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void saveRecipeToFirestore(Recipe recipe) {
        Log.d("AddRecipeActivity", "Saving recipe to Firestore: " + recipe.toString());
        // Ensure the recipe is marked as user-created (not imported from API)
        recipe.setImportedFromApi(false);
        firebaseManager.addRecipe(recipe)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<com.google.firebase.firestore.DocumentReference>() {
                    @Override
                    public void onSuccess(com.google.firebase.firestore.DocumentReference documentReference) {
                        Log.d("AddRecipeActivity", "Recipe saved successfully with ID: " + documentReference.getId());
                        Toast.makeText(AddRecipeActivity.this, R.string.msg_recipe_saved, Toast.LENGTH_SHORT).show();
                        // Set result to indicate recipe was added
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("AddRecipeActivity", "Failed to save recipe", e);
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddRecipeActivity.this, R.string.msg_recipe_save_failed, Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    public void onEditIngredient(Ingredient ingredient, int position) {
        // Pre-fill the input fields with the ingredient data
        binding.etIngredientName.setText(ingredient.getName());
        binding.etIngredientAmount.setText(ingredient.getAmount());
        binding.etIngredientUnit.setText(ingredient.getUnit());
        
        // Set the editing position and update button text
        editingIngredientPosition = position;
        binding.btnAddIngredient.setText("Update Ingredient");
        
        // Scroll to the input fields
        binding.getRoot().post(() -> {
            binding.etIngredientName.requestFocus();
        });
    }

    @Override
    public void onDeleteIngredient(int position) {
        ingredients.remove(position);
        ingredientAdapter.updateIngredients(ingredients);
        
        // If we were editing this ingredient, clear the editing state
        if (editingIngredientPosition == position) {
            editingIngredientPosition = -1;
            binding.btnAddIngredient.setText(R.string.btn_add_ingredient);
            clearIngredientInputs();
        } else if (editingIngredientPosition > position) {
            // Adjust the editing position if we deleted an ingredient before it
            editingIngredientPosition--;
        }
    }
} 