package com.example.cookbook.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

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
        // Set up the logout button
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
        // Set up the change password button
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });
    }

    private void handleLogout() {
        firebaseManager.logoutUser();
        startActivity(new Intent(requireContext(), com.example.cookbook.ui.activities.MainActivity.class));
        requireActivity().finish();
    }

    private void showChangePasswordDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("New Password");

        new AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setMessage("Enter your new password:")
            .setView(input)
            .setPositiveButton("Change", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    String newPassword = input.getText().toString().trim();
                    if (newPassword.length() < 6) {
                        Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseUser user = firebaseManager.getCurrentUser();
                    if (user != null) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(com.google.android.gms.tasks.Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 