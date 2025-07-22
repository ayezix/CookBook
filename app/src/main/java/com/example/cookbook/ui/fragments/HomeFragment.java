package com.example.cookbook.ui.fragments;

import android.app.Activity;
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
import com.example.cookbook.ui.activities.AddRecipeActivity;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.cookbook.api.model.CategoryResponse;
import com.example.cookbook.api.model.AreaResponse;
import com.example.cookbook.api.model.IngredientResponse;
import com.example.cookbook.ui.fragments.RecipeAdapter;

/**
 * HomeFragment - Main home screen for the CookBook application.
 * 
 * This fragment serves as the primary interface for recipe management and discovery.
 * It displays the user's recipes, provides search functionality, and integrates
 * with external APIs for recipe discovery.
 * 
 * Key Features:
 * - Display user's personal recipes
 * - Real-time search functionality (local and online)
 * - Advanced filtering by category, area, and ingredients
 * - Integration with TheMealDB API for recipe discovery
 * - Recipe creation via floating action button
 * - Favorite management
 * 
 * Data Flow:
 * 1. Loads user's recipes from Firebase on startup
 * 2. Handles search queries (local first, then API)
 * 3. Applies filters from RecipeFilterDialog
 * 4. Updates UI based on search/filter results
 * 5. Manages recipe list display and empty states
 */
public class HomeFragment extends Fragment implements RecipeFilterDialog.OnFilterAppliedListener {
    
    /** View binding for the fragment layout */
    private FragmentHomeBinding binding;
    
    /** Firebase manager for data operations */
    private FirebaseManager firebaseManager;
    
    /** Adapter for displaying recipes in RecyclerView */
    private RecipeAdapter recipeAdapter;
    
    /** Complete list of user's recipes (cached) */
    private List<Recipe> allRecipes = new ArrayList<>();
    
    // Search and filter state
    /** Current active filter (null if no filter applied) */
    private RecipeFilter currentFilter = null;
    
    /** Current search query (empty string if no search) */
    private String currentSearchQuery = "";

    // Filter options cache
    /** Available categories for filtering (loaded from API) */
    private ArrayList<CategoryResponse.Category> filterCategories = new ArrayList<>();
    
    /** Available areas/cuisines for filtering (loaded from API) */
    private ArrayList<AreaResponse.Area> filterAreas = new ArrayList<>();
    
    /** Available ingredients for filtering (loaded from API) */
    private ArrayList<IngredientResponse.Ingredient> filterIngredients = new ArrayList<>();
    
    /** Whether filter options have been loaded from API */
    private boolean filterOptionsLoaded = false;

    // 1. Add ActivityResultLauncher field
    private androidx.activity.result.ActivityResultLauncher<Intent> addRecipeLauncher;

    /**
     * Creates the fragment's view hierarchy.
     * 
     * This method inflates the fragment_home layout using ViewBinding
     * and returns the root view.
     * 
     * @param inflater LayoutInflater for inflating the view
     * @param container Parent view group
     * @param savedInstanceState Saved state bundle
     * @return The inflated view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Sets up the fragment after view creation.
     * 
     * This method initializes all components:
     * - Gets FirebaseManager instance
     * - Sets up RecyclerView with adapter
     * - Configures SearchView functionality
     * - Sets up click listeners
     * - Loads initial recipe data
     * 
     * @param view The fragment's view
     * @param savedInstanceState Saved state bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        firebaseManager = FirebaseManager.getInstance();
        // Register the ActivityResultLauncher
        addRecipeLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Recipe was added successfully, reload recipes
                    android.util.Log.d("HomeFragment", "Recipe added, reloading recipes");
                    loadRecipes();
                }
            }
        );
        setupRecyclerView();
        setupSearchView();
        setupClickListeners();
        // Load user recipes from Firebase
        loadRecipes();
    }

    /**
     * Called when the fragment becomes visible to the user.
     * 
     * This method reloads recipes from Firebase to ensure the display
     * is up-to-date with any changes made in other parts of the app.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Reload recipes from Firebase to get any new additions
        loadRecipes();
    }

    /**
     * Sets up the RecyclerView for displaying recipes.
     * 
     * This method:
     * - Creates RecipeAdapter with empty list and favorite change listener
     * - Sets LinearLayoutManager for vertical scrolling
     * - Assigns adapter to RecyclerView
     */
    private void setupRecyclerView() {
        // Set up the adapter for the RecyclerView
        recipeAdapter = new RecipeAdapter(requireContext(), new ArrayList<>(), new RecipeAdapter.OnFavoriteChangedListener() {
            @Override
            public void onFavoriteChanged(Recipe recipe, boolean isFavorite) {
                // Update the recipe in our local list
                for (int i = 0; i < allRecipes.size(); i++) {
                    if (allRecipes.get(i).getId().equals(recipe.getId())) {
                        allRecipes.get(i).setFavorite(isFavorite);
                        break;
                    }
                }
                // Update the display
                updateRecipeList(allRecipes);
            }
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(recipeAdapter);
    }

    /**
     * Sets up the SearchView functionality.
     * 
     * This method configures:
     * - Query submission handling
     * - Real-time search (after 2 characters)
     * - Search clearing functionality
     * - Integration with filter system
     */
    private void setupSearchView() {
        // Set up the search view listeners
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

    /**
     * Updates the displayed recipe list based on current state.
     * 
     * This method filters and validates recipes based on:
     * - Current search query
     * - Active filter
     * - Recipe validity (title, ingredients)
     * - User ownership (for non-search results)
     * 
     * @param recipes List of recipes to process and display
     */
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

    /**
     * Sets up click listeners for UI elements.
     * 
     * This method configures:
     * - Floating action button for adding recipes
     * - Filter button for opening filter dialog
     * - Clear filter button for removing active filters
     */
    private void setupClickListeners() {
        // Set up the floating action button to add a recipe
        binding.fabAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddRecipeActivity.class);
                addRecipeLauncher.launch(intent);
            }
        });
        // Set up the filter button
        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialogWithOptions();
            }
        });
        // Set up the clear filter button
        binding.btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
            }
        });
    }

    /**
     * Shows the filter dialog with loaded options.
     * 
     * This method:
     * - Shows progress indicator
     * - Loads categories, areas, and ingredients from API
     * - Displays RecipeFilterDialog when all options are loaded
     * - Handles loading errors gracefully
     */
    private void showFilterDialogWithOptions() {
        binding.progressBar.setVisibility(View.VISIBLE);
        filterOptionsLoaded = false;
        filterCategories.clear();
        filterAreas.clear();
        filterIngredients.clear();
        
        // Load categories from API
        firebaseManager.getCategories(new FirebaseManager.OnCategoriesLoadedListener() {
            @Override
            public void onCategoriesLoaded(List<CategoryResponse.Category> categories) {
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
        firebaseManager.getUserRecipes()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allRecipes.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = document.toObject(Recipe.class);
                    recipe.setId(document.getId());
                    allRecipes.add(recipe);
                }
                updateRecipeList(allRecipes);
                binding.progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 