package com.example.cookbook;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * CookBookApplication - The Application class for the CookBook Android app.
 * 
 * This class serves as the global application context and handles:
 * - Firebase initialization
 * - Google Play Services availability check
 * - One-time data migrations and updates
 * 
 * The Application class is created before any other components and provides
 * a global context for the entire application lifecycle.
 */
public class CookBookApplication extends Application {
    private static final String TAG = "CookBookApplication";
    private static CookBookApplication instance;

    /**
     * Called when the application is first created.
     * 
     * This method performs essential initialization tasks:
     * 1. Initializes Firebase services
     * 2. Checks Google Play Services availability
     * 3. Performs one-time data migrations (if needed)
     * 
     * This is the first method called when the app starts, before any
     * activities or other components are created.
     */
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
            // This migration ensures that existing recipes imported from TheMealDB API
            // are properly marked as imported recipes
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
    
    /**
     * Gets the application context.
     * 
     * This method provides access to the application context from anywhere in the app.
     * Useful for components that need context but don't have direct access to it.
     * 
     * @return The application context
     */
    public static android.content.Context getAppContext() {
        return instance;
    }
    
    /**
     * Gets the singleton instance of the application.
     * 
     * This method provides access to the application instance, which can be used
     * as a context for various operations throughout the app.
     * 
     * @return The application instance
     */
    public static CookBookApplication getInstance() {
        return instance;
    }
} 