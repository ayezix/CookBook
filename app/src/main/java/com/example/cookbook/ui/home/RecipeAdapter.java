package com.example.cookbook.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ItemRecipeBinding;
import com.example.cookbook.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void updateRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeBinding binding;

        public RecipeViewHolder(@NonNull ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Recipe recipe) {
            binding.tvTitle.setText(recipe.getTitle());
            binding.tvCategory.setText(recipe.getCategory());
            binding.tvIngredients.setText(String.format("%d ingredients", recipe.getIngredients().size()));
            
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.placeholder_recipe)
                        .error(R.drawable.placeholder_recipe)
                        .centerCrop()
                        .into(binding.ivRecipe);
            } else {
                binding.ivRecipe.setImageResource(R.drawable.placeholder_recipe);
            }

            binding.ivFavorite.setImageResource(
                    recipe.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

            binding.getRoot().setOnClickListener(v -> listener.onRecipeClick(recipe));
        }
    }
} 