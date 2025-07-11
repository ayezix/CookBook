package com.example.cookbook.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.cookbook.BuildConfig;
import com.example.cookbook.CookBookApplication;
import com.example.cookbook.R;
import com.example.cookbook.api.ApiClient;
import com.example.cookbook.api.model.ApiRecipe;
import com.example.cookbook.api.model.ApiRecipeResponse;
import com.example.cookbook.api.model.CategoryResponse;
import com.example.cookbook.api.model.AreaResponse;
import com.example.cookbook.api.model.IngredientResponse;
import com.example.cookbook.model.RecipeFilter;
import com.example.cookbook.model.Ingredient;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static final String USERS_COLLECTION = "users";
    private static final String RECIPES_COLLECTION = "recipes";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final Context context;

    private static FirebaseManager instance;

    private FirebaseManager() {
        try {
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            context = CookBookApplication.getInstance();
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
    public Task<AuthResult> registerUser(String email, String password) {
        Log.d(TAG, "Attempting to register user: " + email);
        return auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        FirebaseUser firebaseUser = authResult.getUser();
                        User newUser = new User(firebaseUser.getUid(), email);
                        db.collection(USERS_COLLECTION)
                                .document(firebaseUser.getUid())
                                .set(newUser)
                                .addOnFailureListener(e -> 
                                    Log.e(TAG, "Error creating user document", e));
                    }
                })
                .addOnFailureListener(e -> {
                    String errorMessage = translateRegistrationError(e.getMessage());
                    Log.e(TAG, "Registration failed: " + errorMessage);
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return task;
                    } else {
                        String errorMessage = translateRegistrationError(task.getException().getMessage());
                        return Tasks.forException(new Exception(errorMessage));
                    }
                });
    }

    private String translateRegistrationError(String firebaseError) {
        if (firebaseError == null) {
            return "Registration failed";
        }
        
        if (firebaseError.contains("email address is already in use")) {
            return "This email is already registered";
        } else if (firebaseError.contains("badly formatted")) {
            return "Invalid email format";
        } else if (firebaseError.contains("network error")) {
            return "Network error. Please check your connection";
        } else if (firebaseError.contains("password is too weak")) {
            return "Password is too weak. Use a stronger password";
        } else {
            return "Registration failed: " + firebaseError;
        }
    }

    public Task<AuthResult> loginUser(String email, String password) {
        Log.d(TAG, "Attempting to login user: " + email);
        return auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(e -> {
                    String errorMessage = translateFirebaseError(e.getMessage());
                    Log.e(TAG, "Login failed: " + errorMessage);
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return task;
                    } else {
                        String errorMessage = translateFirebaseError(task.getException().getMessage());
                        return Tasks.forException(new Exception(errorMessage));
                    }
                });
    }

    private String translateFirebaseError(String firebaseError) {
        if (firebaseError == null) {
            return "Login failed";
        }
        
        // Log the exact Firebase error for debugging
        Log.d(TAG, "Firebase error: " + firebaseError);
        
        // Check for user not found errors first
        if (firebaseError.contains("no user record") || 
            firebaseError.contains("user not found") ||
            firebaseError.contains("invalid email") ||
            firebaseError.contains("there is no user record")) {
            return "No account found with this email";
        } else if (firebaseError.contains("password is invalid") ||
                   firebaseError.contains("The supplied auth credential is incorrect") ||
                   firebaseError.contains("supplied auth credential is malformed") ||
                   firebaseError.contains("has expired")) {
            // Firebase doesn't distinguish between wrong password and non-existent user
            // for security reasons, so we provide a generic message
            return "Invalid email or password";
        } else if (firebaseError.contains("badly formatted")) {
            return "Invalid email format";
        } else if (firebaseError.contains("network error")) {
            return "Network error. Please check your connection";
        } else if (firebaseError.contains("too many requests")) {
            return "Too many attempts. Please try again later";
        } else {
            return "Login failed: " + firebaseError;
        }
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
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Also remove from favorites if it was favorited
                    db.collection(RECIPES_COLLECTION)
                            .document(recipeId)
                            .delete();
                });
    }

    public Task<QuerySnapshot> getUserRecipes() {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "Cannot get user recipes: User not logged in");
            return Tasks.forException(new Exception("User not logged in"));
        }
        // First try a simple query without ordering
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", userId)
                .get();
    }

    public Task<QuerySnapshot> searchRecipesByName(String query) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "Cannot search recipes: User not logged in");
            return Tasks.forException(new Exception("User not logged in"));
        }
        String searchQuery = query.toLowerCase();
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", userId)
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
    public Task<String> uploadRecipeImage(Uri imageUri) {
        Log.d(TAG, "Starting image upload...");
        Log.d(TAG, "Selected Image URI: " + imageUri);
        
        Task<String> task = Tasks.call(() -> {
            // Convert Uri to File
            File imageFile = createTempFileFromUri(imageUri);
            
            // Create a CompletableFuture to handle the async ImgBB upload
            CompletableFuture<String> future = new CompletableFuture<>();
            
            // Upload to ImgBB
            ImgBBUploadManager.uploadImage(imageFile, new ImgBBUploadManager.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    Log.d(TAG, "Image uploaded successfully: " + imageUrl);
                    future.complete(imageUrl);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error uploading image: " + error);
                    future.completeExceptionally(new Exception(error));
                }
            });
            
            // Wait for the upload to complete
            return future.get();
        });
        
        return task;
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("recipe_image_", ".jpg", context.getCacheDir());
        
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
        }
        
        return tempFile;
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

    // New method to favorite an API recipe
    public Task<Void> favoriteApiRecipe(Recipe recipe) {
        Log.d(TAG, "Starting to favorite API recipe: " + recipe.getTitle());
        try {
            // Set a flag to indicate this recipe was imported from API
            recipe.setImportedFromApi(true);
            recipe.setFavorite(true); // Set favorite state to true
            Log.d(TAG, "Recipe marked as imported from API and favorited");
            
            // Save the recipe to Firestore
            return addRecipe(recipe)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        String recipeId = task.getResult().getId();
                        recipe.setId(recipeId); // Set the ID on the recipe object
                        Log.d(TAG, "Recipe saved successfully with ID: " + recipeId);
                        return task.getResult().getParent().document(recipeId)
                            .update("isFavorite", true);
                    } else {
                        Log.e(TAG, "Failed to save recipe", task.getException());
                        throw task.getException();
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in favoriteApiRecipe", e);
            return Tasks.forException(e);
        }
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

    public Task<Void> sendPasswordResetEmail(String email) {
        Log.d(TAG, "Sending password reset email to: " + email);
        return auth.sendPasswordResetEmail(email)
                .addOnFailureListener(e -> {
                    String errorMessage = translatePasswordResetError(e.getMessage());
                    Log.e(TAG, "Password reset failed: " + errorMessage);
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return task;
                    } else {
                        String errorMessage = translatePasswordResetError(task.getException().getMessage());
                        return Tasks.forException(new Exception(errorMessage));
                    }
                });
    }

    private String translatePasswordResetError(String firebaseError) {
        if (firebaseError == null) {
            return "Failed to send reset email";
        }
        
        if (firebaseError.contains("badly formatted")) {
            return "Invalid email format";
        } else if (firebaseError.contains("no user record")) {
            return "No account found with this email";
        } else {
            return "Error: " + firebaseError;
        }
    }

    public void searchOnlineRecipes(String query, OnRecipesLoadedListener listener) {
        searchOnlineRecipesWithFilter(RecipeFilter.bySearch(query), listener);
    }

    public void searchOnlineRecipesWithFilter(RecipeFilter filter, OnRecipesLoadedListener listener) {
        Call<ApiRecipeResponse> call;
        String logMsg = "";
        String endpoint = "filter.php";
        String param = "";
        switch (filter.getType()) {
            case CATEGORY:
                param = "c=" + filter.getValue();
                call = ApiClient.getRecipeService().filterByCategory(filter.getValue());
                break;
            case AREA:
                param = "a=" + filter.getValue();
                call = ApiClient.getRecipeService().filterByArea(filter.getValue());
                break;
            case INGREDIENT:
                param = "i=" + filter.getValue();
                call = ApiClient.getRecipeService().filterByIngredient(filter.getValue());
                break;
            case SEARCH:
            default:
                endpoint = "search.php";
                param = "s=" + filter.getValue();
                call = ApiClient.getRecipeService().searchRecipes(filter.getValue());
                break;
        }
        logMsg = "[TheMealDB API] Request: https://www.themealdb.com/api/json/v1/1/" + endpoint + "?" + param;
        android.util.Log.d(TAG, logMsg);
        System.out.println(logMsg);

        call.enqueue(new Callback<ApiRecipeResponse>() {
            @Override
            public void onResponse(Call<ApiRecipeResponse> call, Response<ApiRecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ApiRecipe> searchResults = response.body().getResults();
                    if (searchResults != null && !searchResults.isEmpty()) {
                        List<Recipe> recipes = convertApiRecipesToLocalRecipes(searchResults);
                        
                        // Apply additional local filters (like vegan, gluten-free)
                        recipes = applyLocalFilters(recipes, filter);
                        
                        // Ensure we only return maximum 10 results
                        if (recipes.size() > 10) {
                            recipes = recipes.subList(0, 10);
                        }
                        listener.onRecipesLoaded(recipes);
                    } else {
                        listener.onRecipesLoaded(new ArrayList<>());
                    }
                } else {
                    listener.onError("Failed to load recipes");
                }
            }

            @Override
            public void onFailure(Call<ApiRecipeResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    // Get available categories for filtering
    public void getCategories(OnCategoriesLoadedListener listener) {
        ApiClient.getRecipeService().getCategories()
            .enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        listener.onCategoriesLoaded(response.body().getCategories());
                    } else {
                        listener.onError("Failed to load categories");
                    }
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    listener.onError(t.getMessage());
                }
            });
    }

    // Get available areas for filtering
    public void getAreas(OnAreasLoadedListener listener) {
        ApiClient.getRecipeService().getAreas("list")
            .enqueue(new Callback<AreaResponse>() {
                @Override
                public void onResponse(Call<AreaResponse> call, Response<AreaResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        listener.onAreasLoaded(response.body().getAreas());
                    } else {
                        listener.onError("Failed to load areas");
                    }
                }

                @Override
                public void onFailure(Call<AreaResponse> call, Throwable t) {
                    listener.onError(t.getMessage());
                }
            });
    }

    // Get available ingredients for filtering
    public void getIngredients(OnIngredientsLoadedListener listener) {
        ApiClient.getRecipeService().getIngredients("list")
            .enqueue(new Callback<IngredientResponse>() {
                @Override
                public void onResponse(Call<IngredientResponse> call, Response<IngredientResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        listener.onIngredientsLoaded(response.body().getIngredients());
                    } else {
                        listener.onError("Failed to load ingredients");
                    }
                }

                @Override
                public void onFailure(Call<IngredientResponse> call, Throwable t) {
                    listener.onError(t.getMessage());
                }
            });
    }

    // Apply local filters for dietary restrictions
    private List<Recipe> applyLocalFilters(List<Recipe> recipes, RecipeFilter filter) {
        // This is where we can add local filtering logic for dietary restrictions
        // For now, we'll implement basic vegan/vegetarian filtering based on ingredients
        
        List<Recipe> filteredRecipes = new ArrayList<>();
        
        for (Recipe recipe : recipes) {
            if (isRecipeSuitableForFilter(recipe, filter)) {
                filteredRecipes.add(recipe);
            }
        }
        
        return filteredRecipes;
    }

    private boolean isRecipeSuitableForFilter(Recipe recipe, RecipeFilter filter) {
        // Add local filtering logic here
        // For example, check ingredients for vegan/vegetarian compliance
        
        // For now, return true (no additional filtering)
        // You can enhance this with ingredient analysis
        return true;
    }

    private List<Recipe> convertApiRecipesToLocalRecipes(List<ApiRecipe> apiRecipes) {
        List<Recipe> recipes = new ArrayList<>();
        for (ApiRecipe apiRecipe : apiRecipes) {
            try {
                Recipe recipe = new Recipe();
                recipe.setId(apiRecipe.getId()); // <-- Set the ID from idMeal
                recipe.setTitle(apiRecipe.getTitle());
                recipe.setInstructions(apiRecipe.getInstructions());
                recipe.setImageUrl(apiRecipe.getImageUrl());
                recipe.setCategory(apiRecipe.getCategory() != null ? apiRecipe.getCategory() : "Other");
                recipe.setImportedFromApi(true);
                recipe.setFavorite(false); // Initialize as not favorited
                recipe.setCreatedAt(System.currentTimeMillis());

                // TheMealDB stores ingredients as separate fields, so we need to extract them
                List<Ingredient> ingredients = extractIngredientsFromTheMealDB(apiRecipe);
                recipe.setIngredients(ingredients);
                
                recipes.add(recipe);
                Log.d(TAG, "Successfully converted API recipe: " + recipe.getTitle() + ", id: " + recipe.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error converting API recipe: " + apiRecipe.getTitle(), e);
            }
        }
        return recipes;
    }

    private List<Ingredient> extractIngredientsFromTheMealDB(ApiRecipe apiRecipe) {
        List<Ingredient> ingredients = new ArrayList<>();
        
        // Extract ingredients and measures from TheMealDB response
        String[] ingredientFields = {
            apiRecipe.getIngredient1(), apiRecipe.getIngredient2(), apiRecipe.getIngredient3(),
            apiRecipe.getIngredient4(), apiRecipe.getIngredient5(), apiRecipe.getIngredient6(),
            apiRecipe.getIngredient7(), apiRecipe.getIngredient8(), apiRecipe.getIngredient9(),
            apiRecipe.getIngredient10(), apiRecipe.getIngredient11(), apiRecipe.getIngredient12(),
            apiRecipe.getIngredient13(), apiRecipe.getIngredient14(), apiRecipe.getIngredient15(),
            apiRecipe.getIngredient16(), apiRecipe.getIngredient17(), apiRecipe.getIngredient18(),
            apiRecipe.getIngredient19(), apiRecipe.getIngredient20()
        };
        
        String[] measureFields = {
            apiRecipe.getMeasure1(), apiRecipe.getMeasure2(), apiRecipe.getMeasure3(),
            apiRecipe.getMeasure4(), apiRecipe.getMeasure5(), apiRecipe.getMeasure6(),
            apiRecipe.getMeasure7(), apiRecipe.getMeasure8(), apiRecipe.getMeasure9(),
            apiRecipe.getMeasure10(), apiRecipe.getMeasure11(), apiRecipe.getMeasure12(),
            apiRecipe.getMeasure13(), apiRecipe.getMeasure14(), apiRecipe.getMeasure15(),
            apiRecipe.getMeasure16(), apiRecipe.getMeasure17(), apiRecipe.getMeasure18(),
            apiRecipe.getMeasure19(), apiRecipe.getMeasure20()
        };
        
        // Combine ingredients with their measures
        for (int i = 0; i < ingredientFields.length; i++) {
            String ingredient = ingredientFields[i];
            String measure = measureFields[i];
            
            if (ingredient != null && !ingredient.trim().isEmpty()) {
                // Clean up the measure (remove extra spaces, etc.)
                String cleanMeasure = measure != null ? measure.trim() : "";
                String cleanIngredient = ingredient.trim();
                
                // If no measure is provided, use a default
                if (cleanMeasure.isEmpty()) {
                    cleanMeasure = "1";
                }
                
                ingredients.add(new Ingredient(cleanIngredient, cleanMeasure, ""));
                Log.d(TAG, "Added ingredient: " + cleanIngredient + " - " + cleanMeasure);
            }
        }
        
        // If no ingredients were found, add a placeholder
        if (ingredients.isEmpty() && apiRecipe.getTitle() != null && !apiRecipe.getTitle().isEmpty()) {
            ingredients.add(new Ingredient("Main ingredient", "1", "portion"));
            Log.d(TAG, "No ingredients found, added placeholder for: " + apiRecipe.getTitle());
        }
        
        return ingredients;
    }

    public interface OnRecipesLoadedListener {
        void onRecipesLoaded(List<Recipe> recipes);
        void onError(String error);
    }

    public interface OnCategoriesLoadedListener {
        void onCategoriesLoaded(List<CategoryResponse.Category> categories);
        void onError(String error);
    }

    public interface OnAreasLoadedListener {
        void onAreasLoaded(List<AreaResponse.Area> areas);
        void onError(String error);
    }

    public interface OnIngredientsLoadedListener {
        void onIngredientsLoaded(List<IngredientResponse.Ingredient> ingredients);
        void onError(String error);
    }

    public void loadRecipes(OnRecipesLoadedListener listener) {
        if (listener == null) {
            return;
        }

        db.collection("recipes")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Recipe> recipes = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = document.toObject(Recipe.class);
                    if (recipe != null) {
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                }
                listener.onRecipesLoaded(recipes);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading recipes", e);
                listener.onRecipesLoaded(new ArrayList<>());
            });
    }

    /**
     * Search recipes online by filter OR query (combines both result sets, removes duplicates by title)
     */
    public void searchOnlineRecipesByFilterOrQuery(RecipeFilter filter, String query, OnRecipesLoadedListener listener) {
        // If both are empty/null, just return empty
        if ((filter == null || filter.getType() == null || filter.getValue() == null || filter.getValue().isEmpty()) && (query == null || query.isEmpty())) {
            listener.onRecipesLoaded(new ArrayList<>());
            return;
        }
        // If only filter is set
        if (query == null || query.isEmpty()) {
            searchOnlineRecipesWithFilter(filter, listener);
            return;
        }
        // If only query is set
        if (filter == null || filter.getType() == null || filter.getValue() == null || filter.getValue().isEmpty()) {
            searchOnlineRecipes(query, listener);
            return;
        }
        // Both filter and query are set, so fetch by filter, then locally filter by query
        searchOnlineRecipesWithFilter(filter, new OnRecipesLoadedListener() {
            @Override
            public void onRecipesLoaded(List<Recipe> filterResults) {
                if (filterResults == null) {
                    listener.onRecipesLoaded(new ArrayList<>());
                    return;
                }
                String lowerQuery = query.toLowerCase();
                List<Recipe> filtered = new ArrayList<>();
                for (Recipe r : filterResults) {
                    if (r.getTitle() != null && r.getTitle().toLowerCase().contains(lowerQuery)) {
                        filtered.add(r);
                    }
                }
                listener.onRecipesLoaded(filtered);
            }
            @Override
            public void onError(String error) {
                // If filter search fails, just try query search
                searchOnlineRecipes(query, listener);
            }
        });
    }

    // Fetch full recipe details from TheMealDB by ID
    public void fetchFullRecipeById(String id, OnRecipesLoadedListener listener) {
        ApiClient.getRecipeService().getRecipeInformation(id).enqueue(new retrofit2.Callback<ApiRecipeResponse>() {
            @Override
            public void onResponse(Call<ApiRecipeResponse> call, Response<ApiRecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                    List<Recipe> recipes = convertApiRecipesToLocalRecipes(response.body().getResults());
                    listener.onRecipesLoaded(recipes);
                } else {
                    listener.onError("No recipe details found");
                }
            }
            @Override
            public void onFailure(Call<ApiRecipeResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
} 