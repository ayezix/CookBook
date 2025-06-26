package com.example.cookbook.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.R;
import com.example.cookbook.databinding.ItemIngredientBinding;
import com.example.cookbook.model.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<Ingredient> ingredients;
    private final OnIngredientActionListener listener;

    public interface OnIngredientActionListener {
        void onEditIngredient(Ingredient ingredient, int position);
        void onDeleteIngredient(int position);
    }

    public IngredientAdapter(List<Ingredient> ingredients, OnIngredientActionListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIngredientBinding binding = ItemIngredientBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new IngredientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position), position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateIngredients(List<Ingredient> newIngredients) {
        this.ingredients = newIngredients;
        notifyDataSetChanged();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final ItemIngredientBinding binding;

        IngredientViewHolder(ItemIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Ingredient ingredient, int position) {
            binding.tvIngredientName.setText(ingredient.getName());
            binding.tvIngredientAmount.setText(ingredient.getAmount() + " " + ingredient.getUnit());

            binding.btnEditIngredient.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditIngredient(ingredient, position);
                }
            });

            binding.btnDeleteIngredient.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteIngredient(position);
                }
            });
        }
    }
} 