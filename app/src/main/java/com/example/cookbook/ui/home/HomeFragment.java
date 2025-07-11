package com.example.cookbook.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cookbook.R;
import com.example.cookbook.databinding.FragmentHomeBinding;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.model.RecipeFilter;
import com.example.cookbook.ui.dialog.RecipeFilterDialog;
import com.example.cookbook.ui.recipe.AddRecipeActivity;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.cookbook.api.model.CategoryResponse;
import com.example.cookbook.api.model.AreaResponse;
import com.example.cookbook.api.model.IngredientResponse;

public class HomeFragment extends Fragment implements RecipeFilterDialog.OnFilterAppliedListener {
    private FragmentHomeBinding binding;
    private FirebaseManager firebaseManager;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> allRecipes = new ArrayList<>();
    // Store current filter and search query
    private RecipeFilter currentFilter = null;
    private String currentSearchQuery = "";

    // Add fields to store filter options
    private ArrayList<CategoryResponse.Category> filterCategories = new ArrayList<>();
    private ArrayList<AreaResponse.Area> filterAreas = new ArrayList<>();
    private ArrayList<IngredientResponse.Ingredient> filterIngredients = new ArrayList<>();
    private boolean filterOptionsLoaded = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        firebaseManager = FirebaseManager.getInstance();
        setupRecyclerView();
        setupSearchView();
        setupClickListeners();
        // Do not load recipes by default
        updateRecipeList(new ArrayList<>()); // Show empty state
    }

    @Override
    public void onResume() {
        super.onResume();
        // Do not load recipes by default
        updateRecipeList(new ArrayList<>()); // Show empty state
    }

    private void setupRecyclerView() {
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // No-op: handled in RecipeAdapter now
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(recipeAdapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                searchWithFilterOrQuery();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                if (newText.length() > 2) {
                    searchWithFilterOrQuery();
                } else if (newText.isEmpty()) {
                    currentSearchQuery = "";
                    searchWithFilterOrQuery();
                }
                return true;
            }
        });
    }

    private void searchRecipes(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // First search local recipes
        firebaseManager.searchRecipesByName(query)
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Recipe> localRecipes = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = document.toObject(Recipe.class);
                    recipe.setId(document.getId());
                    localRecipes.add(recipe);
                }
                
                // If no local recipes found, search online
                if (localRecipes.isEmpty()) {
                    searchOnlineRecipes(query);
                } else {
                    updateRecipeList(localRecipes);
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e("HomeFragment", "Failed to search local recipes", e);
                Toast.makeText(requireContext(), "Failed to search local recipes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
            });
    }

    private void searchOnlineRecipes(String query) {
        firebaseManager.searchOnlineRecipes(query, new FirebaseManager.OnRecipesLoadedListener() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                updateRecipeList(recipes);
            }

            @Override
            public void onError(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecipeList(List<Recipe> recipes) {
        if (binding == null) {
            return;
        }
        // Filter out null or invalid recipes
        List<Recipe> validRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe != null && recipe.getTitle() != null && !recipe.getTitle().trim().isEmpty()
                    && recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                validRecipes.add(recipe);
            }
        }
        if (recipeAdapter != null) {
            recipeAdapter.updateRecipes(validRecipes);
        }
        updateEmptyState(validRecipes.isEmpty());
    }

    private void setupClickListeners() {
        binding.fabAddRecipe.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddRecipeActivity.class)));
            
        // Remove Add Sample Recipes button and related click listener
        // Remove any Toasts or messages about sample recipes
        // Remove any code that calls addSampleRecipes or addSampleRecipesIfNeeded
        // Remove logic that adds sample recipes if the user's recipe list is empty
        // Remove references to btnAddSampleRecipes in setupClickListeners and layout
        // Remove any logic that mentions sample recipes in loadRecipes()

        // Add filter button click listener
        binding.btnFilter.setOnClickListener(v -> showFilterDialogWithOptions());
    }

    // New method to load filter options and show dialog
    private void showFilterDialogWithOptions() {
        binding.progressBar.setVisibility(View.VISIBLE);
        filterOptionsLoaded = false;
        filterCategories.clear();
        filterAreas.clear();
        filterIngredients.clear();

        firebaseManager.getCategories(new FirebaseManager.OnCategoriesLoadedListener() {
            @Override
            public void onCategoriesLoaded(List<CategoryResponse.Category> categories) {
                filterCategories = new ArrayList<>(categories);
                checkAndShowDialog();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
        firebaseManager.getAreas(new FirebaseManager.OnAreasLoadedListener() {
            @Override
            public void onAreasLoaded(List<AreaResponse.Area> areas) {
                filterAreas = new ArrayList<>(areas);
                checkAndShowDialog();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Failed to load areas", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
        firebaseManager.getIngredients(new FirebaseManager.OnIngredientsLoadedListener() {
            @Override
            public void onIngredientsLoaded(List<IngredientResponse.Ingredient> ingredients) {
                filterIngredients = new ArrayList<>(ingredients);
                checkAndShowDialog();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Failed to load ingredients", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Helper to show dialog only when all options are loaded
    private void checkAndShowDialog() {
        if (!filterCategories.isEmpty() && !filterAreas.isEmpty() && !filterIngredients.isEmpty() && !filterOptionsLoaded) {
            filterOptionsLoaded = true;
            binding.progressBar.setVisibility(View.GONE);
            RecipeFilterDialog dialog = RecipeFilterDialog.newInstance(filterCategories, filterAreas, filterIngredients);
            dialog.show(getChildFragmentManager(), "filter_dialog");
        }
    }

    private void loadRecipes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // First try to load user's recipes
        firebaseManager.getUserRecipes()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allRecipes.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = document.toObject(Recipe.class);
                    recipe.setId(document.getId());
                    allRecipes.add(recipe);
                }
                
                // If user has no recipes, add sample recipes only if not already added
                // Remove any logic that adds sample recipes if the user's recipe list is empty
                // Remove references to btnAddSampleRecipes in setupClickListeners and layout
                // Remove any logic that mentions sample recipes in loadRecipes()
                recipeAdapter.updateRecipes(allRecipes);
                updateEmptyState(allRecipes.isEmpty());
                binding.progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                android.util.Log.e("HomeFragment", "Failed to load recipes", e);
                Toast.makeText(requireContext(), "Failed to load recipes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
                updateEmptyState(true);
            });
    }

    private void updateEmptyState(boolean isEmpty) {
        if (binding == null || binding.emptyStateLayout == null) {
            return;
        }
        binding.emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onFilterApplied(RecipeFilter filter) {
        binding.progressBar.setVisibility(View.VISIBLE);
        currentFilter = filter;
        searchWithFilterOrQuery();
    }

    private void searchWithFilterOrQuery() {
        binding.progressBar.setVisibility(View.VISIBLE);
        // If both filter and query are empty, load all recipes
        if ((currentFilter == null || currentFilter.getValue() == null || currentFilter.getValue().isEmpty()) && (currentSearchQuery == null || currentSearchQuery.isEmpty())) {
            loadRecipes();
            return;
        }
        // Use the new FirebaseManager method
        firebaseManager.searchOnlineRecipesByFilterOrQuery(currentFilter, currentSearchQuery, new FirebaseManager.OnRecipesLoadedListener() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                updateRecipeList(recipes);
                binding.progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onError(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Search failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFilterTypeName(RecipeFilter.FilterType type) {
        switch (type) {
            case CATEGORY: return "Category";
            case AREA: return "Cuisine";
            case INGREDIENT: return "Ingredient";
            case SEARCH: return "Search";
            default: return "Filter";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 