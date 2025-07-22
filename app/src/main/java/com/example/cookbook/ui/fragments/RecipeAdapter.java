package com.example.cookbook.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ItemRecipeBinding;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.util.FirebaseManager;
import com.example.cookbook.ui.activities.AddRecipeActivity;
import com.example.cookbook.ui.activities.RecipeDetailActivity;

import java.util.List;

/**
 * RecipeAdapter - RecyclerView adapter for displaying recipes in lists.
 * 
 * This adapter manages the display of recipe items in RecyclerViews throughout
 * the application. It handles recipe data binding, click events, favorite
 * management, and image loading.
 * 
 * Key Features:
 * - Recipe data binding to view holders
 * - Click handling for recipe details
 * - Favorite status management
 * - Image loading with Glide
 * - Share functionality
 * - Support for both local and API recipes
 * 
 * Usage:
 * - HomeFragment recipe list
 * - FavoritesFragment recipe list
 * - Search results display
 * - Any recipe list in the application
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    
    /** List of recipes to display */
    private List<Recipe> recipes;
    
    /** Context for starting activities and accessing resources */
    private Context context;
    
    /** Listener for favorite status changes */
    private OnFavoriteChangedListener favoriteListener;

    /**
     * Interface for handling favorite status changes.
     * 
     * This interface allows the parent fragment to be notified when
     * a recipe's favorite status changes, so it can update its data
     * and UI accordingly.
     */
    public interface OnFavoriteChangedListener {
        /**
         * Called when a recipe's favorite status changes.
         * 
         * @param recipe The recipe whose favorite status changed
         * @param isFavorite The new favorite status
         */
        void onFavoriteChanged(Recipe recipe, boolean isFavorite);
    }

    /**
     * Constructor for RecipeAdapter.
     * 
     * @param context The context for starting activities and accessing resources
     * @param recipes Initial list of recipes to display
     * @param favoriteListener Listener for favorite status changes
     */
    public RecipeAdapter(Context context, List<Recipe> recipes, OnFavoriteChangedListener favoriteListener) {
        this.context = context;
        this.recipes = recipes;
        this.favoriteListener = favoriteListener;
    }

    /**
     * Creates a new ViewHolder for recipe items.
     * 
     * This method inflates the recipe item layout and creates a new
     * RecipeViewHolder to hold references to the view elements.
     * 
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new RecipeViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeViewHolder(binding);
    }

    /**
     * Binds recipe data to the ViewHolder.
     * 
     * This method populates the ViewHolder with recipe data and sets up
     * click listeners for various actions (view details, favorite, share).
     * 
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item in the data set
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, position);
    }

    /**
     * Returns the total number of recipes in the data set.
     * 
     * @return The number of recipes
     */
    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    /**
     * Updates the list of recipes and refreshes the adapter.
     * 
     * This method is called when new recipe data is available,
     * such as after a search or filter operation.
     * 
     * @param newRecipes The new list of recipes to display
     */
    public void updateRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for recipe items.
     * 
     * This class holds references to the view elements in each recipe item
     * and provides methods to bind data and handle interactions.
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeBinding binding;

        /**
         * Constructor for RecipeViewHolder.
         * 
         * @param binding The ViewBinding for the recipe item layout
         */
        RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Binds recipe data to the view elements and sets up click listeners.
         * 
         * This method populates all the view elements with recipe data and
         * configures click handlers for recipe details, favorites, and sharing.
         * 
         * @param recipe The recipe data to display
         * @param position The position of this item in the list
         */
        void bind(final Recipe recipe, final int position) {
            // Set recipe title
            binding.tvTitle.setText(recipe.getTitle());
            
            // Set recipe category
            binding.tvCategory.setText(recipe.getCategory());
            
            // Set ingredients count
            int ingredientsCount = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
            binding.tvIngredients.setText(ingredientsCount + " ingredients");

            // Load recipe image with Glide
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_recipe)
                    .error(R.drawable.placeholder_recipe)
                    .into(binding.ivRecipe);
            } else {
                binding.ivRecipe.setImageResource(R.drawable.placeholder_recipe);
            }

            // Set favorite button state
            updateFavoriteButton(recipe.isFavorite());

            // Set up click listeners
            setupClickListeners(recipe, position);
        }

        /**
         * Sets up click listeners for recipe interactions.
         * 
         * This method configures click handlers for:
         * - Recipe item click (opens recipe details)
         * - Favorite button click (toggles favorite status)
         * - Share button click (shares recipe)
         * 
         * @param recipe The recipe associated with this item
         * @param position The position of this item in the list
         */
        private void setupClickListeners(final Recipe recipe, final int position) {
            // Recipe item click - open recipe details
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecipeClick(recipe);
                }
            });

            // Favorite button click
            binding.ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavoriteClick(recipe, position);
                }
            });

            // Share button click
            binding.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShareOptions(recipe);
                }
            });
        }

        /**
         * Updates the favorite button appearance based on favorite status.
         * 
         * @param isFavorite Whether the recipe is currently favorited
         */
        private void updateFavoriteButton(boolean isFavorite) {
            if (isFavorite) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite_border);
            }
        }

        /**
         * Handles recipe item click by opening recipe details.
         * 
         * This method creates an Intent to launch RecipeDetailActivity
         * with the selected recipe data.
         * 
         * @param recipe The recipe to display details for
         */
        private void onRecipeClick(Recipe recipe) {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipe", recipe);
            context.startActivity(intent);
        }

        /**
         * Handles favorite button click by toggling favorite status.
         * 
         * This method updates the recipe's favorite status in Firebase
         * and notifies the parent fragment of the change.
         * 
         * @param recipe The recipe whose favorite status should be toggled
         * @param position The position of the recipe in the list
         */
        private void onFavoriteClick(Recipe recipe, int position) {
            boolean newFavoriteStatus = !recipe.isFavorite();
            
            // Update favorite status in Firebase
            FirebaseManager.getInstance().toggleFavoriteRecipe(recipe.getId(), newFavoriteStatus);
            
            // Update local recipe object
            recipe.setFavorite(newFavoriteStatus);
            
            // Update UI
            updateFavoriteButton(newFavoriteStatus);
            
            // Notify parent fragment
            if (favoriteListener != null) {
                favoriteListener.onFavoriteChanged(recipe, newFavoriteStatus);
            }
        }

        /**
         * Shows sharing options for the recipe.
         * 
         * This method creates a share intent with recipe information
         * and allows the user to share via various apps (WhatsApp, etc.).
         * 
         * @param recipe The recipe to share
         */
        private void showShareOptions(Recipe recipe) {
            String shareText = "Check out this recipe: " + recipe.getTitle() + "\n\n";
            if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
                shareText += "Instructions: " + recipe.getInstructions();
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Recipe: " + recipe.getTitle());

            try {
                context.startActivity(Intent.createChooser(shareIntent, "Share Recipe"));
            } catch (Exception e) {
                Toast.makeText(context, "Error sharing recipe", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 