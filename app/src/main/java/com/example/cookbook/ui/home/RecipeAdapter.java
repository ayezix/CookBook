package com.example.cookbook.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ItemRecipeBinding;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.util.FirebaseManager;
import com.example.cookbook.ui.recipe.RecipeDetailActivity;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;
    private final FirebaseManager firebaseManager;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
        this.firebaseManager = FirebaseManager.getInstance();
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

        RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Recipe recipe) {
            binding.tvTitle.setText(recipe.getTitle());
            binding.tvCategory.setText(recipe.getCategory());
            binding.tvIngredients.setText(recipe.getIngredients().size() + " ingredients");
            
            // Load recipe image
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot())
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_recipe)
                    .error(R.drawable.placeholder_recipe)
                    .into(binding.ivRecipe);
            }

            // Set favorite state
            binding.ivFavorite.setImageResource(
                recipe.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
            );

            // Set click listeners
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), RecipeDetailActivity.class);
                intent.putExtra("recipe", recipe);
                binding.getRoot().getContext().startActivity(intent);
            });
            
            binding.ivFavorite.setOnClickListener(v -> {
                boolean newFavoriteState = !recipe.isFavorite();
                recipe.setFavorite(newFavoriteState);
                binding.ivFavorite.setImageResource(
                    newFavoriteState ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
                );
                
                if (recipe.isImportedFromApi() && newFavoriteState) {
                    // For API recipes, use favoriteApiRecipe only when favoriting
                    firebaseManager.favoriteApiRecipe(recipe)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(binding.getRoot().getContext(), 
                                "Recipe added to favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Revert the UI state if the operation failed
                            recipe.setFavorite(!newFavoriteState);
                            binding.ivFavorite.setImageResource(
                                !newFavoriteState ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
                            );
                            Toast.makeText(binding.getRoot().getContext(), 
                                "Failed to add recipe to favorites", Toast.LENGTH_SHORT).show();
                        });
                } else {
                    // For local recipes or when unfavoriting, use toggleFavoriteRecipe
                    firebaseManager.toggleFavoriteRecipe(recipe.getId(), newFavoriteState)
                        .addOnSuccessListener(aVoid -> {
                            if (!newFavoriteState) {
                                Toast.makeText(binding.getRoot().getContext(), 
                                    "Recipe removed from favorites", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Revert the UI state if the operation failed
                            recipe.setFavorite(!newFavoriteState);
                            binding.ivFavorite.setImageResource(
                                !newFavoriteState ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
                            );
                            Toast.makeText(binding.getRoot().getContext(), 
                                "Failed to update favorite status", Toast.LENGTH_SHORT).show();
                        });
                }
            });

            binding.ivShare.setOnClickListener(v -> showShareOptions(recipe));
        }

        private void showShareOptions(Recipe recipe) {
            String shareText = createShareText(recipe);
            
            // Create WhatsApp share intent
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            
            // Create general share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            
            // Create chooser intent
            Intent chooserIntent = Intent.createChooser(shareIntent, 
                binding.getRoot().getContext().getString(R.string.share_recipe_title));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { whatsappIntent });
            
            try {
                binding.getRoot().getContext().startActivity(chooserIntent);
            } catch (Exception e) {
                Toast.makeText(binding.getRoot().getContext(), 
                    R.string.share_recipe_no_app, Toast.LENGTH_SHORT).show();
            }
        }

        private String createShareText(Recipe recipe) {
            StringBuilder shareText = new StringBuilder();
            shareText.append(binding.getRoot().getContext().getString(R.string.share_recipe_header))
                    .append("\n\n");
            shareText.append(recipe.getTitle()).append("\n");
            shareText.append(binding.getRoot().getContext().getString(R.string.share_recipe_category, 
                    recipe.getCategory())).append("\n\n");
            
            shareText.append(binding.getRoot().getContext().getString(R.string.share_recipe_ingredients))
                    .append("\n");
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                shareText.append("- ").append(recipe.getIngredients().get(i).getName())
                    .append(" (").append(recipe.getIngredients().get(i).getAmount())
                    .append(" ").append(recipe.getIngredients().get(i).getUnit()).append(")\n");
            }
            
            shareText.append("\n")
                    .append(binding.getRoot().getContext().getString(R.string.share_recipe_instructions))
                    .append("\n")
                    .append(recipe.getInstructions());
            
            return shareText.toString();
        }
    }
} 