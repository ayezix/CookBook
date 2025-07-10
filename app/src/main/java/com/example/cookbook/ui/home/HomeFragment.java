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

public class HomeFragment extends Fragment implements RecipeFilterDialog.OnFilterAppliedListener {
    private FragmentHomeBinding binding;
    private FirebaseManager firebaseManager;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> allRecipes = new ArrayList<>();
    // Store current filter and search query
    private RecipeFilter currentFilter = null;
    private String currentSearchQuery = "";

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
        loadRecipes();
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
        if (recipeAdapter != null) {
            recipeAdapter.updateRecipes(recipes);
        }
        updateEmptyState(recipes.isEmpty());
    }

    private void setupClickListeners() {
        binding.fabAddRecipe.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddRecipeActivity.class)));
            
        binding.btnAddSampleRecipes.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            firebaseManager.addSampleRecipes()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Sample recipes added successfully", Toast.LENGTH_SHORT).show();
                    loadRecipes();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to add sample recipes", Toast.LENGTH_SHORT).show();
                });
        });

        // Add click listener for the Create Recipe button in empty state
        binding.btnCreateRecipe.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddRecipeActivity.class)));

        // Add filter button click listener
        binding.btnFilter.setOnClickListener(v -> showFilterDialog());
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
                if (allRecipes.isEmpty()) {
                    firebaseManager.addSampleRecipesIfNeeded()
                        .addOnSuccessListener(aVoid -> {
                            // Reload recipes after adding samples (if needed)
                            firebaseManager.getUserRecipes()
                                .addOnSuccessListener(newDocs -> {
                                    allRecipes.clear();
                                    for (QueryDocumentSnapshot doc : newDocs) {
                                        Recipe recipe = doc.toObject(Recipe.class);
                                        recipe.setId(doc.getId());
                                        allRecipes.add(recipe);
                                    }
                                    recipeAdapter.updateRecipes(allRecipes);
                                    updateEmptyState(allRecipes.isEmpty());
                                    binding.progressBar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("HomeFragment", "Failed to load recipes after adding samples", e);
                                    Toast.makeText(requireContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                    updateEmptyState(true);
                                });
                        })
                        .addOnFailureListener(e -> {
                            android.util.Log.e("HomeFragment", "Failed to add sample recipes", e);
                            Toast.makeText(requireContext(), "Failed to add sample recipes", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                            updateEmptyState(true);
                        });
                } else {
                    recipeAdapter.updateRecipes(allRecipes);
                    updateEmptyState(false);
                    binding.progressBar.setVisibility(View.GONE);
                }
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

    private void showFilterDialog() {
        RecipeFilterDialog dialog = RecipeFilterDialog.newInstance();
        dialog.show(getChildFragmentManager(), "filter_dialog");
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