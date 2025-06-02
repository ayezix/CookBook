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
import java.util.Arrays;
import java.util.List;

import com.example.cookbook.ui.home.FilterTagAdapter.OnTagSelectedListener;

public class HomeFragment extends Fragment implements OnTagSelectedListener {
    private FragmentHomeBinding binding;
    private FirebaseManager firebaseManager;
    private RecipeAdapter recipeAdapter;
    private FilterTagAdapter filterTagAdapter;
    private List<Recipe> allRecipes = new ArrayList<>();
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
        setupFilterTags();
        setupRecyclerView();
        setupSearchView();
        setupClickListeners();
        loadRecipes();
    }

    private void setupFilterTags() {
        List<String> tags = Arrays.asList(
            "All",
            "Breakfast",
            "Lunch",
            "Dinner",
            "Desserts",
            "Vegetarian",
            "Vegan",
            "Favorites"
        );
        
        filterTagAdapter = new FilterTagAdapter(tags, this);
        binding.filterTagsRecyclerView.setAdapter(filterTagAdapter);
    }

    @Override
    public void onTagSelected(String tag, boolean isSelected) {
        filterRecipes();
    }

    private void filterRecipes() {
        if (allRecipes == null) return;

        List<String> selectedTags = filterTagAdapter.getSelectedTags();
        List<Recipe> filteredRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                recipe.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase());

            boolean matchesTags = selectedTags.isEmpty() || selectedTags.contains("All") ||
                selectedTags.stream().anyMatch(tag -> {
                    if (tag.equals("Favorites")) {
                        return recipe.isFavorite();
                    }
                    return recipe.getCategory().equals(tag);
                });

            if (matchesSearch && matchesTags) {
                filteredRecipes.add(recipe);
            }
        }

        recipeAdapter.updateRecipes(filteredRecipes);
        updateEmptyState(filteredRecipes.isEmpty());
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                filterRecipes();
                return true;
            }
        });
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
                filterRecipes();
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 