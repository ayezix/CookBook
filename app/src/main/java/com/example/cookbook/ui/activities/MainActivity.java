package com.example.cookbook.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cookbook.util.FirebaseManager;
import com.example.cookbook.ui.fragments.FavoritesFragment;
import com.example.cookbook.ui.fragments.HomeFragment;
import com.example.cookbook.ui.fragments.ProfileFragment;
import com.example.cookbook.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.cookbook.R;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private ActivityMainBinding binding;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance();
        if (firebaseManager.getCurrentUser() == null) {
            setContentView(R.layout.activity_main); // Login/register screen
            setupLoginScreen();
        } else {
            showMainAppUI(savedInstanceState);
        }
    }

    private void setupLoginScreen() {
        // Get references to UI elements
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = findViewById(R.id.tilPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView btnForgotPassword = findViewById(R.id.btnForgotPassword);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Set up login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get email and password from input fields
                String email = etEmail.getText().toString();
                email = email.trim();
                String password = etPassword.getText().toString();
                password = password.trim();
                // Validate input
                if (validateInput(tilEmail, tilPassword, email, password)) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseManager.loginUser(email, password)
                        .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                            @Override
                            public void onComplete(com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, R.string.msg_login_success, Toast.LENGTH_SHORT).show();
                                    showMainAppUI(null);
                                } else {
                                    String errorMessage;
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        errorMessage = exception.getMessage();
                                    } else {
                                        errorMessage = getString(R.string.msg_login_failed);
                                    }
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
            }
        });

        // Set up register button click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                email = email.trim();
                String password = etPassword.getText().toString();
                password = password.trim();
                if (validateInput(tilEmail, tilPassword, email, password)) {
                    btnLogin.setEnabled(false);
                    btnRegister.setEnabled(false);
                    btnForgotPassword.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseManager.registerUser(email, password)
                        .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                            @Override
                            public void onComplete(com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> task) {
                                btnLogin.setEnabled(true);
                                btnRegister.setEnabled(true);
                                btnForgotPassword.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, R.string.msg_register_success, Toast.LENGTH_SHORT).show();
                                    showMainAppUI(null);
                                } else {
                                    String errorMessage;
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        errorMessage = exception.getMessage();
                                    } else {
                                        errorMessage = getString(R.string.msg_register_failed);
                                    }
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
            }
        });

        // Set up forgot password click
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                email = email.trim();
                if (email.isEmpty()) {
                    tilEmail.setError("Please enter your email");
                    return;
                }
                firebaseManager.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                            } else {
                                String errorMessage = "Failed to send reset email";
                                Exception exception = task.getException();
                                if (exception != null) {
                                    errorMessage = exception.getMessage();
                                }
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
        });
    }

    private void showMainAppUI(Bundle savedInstanceState) {
        setContentView(R.layout.main_app);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Set up bottom navigation and fragments
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(this);
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private boolean validateInput(TextInputLayout tilEmail, TextInputLayout tilPassword, String email, String password) {
        boolean isValid = true;
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }
        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }
        return isValid;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.navigation_favorites) {
            fragment = new FavoritesFragment();
        } else if (itemId == R.id.navigation_profile) {
            fragment = new ProfileFragment();
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}