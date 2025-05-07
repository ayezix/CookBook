package com.example.cookbook.util;

import android.net.Uri;
import android.util.Log;

import com.example.cookbook.model.Recipe;
import com.example.cookbook.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static final String USERS_COLLECTION = "users";
    private static final String RECIPES_COLLECTION = "recipes";
    private static final String RECIPE_IMAGES_PATH = "recipe_images";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private static FirebaseManager instance;

    private FirebaseManager() {
        try {
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
            Log.d(TAG, "Firebase services initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase services", e);
            throw e;
        }
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            try {
                instance = new FirebaseManager();
            } catch (Exception e) {
                Log.e(TAG, "Error creating FirebaseManager instance", e);
                throw e;
            }
        }
        return instance;
    }

    // Authentication methods
    public Task<Void> registerUser(String email, String password) {
        Log.d(TAG, "Attempting to register user: " + email);
        return auth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        User newUser = new User(firebaseUser.getUid(), email);
                        return db.collection(USERS_COLLECTION)
                                .document(firebaseUser.getUid())
                                .set(newUser);
                    }
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Registration failed", task.getException());
                    }
                    return task.continueWith(t -> null);
                });
    }

    public Task<Void> loginUser(String email, String password) {
        Log.d(TAG, "Attempting to login user: " + email);
        return auth.signInWithEmailAndPassword(email, password)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Login failed", task.getException());
                    }
                    return null;
                });
    }

    public void logoutUser() {
        try {
            auth.signOut();
            Log.d(TAG, "User logged out successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
        }
    }

    // Recipe methods
    public Task<DocumentReference> addRecipe(Recipe recipe) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "Cannot add recipe: User not logged in");
            return null;
        }
        recipe.setUserId(userId);
        return db.collection(RECIPES_COLLECTION).add(recipe)
                .addOnFailureListener(e -> Log.e(TAG, "Error adding recipe", e));
    }

    public Task<Void> updateRecipe(Recipe recipe) {
        return db.collection(RECIPES_COLLECTION)
                .document(recipe.getId())
                .set(recipe);
    }

    public Task<Void> deleteRecipe(String recipeId) {
        return db.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .delete();
    }

    public Task<QuerySnapshot> getUserRecipes() {
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", getCurrentUserId())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> searchRecipesByName(String query) {
        String searchQuery = query.toLowerCase();
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", getCurrentUserId())
                .whereGreaterThanOrEqualTo("title", searchQuery)
                .whereLessThanOrEqualTo("title", searchQuery + "\uf8ff")
                .get();
    }

    public Task<QuerySnapshot> searchRecipesByCategory(String category) {
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", getCurrentUserId())
                .whereEqualTo("category", category)
                .get();
    }

    public Task<QuerySnapshot> searchRecipesByIngredient(String ingredient) {
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", getCurrentUserId())
                .whereArrayContains("ingredients", ingredient)
                .get();
    }

    // Image upload methods
    public UploadTask uploadRecipeImage(Uri imageUri) {
        String imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storage.getReference()
                .child(RECIPE_IMAGES_PATH)
                .child(imageName);
        return imageRef.putFile(imageUri);
    }

    // Favorite recipes methods
    public Task<Void> toggleFavoriteRecipe(String recipeId, boolean isFavorite) {
        return db.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .update("isFavorite", isFavorite);
    }

    public Task<QuerySnapshot> getFavoriteRecipes() {
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", getCurrentUserId())
                .whereEqualTo("isFavorite", true)
                .get();
    }

    // Helper methods
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.w(TAG, "getCurrentUserId: No user logged in");
        }
        return user != null ? user.getUid() : null;
    }

    public FirebaseUser getCurrentUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.w(TAG, "getCurrentUser: No user logged in");
        }
        return user;
    }
} 