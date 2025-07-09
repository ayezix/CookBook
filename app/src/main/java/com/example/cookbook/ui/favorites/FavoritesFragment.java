package com.example.cookbook.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cookbook.databinding.FragmentFavoritesBinding;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.ui.home.RecipeAdapter;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private FirebaseManager firebaseManager;
    private RecipeAdapter recipeAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        firebaseManager = FirebaseManager.getInstance();
        setupRecyclerView();
        loadFavoriteRecipes();
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
            // Handle recipe click - open recipe details or handle delete
            // If you want to handle delete here, you can do so:
            // For example, if you have a delete button in the item, set its click listener here
            Intent intent = new Intent(requireContext(), com.example.cookbook.ui.recipe.RecipeDetailActivity.class);
            intent.putExtra("recipe", recipe);
            startActivityForResult(intent, 1001);
        });
        recipeAdapter.setOnRecipeUnfavoritedListener(recipe -> {
            List<Recipe> currentList = new ArrayList<>(recipeAdapter.getRecipes());
            currentList.remove(recipe);
            recipeAdapter.updateRecipes(currentList);
            updateEmptyState(currentList.isEmpty());
            Toast.makeText(requireContext(), "Recipe removed from favorites", Toast.LENGTH_SHORT).show();
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(recipeAdapter);
    }

    private void loadFavoriteRecipes() {
        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show();
        
        firebaseManager.getFavoriteRecipes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    HashSet<String> seenIds = new HashSet<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        if (recipe.getId() != null && !seenIds.contains(recipe.getId())) {
                            recipes.add(recipe);
                            seenIds.add(recipe.getId());
                        }
                    }
                    recipeAdapter.updateRecipes(recipes);
                    updateEmptyState(recipes.isEmpty());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load favorite recipes", Toast.LENGTH_SHORT).show();
                    updateEmptyState(true);
                });
    }

    private void updateEmptyState(boolean isEmpty) {
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