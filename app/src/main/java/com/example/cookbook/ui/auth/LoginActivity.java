package com.example.cookbook.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbook.MainActivity;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ActivityLoginBinding;
import com.example.cookbook.util.FirebaseManager;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> handleLogin());
        binding.btnRegister.setOnClickListener(v -> handleRegister());
        binding.btnForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    private void handleLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            binding.progressBar.setVisibility(View.VISIBLE);
            firebaseManager.loginUser(email, password)
                    .addOnCompleteListener(task -> {
                        binding.progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, R.string.msg_login_success, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            String errorMessage;
                            Exception exception = task.getException();
                            if (exception != null) {
                                errorMessage = exception.getMessage();
                            } else {
                                errorMessage = getString(R.string.msg_login_failed);
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void handleRegister() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            // Disable buttons during registration
            binding.btnLogin.setEnabled(false);
            binding.btnRegister.setEnabled(false);
            binding.btnForgotPassword.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);

            firebaseManager.registerUser(email, password)
                    .addOnCompleteListener(task -> {
                        // Re-enable buttons
                        binding.btnLogin.setEnabled(true);
                        binding.btnRegister.setEnabled(true);
                        binding.btnForgotPassword.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(this, R.string.msg_register_success, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            String errorMessage;
                            Exception exception = task.getException();
                            if (exception != null) {
                                errorMessage = exception.getMessage();
                            } else {
                                errorMessage = getString(R.string.msg_register_failed);
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void handleForgotPassword() {
        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.etEmail.setError("Please enter your email");
            return;
        }

        firebaseManager.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = "Failed to send reset email";
                        Exception exception = task.getException();
                        if (exception != null) {
                            errorMessage = exception.getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        //to do email validation (and also special character)

        return isValid;
    }
} 