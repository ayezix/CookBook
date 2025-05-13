package com.example.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cookbook.ui.auth.LoginActivity;
import com.example.cookbook.util.FirebaseManager;
import com.example.cookbook.ui.favorites.FavoritesFragment;
import com.example.cookbook.ui.home.HomeFragment;
import com.example.cookbook.ui.profile.ProfileFragment;
import com.example.cookbook.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private ActivityMainBinding binding;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        checkUserAuthentication();

        binding.bottomNavigation.setOnItemSelectedListener(this);
        
        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = firebaseManager.getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
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