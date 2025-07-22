package com.example.cookbook.ui.fragments;

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
import com.example.cookbook.ui.fragments.RecipeAdapter;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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
    public void onResume() {
        super.onResume();
        loadFavoriteRecipes();
    }

    private void setupRecyclerView() {
        // Set up the adapter for the RecyclerView
        recipeAdapter = new RecipeAdapter(requireContext(), new ArrayList<>(), new RecipeAdapter.OnFavoriteChangedListener() {
            @Override
            public void onFavoriteChanged(Recipe recipe, boolean isFavorite) {
                loadFavoriteRecipes();
            }
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(recipeAdapter);
    }

    private void loadFavoriteRecipes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        firebaseManager.getFavoriteRecipes()
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onSuccess(com.google.firebase.firestore.QuerySnapshot queryDocumentSnapshots) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Recipe recipe = document.toObject(Recipe.class);
                            recipe.setId(document.getId());
                            recipes.add(recipe);
                        }
                        recipeAdapter.updateRecipes(recipes);
                        updateEmptyState(recipes.isEmpty());
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), "Failed to load favorite recipes", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        updateEmptyState(true);
                    }
                });
    }

    private void updateEmptyState(boolean isEmpty) {
        binding.emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 