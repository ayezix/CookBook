package com.example.cookbook;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.android.gms.common.GoogleApiAvailability;

public class CookBookApplication extends Application {
    private static final String TAG = "CookBookApplication";
    private static CookBookApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully");
            
            // Check Google Play Services
            int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            if (resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS) {
                Log.d(TAG, "Google Play Services is available");
            } else {
                Log.e(TAG, "Google Play Services is not available. Error code: " + resultCode);
            }
                
            // One-time update: set importedFromApi=true for Pierogi (Polish Dumplings)
            com.example.cookbook.util.FirebaseManager.getInstance().updateRecipeImportedFlagByTitle(
                "Pierogi (Polish Dumplings)",
                true,
                new com.example.cookbook.util.FirebaseManager.OnRecipesLoadedListener() {
                    @Override
                    public void onRecipesLoaded(java.util.List<com.example.cookbook.model.Recipe> recipes) {
                        android.util.Log.d("CookBookApp", "Updated importedFromApi for Pierogi: " + recipes.size());
                    }
                    @Override
                    public void onError(String error) {
                        android.util.Log.e("CookBookApp", "Error updating importedFromApi for Pierogi: " + error);
                    }
                }
            );
        } catch (Exception e) {
            Log.e(TAG, "Error initializing app", e);
        }
    }

    public static CookBookApplication getInstance() {
        return instance;
    }
} 