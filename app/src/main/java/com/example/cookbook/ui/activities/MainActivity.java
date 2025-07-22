package com.example.cookbook.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cookbook.R;
import com.example.cookbook.util.FirebaseManager;
import com.example.cookbook.ui.fragments.HomeFragment;
import com.example.cookbook.ui.fragments.ProfileFragment;
import com.example.cookbook.ui.fragments.FavoritesFragment;
import com.example.cookbook.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * MainActivity - The primary entry point of the CookBook application.
 * 
 * This activity serves as the main controller for the entire application lifecycle.
 * It handles:
 * - User authentication (login/registration)
 * - Navigation between different app sections
 * - Fragment management for the main app interface
 * 
 * The activity has two main states:
 * 1. Authentication State: Shows login/registration screen
 * 2. Main App State: Shows the main application with bottom navigation
 */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private FirebaseManager firebaseManager;

    /**
     * Called when the activity is first created.
     * 
     * This method initializes the application by:
     * 1. Getting the FirebaseManager instance
     * 2. Checking if user is already authenticated
     * 3. Showing appropriate UI based on authentication status
     * 
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
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

    /**
     * Sets up the login and registration screen UI.
     * 
     * This method:
     * - Initializes all UI elements (input fields, buttons, progress bar)
     * - Sets up click listeners for login, register, and forgot password
     * - Handles user input validation
     * - Manages authentication flow with Firebase
     */
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

    /**
     * Displays the main application interface after successful authentication.
     * 
     * This method:
     * - Sets the main app layout
     * - Configures bottom navigation
     * - Loads the default fragment (HomeFragment)
     * 
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
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

    /**
     * Validates user input for authentication.
     * 
     * This method checks:
     * - Email is not empty
     * - Password is not empty and at least 6 characters long
     * - Sets appropriate error messages on input layouts
     * 
     * @param tilEmail TextInputLayout for email field
     * @param tilPassword TextInputLayout for password field
     * @param email The email string to validate
     * @param password The password string to validate
     * @return true if input is valid, false otherwise
     */
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

    /**
     * Handles bottom navigation item selection.
     * 
     * This method determines which fragment to load based on the selected
     * navigation item and switches to the appropriate fragment.
     * 
     * @param item The selected menu item
     * @return true if the item selection was handled successfully
     */
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

    /**
     * Loads a fragment into the main container.
     * 
     * This method replaces the current fragment in the fragment container
     * with the specified fragment.
     * 
     * @param fragment The fragment to load
     * @return true if fragment was loaded successfully, false otherwise
     */
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