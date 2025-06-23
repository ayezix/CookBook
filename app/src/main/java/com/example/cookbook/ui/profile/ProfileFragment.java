package com.example.cookbook.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cookbook.databinding.FragmentProfileBinding;
import com.example.cookbook.util.FirebaseManager;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseManager firebaseManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        firebaseManager = FirebaseManager.getInstance();
        setupUserProfile();
        setupClickListeners();
    }

    private void setupUserProfile() {
        FirebaseUser currentUser = firebaseManager.getCurrentUser();
        if (currentUser != null) {
            binding.tvEmail.setText(currentUser.getEmail());
        }
    }

    private void setupClickListeners() {
        binding.btnLogout.setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        firebaseManager.logoutUser();
        startActivity(new Intent(requireContext(), com.example.cookbook.MainActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 