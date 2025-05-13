package com.example.cookbook.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cookbook.R;
import com.example.cookbook.databinding.FragmentFavoritesBinding;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.ui.home.RecipeAdapter;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private RecipeAdapter adapter;
    private final FirebaseManager firebaseManager = FirebaseManager.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadFavorites();
        setupClearFavoritesButton();
    }

    private void setupRecyclerView() {
        adapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Handle recipe click
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadFavorites() {
        binding.progressBar.setVisibility(View.VISIBLE);
        firebaseManager.getFavoriteRecipes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> {
                        Recipe recipe = doc.toObject(Recipe.class);
                        if (recipe != null) {
                            recipe.setId(doc.getId());
                            recipes.add(recipe);
                        }
                    });
                    updateUI(recipes);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error loading favorites", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                });
    }

    private void setupClearFavoritesButton() {
        binding.btnClearFavorites.setOnClickListener(v -> showClearFavoritesConfirmation());
    }

    private void showClearFavoritesConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.clear_favorites)
                .setMessage(R.string.clear_favorites_confirmation)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> clearAllFavorites())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void clearAllFavorites() {
        binding.progressBar.setVisibility(View.VISIBLE);
        firebaseManager.clearAllFavorites()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), R.string.favorites_cleared, Toast.LENGTH_SHORT).show();
                    loadFavorites();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error clearing favorites", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                });
    }

    private void updateUI(List<Recipe> recipes) {
        binding.progressBar.setVisibility(View.GONE);
        if (recipes.isEmpty()) {
            binding.tvEmptyFavorites.setVisibility(View.VISIBLE);
            binding.btnClearFavorites.setVisibility(View.GONE);
        } else {
            binding.tvEmptyFavorites.setVisibility(View.GONE);
            binding.btnClearFavorites.setVisibility(View.VISIBLE);
        }
        adapter.updateRecipes(recipes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 