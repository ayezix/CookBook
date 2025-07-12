package com.example.cookbook.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.cookbook.R;
import com.example.cookbook.api.model.AreaResponse;
import com.example.cookbook.api.model.CategoryResponse;
import com.example.cookbook.api.model.IngredientResponse;
import com.example.cookbook.databinding.DialogRecipeFilterBinding;
import com.example.cookbook.model.RecipeFilter;
import com.example.cookbook.util.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class RecipeFilterDialog extends DialogFragment {
    
    private FirebaseManager firebaseManager;
    private OnFilterAppliedListener listener;
    private DialogRecipeFilterBinding binding;
    
    // Data
    private List<CategoryResponse.Category> categories = new ArrayList<>();
    private List<AreaResponse.Area> areas = new ArrayList<>();
    private List<IngredientResponse.Ingredient> ingredients = new ArrayList<>();
    
    // Loading states
    private boolean categoriesLoaded = false;
    private boolean areasLoaded = false;
    private boolean ingredientsLoaded = false;
    
    public interface OnFilterAppliedListener {
        void onFilterApplied(RecipeFilter filter);
    }
    
    public static RecipeFilterDialog newInstance(
            ArrayList<CategoryResponse.Category> categories,
            ArrayList<AreaResponse.Area> areas,
            ArrayList<IngredientResponse.Ingredient> ingredients) {
        RecipeFilterDialog dialog = new RecipeFilterDialog();
        Bundle args = new Bundle();
        args.putSerializable("categories", categories);
        args.putSerializable("areas", areas);
        args.putSerializable("ingredients", ingredients);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try to set listener from parent fragment first (for getChildFragmentManager usage)
        if (getParentFragment() instanceof OnFilterAppliedListener) {
            listener = (OnFilterAppliedListener) getParentFragment();
        } else if (context instanceof OnFilterAppliedListener) {
            listener = (OnFilterAppliedListener) context;
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        firebaseManager = FirebaseManager.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = DialogRecipeFilterBinding.inflate(inflater);

        // Try to get filter options from arguments
        Bundle args = getArguments();
        if (args != null) {
            ArrayList<CategoryResponse.Category> argCategories = (ArrayList<CategoryResponse.Category>) args.getSerializable("categories");
            ArrayList<AreaResponse.Area> argAreas = (ArrayList<AreaResponse.Area>) args.getSerializable("areas");
            ArrayList<IngredientResponse.Ingredient> argIngredients = (ArrayList<IngredientResponse.Ingredient>) args.getSerializable("ingredients");
            if (argCategories != null && !argCategories.isEmpty()) {
                categories = argCategories;
                categoriesLoaded = true;
            }
            if (argAreas != null && !argAreas.isEmpty()) {
                areas = argAreas;
                areasLoaded = true;
            }
            if (argIngredients != null && !argIngredients.isEmpty()) {
                ingredients = argIngredients;
                ingredientsLoaded = true;
            }
        }

        setupListeners();
        if (categoriesLoaded && areasLoaded && ingredientsLoaded) {
            setupCategorySpinner();
            setupAreaSpinner();
            setupIngredientSpinner();
            checkCanEnableApplyButton();
        } else {
            loadFilterOptions();
        }

        builder.setView(binding.getRoot());
        return builder.create();
    }
    
    private void setupListeners() {
        binding.radioGroupFilterType.setOnCheckedChangeListener((group, checkedId) -> {
            // Hide all layouts first
            binding.layoutCategory.setVisibility(View.GONE);
            binding.layoutArea.setVisibility(View.GONE);
            binding.layoutIngredient.setVisibility(View.GONE);
            binding.layoutDietary.setVisibility(View.GONE);
            
            // Show the selected layout
            if (checkedId == R.id.radioCategory) {
                binding.layoutCategory.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radioArea) {
                binding.layoutArea.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radioIngredient) {
                binding.layoutIngredient.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radioDietary) {
                binding.layoutDietary.setVisibility(View.VISIBLE);
            }
            
            // Check if we can enable the apply button
            checkCanEnableApplyButton();
        });
        
        // Enable Apply button when a dietary restriction is selected
        binding.radioGroupDietary.setOnCheckedChangeListener((group, checkedId) -> {
            checkCanEnableApplyButton();
        });
        
        binding.btnApply.setOnClickListener(v -> applyFilter());
        binding.btnCancel.setOnClickListener(v -> dismiss());
        
        // Initially disable apply button until data is loaded
        binding.btnApply.setEnabled(false);
    }
    
    private void loadFilterOptions() {
        // Set a timeout to ensure the dialog loads even if API calls are slow
        new android.os.Handler().postDelayed(() -> {
            if (!categoriesLoaded) {
                android.util.Log.w("RecipeFilterDialog", "Categories loading timeout, using fallback");
                RecipeFilterDialog.this.categories = getDefaultCategories();
                categoriesLoaded = true;
                setupCategorySpinner();
                checkCanEnableApplyButton();
            }
            if (!areasLoaded) {
                android.util.Log.w("RecipeFilterDialog", "Areas loading timeout, using fallback");
                RecipeFilterDialog.this.areas = getDefaultAreas();
                areasLoaded = true;
                setupAreaSpinner();
                checkCanEnableApplyButton();
            }
            if (!ingredientsLoaded) {
                android.util.Log.w("RecipeFilterDialog", "Ingredients loading timeout, using fallback");
                RecipeFilterDialog.this.ingredients = getDefaultIngredients();
                ingredientsLoaded = true;
                setupIngredientSpinner();
                checkCanEnableApplyButton();
            }
        }, 5000); // 5 second timeout
        
        // Load categories
        firebaseManager.getCategories(new FirebaseManager.OnCategoriesLoadedListener() {
            @Override
            public void onCategoriesLoaded(List<CategoryResponse.Category> categories) {
                android.util.Log.d("RecipeFilterDialog", "Categories loaded: " + categories.size());
                RecipeFilterDialog.this.categories = categories;
                categoriesLoaded = true;
                setupCategorySpinner();
                checkCanEnableApplyButton();
            }
            
            @Override
            public void onError(String error) {
                android.util.Log.e("RecipeFilterDialog", "Failed to load categories: " + error);
                // Provide fallback categories
                RecipeFilterDialog.this.categories = getDefaultCategories();
                categoriesLoaded = true;
                setupCategorySpinner();
                checkCanEnableApplyButton();
            }
        });
        
        // Load areas
        firebaseManager.getAreas(new FirebaseManager.OnAreasLoadedListener() {
            @Override
            public void onAreasLoaded(List<AreaResponse.Area> areas) {
                android.util.Log.d("RecipeFilterDialog", "Areas loaded: " + areas.size());
                RecipeFilterDialog.this.areas = areas;
                areasLoaded = true;
                setupAreaSpinner();
                checkCanEnableApplyButton();
            }
            
            @Override
            public void onError(String error) {
                android.util.Log.e("RecipeFilterDialog", "Failed to load areas: " + error);
                // Provide fallback areas
                RecipeFilterDialog.this.areas = getDefaultAreas();
                areasLoaded = true;
                setupAreaSpinner();
                checkCanEnableApplyButton();
            }
        });
        
        // Load ingredients
        firebaseManager.getIngredients(new FirebaseManager.OnIngredientsLoadedListener() {
            @Override
            public void onIngredientsLoaded(List<IngredientResponse.Ingredient> ingredients) {
                android.util.Log.d("RecipeFilterDialog", "Ingredients loaded: " + ingredients.size());
                RecipeFilterDialog.this.ingredients = ingredients;
                ingredientsLoaded = true;
                setupIngredientSpinner();
                checkCanEnableApplyButton();
            }
            
            @Override
            public void onError(String error) {
                android.util.Log.e("RecipeFilterDialog", "Failed to load ingredients: " + error);
                // Provide fallback ingredients
                RecipeFilterDialog.this.ingredients = getDefaultIngredients();
                ingredientsLoaded = true;
                setupIngredientSpinner();
                checkCanEnableApplyButton();
            }
        });
    }
    
    private void checkCanEnableApplyButton() {
        if (!categoriesLoaded || !areasLoaded || !ingredientsLoaded) {
            binding.btnApply.setEnabled(false);
            return;
        }

        int checkedId = binding.radioGroupFilterType.getCheckedRadioButtonId();
        boolean enable = false;
        if (checkedId == R.id.radioCategory) {
            enable = binding.spinnerCategory.getCount() > 0 && binding.spinnerCategory.getSelectedItem() != null;
        } else if (checkedId == R.id.radioArea) {
            enable = binding.spinnerArea.getCount() > 0 && binding.spinnerArea.getSelectedItem() != null;
        } else if (checkedId == R.id.radioIngredient) {
            enable = binding.spinnerIngredient.getCount() > 0 && binding.spinnerIngredient.getSelectedItem() != null;
        } else if (checkedId == R.id.radioDietary) {
            enable = binding.radioGroupDietary.getCheckedRadioButtonId() != -1;
        }
        binding.btnApply.setEnabled(enable);
    }
    
    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        for (CategoryResponse.Category category : categories) {
            categoryNames.add(category.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);
        
        // Auto-select first item if available
        if (categoryNames.size() > 0) {
            binding.spinnerCategory.setSelection(0);
        }

        // Add listener to enable Apply button on selection
        binding.spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                checkCanEnableApplyButton();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                checkCanEnableApplyButton();
            }
        });
    }
    
    private void setupAreaSpinner() {
        List<String> areaNames = new ArrayList<>();
        for (AreaResponse.Area area : areas) {
            areaNames.add(area.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            areaNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerArea.setAdapter(adapter);
        
        // Auto-select first item if available
        if (areaNames.size() > 0) {
            binding.spinnerArea.setSelection(0);
        }

        // Add listener to enable Apply button on selection
        binding.spinnerArea.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                checkCanEnableApplyButton();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                checkCanEnableApplyButton();
            }
        });
    }
    
    private void setupIngredientSpinner() {
        List<String> ingredientNames = new ArrayList<>();
        for (IngredientResponse.Ingredient ingredient : ingredients) {
            ingredientNames.add(ingredient.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ingredientNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIngredient.setAdapter(adapter);
        
        // Auto-select first item if available
        if (ingredientNames.size() > 0) {
            binding.spinnerIngredient.setSelection(0);
        }

        // Add listener to enable Apply button on selection
        binding.spinnerIngredient.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                checkCanEnableApplyButton();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                checkCanEnableApplyButton();
            }
        });
    }
    
    private void applyFilter() {
        RecipeFilter filter = null;
        int checkedId = binding.radioGroupFilterType.getCheckedRadioButtonId();
        String logSelection = "";

        if (checkedId == R.id.radioCategory) {
            if (binding.spinnerCategory.getCount() > 0 && binding.spinnerCategory.getSelectedItem() != null) {
                String category = binding.spinnerCategory.getSelectedItem().toString();
                filter = RecipeFilter.byCategory(category);
                logSelection = "Category: " + category;
            }
        } else if (checkedId == R.id.radioArea) {
            if (binding.spinnerArea.getCount() > 0 && binding.spinnerArea.getSelectedItem() != null) {
                String area = binding.spinnerArea.getSelectedItem().toString();
                filter = RecipeFilter.byArea(area);
                logSelection = "Area: " + area;
            }
        } else if (checkedId == R.id.radioIngredient) {
            if (binding.spinnerIngredient.getCount() > 0 && binding.spinnerIngredient.getSelectedItem() != null) {
                String ingredient = binding.spinnerIngredient.getSelectedItem().toString();
                filter = RecipeFilter.byIngredient(ingredient);
                logSelection = "Ingredient: " + ingredient;
            }
        } else if (checkedId == R.id.radioDietary) {
            int dietaryCheckedId = binding.radioGroupDietary.getCheckedRadioButtonId();
            if (dietaryCheckedId == R.id.radioVegan) {
                filter = RecipeFilter.veganOnly();
                logSelection = "Dietary: Vegan";
            } else if (dietaryCheckedId == R.id.radioVegetarian) {
                filter = RecipeFilter.vegetarianOnly();
                logSelection = "Dietary: Vegetarian";
            } else if (dietaryCheckedId == R.id.radioGlutenFree) {
                filter = RecipeFilter.glutenFreeOnly();
                logSelection = "Dietary: Gluten Free";
            }
        }

        android.util.Log.d("RecipeFilterDialog", "User selection: " + logSelection);
        System.out.println("User selection: " + logSelection);
        // Log filter and listener values
        android.util.Log.d("RecipeFilterDialog", "filter=" + filter);
        android.util.Log.d("RecipeFilterDialog", "listener=" + listener);
        System.out.println("filter=" + filter);
        System.out.println("listener=" + listener);

        if (filter != null && listener != null) {
            listener.onFilterApplied(filter);
            dismiss();
        } else {
            Toast.makeText(requireContext(), "Please select a filter option", Toast.LENGTH_SHORT).show();
        }
    }
    
    // Fallback data methods
    private List<CategoryResponse.Category> getDefaultCategories() {
        List<CategoryResponse.Category> defaultCategories = new ArrayList<>();
        String[] categoryNames = {
            "Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", 
            "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian"
        };
        
        for (int i = 0; i < categoryNames.length; i++) {
            CategoryResponse.Category category = createCategory(String.valueOf(i + 1), categoryNames[i]);
            defaultCategories.add(category);
        }
        return defaultCategories;
    }
    
    private List<AreaResponse.Area> getDefaultAreas() {
        List<AreaResponse.Area> defaultAreas = new ArrayList<>();
        String[] areaNames = {
            "American", "British", "Chinese", "French", "Indian", 
            "Italian", "Japanese", "Mexican", "Spanish", "Thai"
        };
        
        for (String name : areaNames) {
            AreaResponse.Area area = createArea(name);
            defaultAreas.add(area);
        }
        return defaultAreas;
    }
    
    private List<IngredientResponse.Ingredient> getDefaultIngredients() {
        List<IngredientResponse.Ingredient> defaultIngredients = new ArrayList<>();
        String[] ingredientNames = {
            "Chicken", "Beef", "Pork", "Fish", "Rice", "Pasta", 
            "Tomato", "Onion", "Garlic", "Cheese", "Eggs", "Milk"
        };
        
        for (int i = 0; i < ingredientNames.length; i++) {
            IngredientResponse.Ingredient ingredient = createIngredient(String.valueOf(i + 1), ingredientNames[i]);
            defaultIngredients.add(ingredient);
        }
        return defaultIngredients;
    }
    
    // Helper methods to create objects using reflection
    private CategoryResponse.Category createCategory(String id, String name) {
        try {
            CategoryResponse.Category category = new CategoryResponse.Category();
            java.lang.reflect.Field idField = CategoryResponse.Category.class.getDeclaredField("id");
            java.lang.reflect.Field nameField = CategoryResponse.Category.class.getDeclaredField("name");
            idField.setAccessible(true);
            nameField.setAccessible(true);
            idField.set(category, id);
            nameField.set(category, name);
            return category;
        } catch (Exception e) {
            android.util.Log.e("RecipeFilterDialog", "Error creating category", e);
            return null;
        }
    }
    
    private AreaResponse.Area createArea(String name) {
        try {
            AreaResponse.Area area = new AreaResponse.Area();
            java.lang.reflect.Field nameField = AreaResponse.Area.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(area, name);
            return area;
        } catch (Exception e) {
            android.util.Log.e("RecipeFilterDialog", "Error creating area", e);
            return null;
        }
    }
    
    private IngredientResponse.Ingredient createIngredient(String id, String name) {
        try {
            IngredientResponse.Ingredient ingredient = new IngredientResponse.Ingredient();
            java.lang.reflect.Field idField = IngredientResponse.Ingredient.class.getDeclaredField("id");
            java.lang.reflect.Field nameField = IngredientResponse.Ingredient.class.getDeclaredField("name");
            idField.setAccessible(true);
            nameField.setAccessible(true);
            idField.set(ingredient, id);
            nameField.set(ingredient, name);
            return ingredient;
        } catch (Exception e) {
            android.util.Log.e("RecipeFilterDialog", "Error creating ingredient", e);
            return null;
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 