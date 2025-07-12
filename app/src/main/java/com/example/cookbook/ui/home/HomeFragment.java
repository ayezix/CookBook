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
        // Load user recipes from Firebase
        loadRecipes();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload recipes from Firebase to get any new additions
        loadRecipes();
    }

    private void setupRecyclerView() {
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // No-op: handled in RecipeAdapter now
        }, new RecipeAdapter.OnFavoriteChangedListener() {
            @Override
            public void onFavoriteChanged() {
                loadRecipes(); // Reload user recipes from Firebase
            }
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
        // Add this block to clear the list when the x button is pressed
        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Clear the search query and filter state
                currentSearchQuery = "";
                currentFilter = null;
                binding.btnClearFilter.setVisibility(View.GONE);
                android.util.Log.d("HomeFragment", "Search cleared, loading user recipes");
                // Show only the user's own recipes
                loadRecipes();
                return false; // Let the SearchView handle default behavior too
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
        android.util.Log.d("HomeFragment", "updateRecipeList called with " + recipes.size() + " recipes, currentSearchQuery: " + currentSearchQuery + ", currentFilter: " + (currentFilter != null ? currentFilter.getType() + "=" + currentFilter.getValue() : "null"));
        if (binding == null) {
            android.util.Log.w("HomeFragment", "Binding is null, returning");
            return;
        }
        List<Recipe> validRecipes = new ArrayList<>();
        
        // If we have a filter or search query, show the API results
        if ((currentFilter != null && currentFilter.getValue() != null && !currentFilter.getValue().isEmpty()) || 
            (currentSearchQuery != null && !currentSearchQuery.isEmpty())) {
            android.util.Log.d("HomeFragment", "Processing as API/search results");
            for (Recipe recipe : recipes) {
                if (recipe != null && recipe.getTitle() != null && !recipe.getTitle().trim().isEmpty()
                        && recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                    validRecipes.add(recipe);
                }
            }
        } else {
            // When no filter or search, show only user-created recipes (not imported from API, userId matches current user)
            android.util.Log.d("HomeFragment", "Processing as user recipes, allRecipes size: " + allRecipes.size());
            String currentUserId = firebaseManager.getCurrentUserId();
            for (Recipe userRecipe : allRecipes) {
                if (userRecipe != null
                        && userRecipe.getTitle() != null && !userRecipe.getTitle().trim().isEmpty()
                        && userRecipe.getIngredients() != null && !userRecipe.getIngredients().isEmpty()
                        && !userRecipe.isImportedFromApi()
                        && userRecipe.getUserId() != null
                        && userRecipe.getUserId().equals(currentUserId)) {
                    validRecipes.add(userRecipe);
                }
            }
        }
        android.util.Log.d("HomeFragment", "Valid recipes to display: " + validRecipes.size());
        if (recipeAdapter != null) {
            recipeAdapter.updateRecipes(validRecipes);
        }
        updateEmptyState(validRecipes.isEmpty());
    }

    private void setupClickListeners() {
        binding.fabAddRecipe.setOnClickListener(v -> 
            startActivityForResult(new Intent(requireContext(), AddRecipeActivity.class), 1001));
            
        // Remove Add Sample Recipes button and related click listener
        // Remove any Toasts or messages about sample recipes
        // Remove any code that calls addSampleRecipes or addSampleRecipesIfNeeded
        // Remove logic that adds sample recipes if the user's recipe list is empty
        // Remove references to btnAddSampleRecipes in setupClickListeners and layout
        // Remove any logic that mentions sample recipes in loadRecipes()

        // Add filter button click listener
        binding.btnFilter.setOnClickListener(v -> showFilterDialogWithOptions());
        
        // Add clear filter button click listener
        binding.btnClearFilter.setOnClickListener(v -> clearFilter());
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
                // Only allow these categories
                List<String> allowedCategories = java.util.Arrays.asList("Dessert", "Side", "Starter", "Breakfast", "Goat");
                ArrayList<CategoryResponse.Category> filtered = new ArrayList<>();
                for (CategoryResponse.Category cat : categories) {
                    if (allowedCategories.contains(cat.getName())) {
                        filtered.add(cat);
                    }
                }
                filterCategories = filtered;
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
        android.util.Log.d("HomeFragment", "loadRecipes() called");
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // First try to load user's recipes
        firebaseManager.getUserRecipes()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allRecipes.clear();
                android.util.Log.d("HomeFragment", "Loaded " + queryDocumentSnapshots.size() + " recipes from Firebase");
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = document.toObject(Recipe.class);
                    recipe.setId(document.getId());
                    allRecipes.add(recipe);
                    android.util.Log.d("HomeFragment", "Added recipe: " + recipe.getTitle() + 
                        ", importedFromApi: " + recipe.isImportedFromApi() + 
                        ", userId: " + recipe.getUserId() + 
                        ", isFavorite: " + recipe.isFavorite());
                    
                    // Log raw document data to debug field mapping
                    android.util.Log.d("HomeFragment", "Raw document data for " + recipe.getTitle() + ": " + document.getData());
                }
                
                // If user has no recipes, add sample recipes only if not already added
                // Remove any logic that adds sample recipes if the user's recipe list is empty
                // Remove references to btnAddSampleRecipes in setupClickListeners and layout
                // Remove any logic that mentions sample recipes in loadRecipes()
                android.util.Log.d("HomeFragment", "Calling updateRecipeList with " + allRecipes.size() + " recipes");
                updateRecipeList(allRecipes);
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
        android.util.Log.d("HomeFragment", "Filter applied: " + filter.getType() + " = " + filter.getValue());
        binding.progressBar.setVisibility(View.VISIBLE);
        currentFilter = filter;
        binding.btnClearFilter.setVisibility(View.VISIBLE);
        searchWithFilterOrQuery();
    }
    
    private void clearFilter() {
        android.util.Log.d("HomeFragment", "Clearing filter");
        currentFilter = null;
        binding.btnClearFilter.setVisibility(View.GONE);
        loadRecipes();
    }

    private void searchWithFilterOrQuery() {
        android.util.Log.d("HomeFragment", "searchWithFilterOrQuery called - filter: " + (currentFilter != null ? currentFilter.getType() + "=" + currentFilter.getValue() : "null") + ", query: " + currentSearchQuery);
        binding.progressBar.setVisibility(View.VISIBLE);
        // If both filter and query are empty, load all recipes
        if ((currentFilter == null || currentFilter.getValue() == null || currentFilter.getValue().isEmpty()) && (currentSearchQuery == null || currentSearchQuery.isEmpty())) {
            android.util.Log.d("HomeFragment", "Both filter and query are empty, loading all recipes");
            loadRecipes();
            return;
        }
        
        // Search both local recipes and API recipes
        List<Recipe> combinedResults = new ArrayList<>();
        final int[] completedSearches = {0};
        final int totalSearches = 2; // Local + API
        
        // Search local recipes first
        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            // Search by name
            firebaseManager.searchRecipesByName(currentSearchQuery)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    android.util.Log.d("HomeFragment", "Local search by name found " + queryDocumentSnapshots.size() + " recipes");
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        // Only add user-created recipes (not imported from API)
                        if (!recipe.isImportedFromApi()) {
                            combinedResults.add(recipe);
                        }
                    }
                    completedSearches[0]++;
                    if (completedSearches[0] == totalSearches) {
                        android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                        updateRecipeList(combinedResults);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("HomeFragment", "Local search by name failed", e);
                    completedSearches[0]++;
                    if (completedSearches[0] == totalSearches) {
                        android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                        updateRecipeList(combinedResults);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
        } else if (currentFilter != null && currentFilter.getValue() != null && !currentFilter.getValue().isEmpty()) {
            // Search by filter (category or ingredient only, since Recipe model doesn't have area field)
            if (currentFilter.getType() == RecipeFilter.FilterType.CATEGORY) {
                firebaseManager.searchRecipesByCategory(currentFilter.getValue())
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        android.util.Log.d("HomeFragment", "Local search by category found " + queryDocumentSnapshots.size() + " recipes");
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Recipe recipe = document.toObject(Recipe.class);
                            recipe.setId(document.getId());
                            // Only add user-created recipes (not imported from API)
                            if (!recipe.isImportedFromApi()) {
                                combinedResults.add(recipe);
                            }
                        }
                        completedSearches[0]++;
                        if (completedSearches[0] == totalSearches) {
                            android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                            updateRecipeList(combinedResults);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("HomeFragment", "Local search by category failed", e);
                        completedSearches[0]++;
                        if (completedSearches[0] == totalSearches) {
                            android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                            updateRecipeList(combinedResults);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
            } else if (currentFilter.getType() == RecipeFilter.FilterType.INGREDIENT) {
                firebaseManager.searchRecipesByIngredient(currentFilter.getValue())
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        android.util.Log.d("HomeFragment", "Local search by ingredient found " + queryDocumentSnapshots.size() + " recipes");
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Recipe recipe = document.toObject(Recipe.class);
                            recipe.setId(document.getId());
                            // Only add user-created recipes (not imported from API)
                            if (!recipe.isImportedFromApi()) {
                                combinedResults.add(recipe);
                            }
                        }
                        completedSearches[0]++;
                        if (completedSearches[0] == totalSearches) {
                            android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                            updateRecipeList(combinedResults);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("HomeFragment", "Local search by ingredient failed", e);
                        completedSearches[0]++;
                        if (completedSearches[0] == totalSearches) {
                            android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                            updateRecipeList(combinedResults);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
            } else {
                // For AREA filter, we can't search local recipes since Recipe model doesn't have area field
                android.util.Log.d("HomeFragment", "Skipping local search for AREA filter (not supported)");
                completedSearches[0]++;
            }
        } else {
            // No search query or filter, skip local search
            completedSearches[0]++;
        }
        
        // Search API recipes
        firebaseManager.searchOnlineRecipesByFilterOrQuery(currentFilter, currentSearchQuery, new FirebaseManager.OnRecipesLoadedListener() {
            @Override
            public void onRecipesLoaded(List<Recipe> apiRecipes) {
                android.util.Log.d("HomeFragment", "API search found " + apiRecipes.size() + " recipes");
                combinedResults.addAll(apiRecipes);
                completedSearches[0]++;
                if (completedSearches[0] == totalSearches) {
                    android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                    updateRecipeList(combinedResults);
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(String error) {
                android.util.Log.e("HomeFragment", "API search failed: " + error);
                completedSearches[0]++;
                if (completedSearches[0] == totalSearches) {
                    android.util.Log.d("HomeFragment", "All searches completed, total results: " + combinedResults.size());
                    updateRecipeList(combinedResults);
                    binding.progressBar.setVisibility(View.GONE);
                }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == android.app.Activity.RESULT_OK) {
            // Recipe was added successfully, reload recipes
            android.util.Log.d("HomeFragment", "Recipe added, reloading recipes");
            loadRecipes();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 