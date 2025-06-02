package com.example.cookbook.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.R;
import com.example.cookbook.databinding.ItemFilterTagBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class FilterTagAdapter extends RecyclerView.Adapter<FilterTagAdapter.FilterTagViewHolder> {
    private final List<String> tags;
    private final List<String> selectedTags;
    private final OnTagSelectedListener listener;

    public interface OnTagSelectedListener {
        void onTagSelected(String tag, boolean isSelected);
    }

    public FilterTagAdapter(List<String> tags, OnTagSelectedListener listener) {
        this.tags = tags;
        this.selectedTags = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterTagBinding binding = ItemFilterTagBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterTagViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterTagViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public List<String> getSelectedTags() {
        return new ArrayList<>(selectedTags);
    }

    class FilterTagViewHolder extends RecyclerView.ViewHolder {
        private final ItemFilterTagBinding binding;

        FilterTagViewHolder(ItemFilterTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String tag) {
            Chip chip = binding.getRoot();
            chip.setText(tag);
            chip.setChecked(selectedTags.contains(tag));
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
                listener.onTagSelected(tag, isChecked);
            });
        }
    }
} 