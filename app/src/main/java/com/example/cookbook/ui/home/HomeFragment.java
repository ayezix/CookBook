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
import com.example.cookbook.ui.recipe.AddRecipeActivity;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseManager firebaseManager;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> allRecipes = new ArrayList<>();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && data != null) {
            String deletedId = data.getStringExtra("deleted_recipe_id");
            if (deletedId != null) {
                removeRecipeById(deletedId);
            }
        }
    }

    private void setupRecyclerView() {
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            Intent intent = new Intent(requireContext(), com.example.cookbook.ui.recipe.RecipeDetailActivity.class);
            intent.putExtra("recipe", recipe);
            startActivityForResult(intent, 1002);
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(recipeAdapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    searchRecipes(newText);
                } else if (newText.isEmpty()) {
                    loadRecipes();
                }
                return true;
            }
        });
    }

    private void searchRecipes(String query) {
        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show();
        
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
            });
    }

    private void searchOnlineRecipes(String query) {
        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show();
        firebaseManager.searchOnlineRecipes(query, new FirebaseManager.OnRecipesLoadedListener() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                updateRecipeList(recipes);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecipeList(List<Recipe> recipes) {
        if (binding == null) {
            return;
        }
        // Remove duplicates by ID
        List<Recipe> uniqueRecipes = new ArrayList<>();
        HashSet<String> seenIds = new HashSet<>();
        for (Recipe recipe : recipes) {
            if (recipe.getId() != null && !seenIds.contains(recipe.getId())) {
                uniqueRecipes.add(recipe);
                seenIds.add(recipe.getId());
            }
        }
        if (recipeAdapter != null) {
            recipeAdapter.updateRecipes(uniqueRecipes);
        }
        updateEmptyState(uniqueRecipes.isEmpty());
    }

    private void setupClickListeners() {
        binding.fabAddRecipe.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddRecipeActivity.class)));
            
        binding.btnAddSampleRecipes.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show();
            firebaseManager.addSampleRecipes()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Sample recipes added successfully", Toast.LENGTH_SHORT).show();
                    loadRecipes();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to add sample recipes", Toast.LENGTH_SHORT).show();
                });
        });

        // Add click listener for the Create Recipe button in empty state
        binding.btnCreateRecipe.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddRecipeActivity.class)));
    }

    private void loadRecipes() {
        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show();
        firebaseManager.getUserRecipes()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allRecipes.clear();
                HashSet<String> seenIds = new HashSet<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = document.toObject(Recipe.class);
                    recipe.setId(document.getId());
                    if (recipe.getId() != null && !seenIds.contains(recipe.getId())) {
                        allRecipes.add(recipe);
                        seenIds.add(recipe.getId());
                    }
                }
                // If user has no recipes, add sample recipes only if not already added
                if (allRecipes.isEmpty()) {
                    firebaseManager.addSampleRecipesIfNeeded()
                        .addOnSuccessListener(aVoid ->
                            firebaseManager.getUserRecipes()
                                .addOnSuccessListener(newDocs -> {
                                    allRecipes.clear();
                                    HashSet<String> seenIds2 = new HashSet<>();
                                    for (QueryDocumentSnapshot doc : newDocs) {
                                        Recipe recipe = doc.toObject(Recipe.class);
                                        recipe.setId(doc.getId());
                                        if (recipe.getId() != null && !seenIds2.contains(recipe.getId())) {
                                            allRecipes.add(recipe);
                                            seenIds2.add(recipe.getId());
                                        }
                                    }
                                    recipeAdapter.updateRecipes(allRecipes);
                                    updateEmptyState(allRecipes.isEmpty());
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("HomeFragment", "Failed to load recipes after adding samples", e);
                                    Toast.makeText(requireContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
                                    updateEmptyState(true);
                                })
                        )
                        .addOnFailureListener(e -> {
                            android.util.Log.e("HomeFragment", "Failed to add sample recipes", e);
                            Toast.makeText(requireContext(), "Failed to add sample recipes", Toast.LENGTH_SHORT).show();
                            updateEmptyState(true);
                        });
                } else {
                    recipeAdapter.updateRecipes(allRecipes);
                    updateEmptyState(false);
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e("HomeFragment", "Failed to load recipes", e);
                Toast.makeText(requireContext(), "Failed to load recipes: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    // Add a public method to remove a recipe by ID
    public void removeRecipeById(String recipeId) {
        List<Recipe> currentList = new ArrayList<>(recipeAdapter.getRecipes());
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getId().equals(recipeId)) {
                currentList.remove(i);
                break;
            }
        }
        recipeAdapter.updateRecipes(currentList);
        updateEmptyState(currentList.isEmpty());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 