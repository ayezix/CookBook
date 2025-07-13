package com.example.cookbook.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.cookbook.R;
import com.example.cookbook.api.model.AreaResponse;
import com.example.cookbook.api.model.CategoryResponse;
import com.example.cookbook.api.model.IngredientResponse;
import com.example.cookbook.util.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class SimpleFilterDialog extends DialogFragment {
    
    public enum FilterType {
        CUISINE,
        INGREDIENT,
        DIETARY
    }
    
    private FirebaseManager firebaseManager;
    private OnFilterSelectedListener listener;
    private FilterType filterType;
    private Spinner spinner;
    private Button btnApply;
    private List<Object> filterOptions = new ArrayList<>();
    private boolean dataLoaded = false;
    
    public interface OnFilterSelectedListener {
        void onFilterSelected(FilterType type, String value);
    }
    
    public static SimpleFilterDialog newInstance(FilterType type) {
        SimpleFilterDialog dialog = new SimpleFilterDialog();
        dialog.filterType = type;
        return dialog;
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterSelectedListener) {
            listener = (OnFilterSelectedListener) context;
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        firebaseManager = FirebaseManager.getInstance();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_simple_filter, null);
        
        setupViews(view);
        loadFilterOptions();
        
        builder.setView(view);
        return builder.create();
    }
    
    private void setupViews(View view) {
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        spinner = view.findViewById(R.id.spinner);
        btnApply = view.findViewById(R.id.btnApply);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        
        android.util.Log.d("SimpleFilterDialog", "Setting up views - btnApply found: " + (btnApply != null));
        
        // Set title based on filter type
        switch (filterType) {
            case CUISINE:
                tvTitle.setText("Select Cuisine");
                break;
            case INGREDIENT:
                tvTitle.setText("Select Main Ingredient");
                break;
            case DIETARY:
                tvTitle.setText("Select Dietary Restriction");
                break;
        }
        
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.d("SimpleFilterDialog", "Apply button clicked in setupViews");
                applyFilter();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        // Initially disable apply button
        btnApply.setEnabled(false);
        android.util.Log.d("SimpleFilterDialog", "Apply button initially disabled");
    }
    
    private void loadFilterOptions() {
        switch (filterType) {
            case CUISINE:
                loadCuisines();
                break;
            case INGREDIENT:
                loadIngredients();
                break;
            case DIETARY:
                loadDietaryOptions();
                break;
        }
    }
    
    private void loadCuisines() {
        firebaseManager.getAreas(new FirebaseManager.OnAreasLoadedListener() {
            @Override
            public void onAreasLoaded(List<AreaResponse.Area> areas) {
                android.util.Log.d("SimpleFilterDialog", "Loaded " + areas.size() + " areas from API");
                filterOptions.clear();
                for (AreaResponse.Area area : areas) {
                    String areaName = area.getName();
                    android.util.Log.d("SimpleFilterDialog", "Area: " + areaName);
                    filterOptions.add(areaName);
                }
                setupSpinner();
            }
            
            @Override
            public void onError(String error) {
                // Use fallback cuisines
                String[] fallbackCuisines = {
                    "American", "British", "Chinese", "French", "Indian", 
                    "Italian", "Japanese", "Mexican", "Spanish", "Thai"
                };
                filterOptions.clear();
                for (String cuisine : fallbackCuisines) {
                    filterOptions.add(cuisine);
                }
                setupSpinner();
            }
        });
    }
    
    private void loadIngredients() {
        firebaseManager.getIngredients(new FirebaseManager.OnIngredientsLoadedListener() {
            @Override
            public void onIngredientsLoaded(List<IngredientResponse.Ingredient> ingredients) {
                filterOptions.clear();
                for (IngredientResponse.Ingredient ingredient : ingredients) {
                    filterOptions.add(ingredient.getName());
                }
                setupSpinner();
            }
            
            @Override
            public void onError(String error) {
                // Use fallback ingredients
                String[] fallbackIngredients = {
                    "Chicken", "Beef", "Pork", "Fish", "Rice", "Pasta", 
                    "Tomato", "Onion", "Garlic", "Cheese", "Eggs", "Milk"
                };
                filterOptions.clear();
                for (String ingredient : fallbackIngredients) {
                    filterOptions.add(ingredient);
                }
                setupSpinner();
            }
        });
    }
    
    private void loadDietaryOptions() {
        // Dietary options are static
        String[] dietaryOptions = {
            "Vegan", "Vegetarian", "Gluten-Free", "Dairy-Free", "Low-Carb", "Keto"
        };
        filterOptions.clear();
        for (String option : dietaryOptions) {
            filterOptions.add(option);
        }
        setupSpinner();
    }
    
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            (List<String>) (List<?>) filterOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        // Auto-select first item
        if (filterOptions.size() > 0) {
            spinner.setSelection(0);
        }
        
        dataLoaded = true;
        enableApplyButton();
    }
    
    private void enableApplyButton() {
        if (btnApply != null) {
            boolean shouldEnable = dataLoaded && filterOptions.size() > 0;
            android.util.Log.d("SimpleFilterDialog", "Enabling apply button: dataLoaded=" + dataLoaded + ", filterOptions.size=" + filterOptions.size() + ", shouldEnable=" + shouldEnable);
            btnApply.setEnabled(shouldEnable);
        } else {
            android.util.Log.e("SimpleFilterDialog", "btnApply is null!");
        }
    }
    
    private void applyFilter() {
        android.util.Log.d("SimpleFilterDialog", "Apply button clicked!");
        if (spinner.getSelectedItem() != null) {
            String selectedValue = spinner.getSelectedItem().toString();
            android.util.Log.d("SimpleFilterDialog", "Selected filter: " + filterType + " = " + selectedValue);
            if (listener != null) {
                android.util.Log.d("SimpleFilterDialog", "Calling listener.onFilterSelected");
                listener.onFilterSelected(filterType, selectedValue);
            } else {
                android.util.Log.e("SimpleFilterDialog", "Listener is null!");
            }
            dismiss();
        } else {
            android.util.Log.e("SimpleFilterDialog", "No item selected in spinner");
            Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show();
        }
    }
} 