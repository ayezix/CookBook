package com.example.cookbook.ui.home;

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

import com.example.cookbook.R;
import com.example.cookbook.databinding.FragmentHomeBinding;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.ui.recipe.AddRecipeActivity;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseManager firebaseManager;
    private RecipeAdapter recipeAdapter;

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
        setupClickListeners();
        loadRecipes();
    }

    private void setupRecyclerView() {
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // TODO: Handle recipe click - open recipe details
            Toast.makeText(requireContext(), "Recipe clicked: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(recipeAdapter);
    }

    private void setupClickListeners() {
        binding.fabAddRecipe.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddRecipeActivity.class)));
    }

    private void loadRecipes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        firebaseManager.getUserRecipes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                    recipeAdapter.updateRecipes(recipes);
                    updateEmptyState(recipes.isEmpty());
                    binding.progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    updateEmptyState(true);
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