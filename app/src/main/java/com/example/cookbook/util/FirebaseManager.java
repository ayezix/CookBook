package com.example.cookbook.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.cookbook.BuildConfig;
import com.example.cookbook.CookBookApplication;
import com.example.cookbook.R;
import com.example.cookbook.api.ApiClient;
import com.example.cookbook.api.model.ApiIngredient;
import com.example.cookbook.api.model.ApiRecipe;
import com.example.cookbook.api.model.ApiRecipeResponse;
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
                .addOnFailureListener(e -> 
                    Log.e(TAG, "Registration failed", e));
    }

    public Task<AuthResult> loginUser(String email, String password) {
        Log.d(TAG, "Attempting to login user: " + email);
        return auth.signInWithEmailAndPassword(email, password);
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
        return db.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
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
        return auth.sendPasswordResetEmail(email);
    }


    public Task<Void> addSampleRecipes() {
        List<Recipe> sampleRecipes = new ArrayList<>();
        
        // Recipe 1: Classic Margherita Pizza
        Recipe pizza = new Recipe();
        pizza.setTitle("Classic Margherita Pizza");
        pizza.setCategory("Dinner");
        pizza.setInstructions("1. Preheat oven to 450°F (230°C)\n" +
                "2. Roll out pizza dough on a floured surface\n" +
                "3. Spread tomato sauce evenly\n" +
                "4. Add fresh mozzarella slices\n" +
                "5. Add fresh basil leaves\n" +
                "6. Drizzle with olive oil\n" +
                "7. Bake for 12-15 minutes until crust is golden");
        pizza.setImageUrl("https://images.unsplash.com/photo-1604382354936-07c5d9983bd3");
        List<Ingredient> pizzaIngredients = new ArrayList<>();
        pizzaIngredients.add(new Ingredient("Pizza dough", "1", "ball"));
        pizzaIngredients.add(new Ingredient("Tomato sauce", "1/2", "cup"));
        pizzaIngredients.add(new Ingredient("Fresh mozzarella", "8", "oz"));
        pizzaIngredients.add(new Ingredient("Fresh basil", "1/4", "cup"));
        pizzaIngredients.add(new Ingredient("Olive oil", "2", "tbsp"));
        pizza.setIngredients(pizzaIngredients);
        sampleRecipes.add(pizza);

        // Recipe 2: Chocolate Chip Cookies
        Recipe cookies = new Recipe();
        cookies.setTitle("Classic Chocolate Chip Cookies");
        cookies.setCategory("Desserts");
        cookies.setInstructions("1. Preheat oven to 350°F (175°C)\n" +
                "2. Cream butter and sugars until fluffy\n" +
                "3. Add eggs and vanilla, mix well\n" +
                "4. Combine flour, baking soda, and salt\n" +
                "5. Mix dry ingredients into wet ingredients\n" +
                "6. Fold in chocolate chips\n" +
                "7. Drop rounded tablespoons onto baking sheet\n" +
                "8. Bake for 10-12 minutes until golden");
        cookies.setImageUrl("https://images.unsplash.com/photo-1499636136210-6f4ee915583e");
        List<Ingredient> cookieIngredients = new ArrayList<>();
        cookieIngredients.add(new Ingredient("Butter", "1", "cup"));
        cookieIngredients.add(new Ingredient("White sugar", "3/4", "cup"));
        cookieIngredients.add(new Ingredient("Brown sugar", "3/4", "cup"));
        cookieIngredients.add(new Ingredient("Eggs", "2", "large"));
        cookieIngredients.add(new Ingredient("Vanilla extract", "1", "tsp"));
        cookieIngredients.add(new Ingredient("Flour", "2 1/4", "cups"));
        cookieIngredients.add(new Ingredient("Baking soda", "1", "tsp"));
        cookieIngredients.add(new Ingredient("Chocolate chips", "2", "cups"));
        cookies.setIngredients(cookieIngredients);
        sampleRecipes.add(cookies);

        // Recipe 3: Avocado Toast
        Recipe avocadoToast = new Recipe();
        avocadoToast.setTitle("Avocado Toast with Poached Egg");
        avocadoToast.setCategory("Breakfast");
        avocadoToast.setInstructions("1. Toast bread until golden\n" +
                "2. Mash avocado with salt and pepper\n" +
                "3. Poach egg in simmering water for 3-4 minutes\n" +
                "4. Spread avocado on toast\n" +
                "5. Top with poached egg\n" +
                "6. Sprinkle with red pepper flakes");
        avocadoToast.setImageUrl("https://images.unsplash.com/photo-1588137378633-dea1336ce1e2");
        List<Ingredient> toastIngredients = new ArrayList<>();
        toastIngredients.add(new Ingredient("Sourdough bread", "2", "slices"));
        toastIngredients.add(new Ingredient("Avocado", "1", "medium"));
        toastIngredients.add(new Ingredient("Eggs", "2", "large"));
        toastIngredients.add(new Ingredient("Salt", "1/4", "tsp"));
        toastIngredients.add(new Ingredient("Black pepper", "1/4", "tsp"));
        toastIngredients.add(new Ingredient("Red pepper flakes", "1/4", "tsp"));
        avocadoToast.setIngredients(toastIngredients);
        sampleRecipes.add(avocadoToast);

        // Recipe 4: Greek Salad
        Recipe greekSalad = new Recipe();
        greekSalad.setTitle("Classic Greek Salad");
        greekSalad.setCategory("Lunch");
        greekSalad.setInstructions("1. Combine chopped vegetables in a large bowl\n" +
                "2. Add olives and feta cheese\n" +
                "3. Whisk together olive oil, lemon juice, and oregano\n" +
                "4. Pour dressing over salad\n" +
                "5. Toss gently to combine\n" +
                "6. Season with salt and pepper");
        greekSalad.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c");
        List<Ingredient> saladIngredients = new ArrayList<>();
        saladIngredients.add(new Ingredient("Cucumber", "1", "large"));
        saladIngredients.add(new Ingredient("Tomatoes", "4", "medium"));
        saladIngredients.add(new Ingredient("Red onion", "1", "small"));
        saladIngredients.add(new Ingredient("Bell pepper", "1", "medium"));
        saladIngredients.add(new Ingredient("Feta cheese", "8", "oz"));
        saladIngredients.add(new Ingredient("Kalamata olives", "1/2", "cup"));
        saladIngredients.add(new Ingredient("Olive oil", "1/4", "cup"));
        saladIngredients.add(new Ingredient("Lemon juice", "2", "tbsp"));
        saladIngredients.add(new Ingredient("Dried oregano", "1", "tsp"));
        greekSalad.setIngredients(saladIngredients);
        sampleRecipes.add(greekSalad);

        // Add all recipes to Firestore
        List<Task<DocumentReference>> tasks = new ArrayList<>();
        for (Recipe recipe : sampleRecipes) {
            recipe.setUserId(getCurrentUserId());
            tasks.add(addRecipe(recipe));
        }

        return Tasks.whenAll(tasks).continueWith(task -> null);
    }

    public void searchOnlineRecipes(String query, OnRecipesLoadedListener listener) {
        String apiKey = BuildConfig.SPOONACULAR_API_KEY;
        ApiClient.getRecipeService().searchRecipes(apiKey, query, 10, true, true, true, true)
            .enqueue(new Callback<ApiRecipeResponse>() {
                @Override
                public void onResponse(Call<ApiRecipeResponse> call, Response<ApiRecipeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ApiRecipe> searchResults = response.body().getResults();
                        if (searchResults != null && !searchResults.isEmpty()) {
                            // Get recipe IDs
                            StringBuilder ids = new StringBuilder();
                            for (ApiRecipe recipe : searchResults) {
                                if (ids.length() > 0) ids.append(",");
                                ids.append(recipe.getId());
                            }
                            
                            // Get detailed recipe information
                            ApiClient.getRecipeService().getRecipeInformation(apiKey, ids.toString(), true)
                                .enqueue(new Callback<List<ApiRecipe>>() {
                                    @Override
                                    public void onResponse(Call<List<ApiRecipe>> call, Response<List<ApiRecipe>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            List<Recipe> recipes = convertApiRecipesToLocalRecipes(response.body());
                                            listener.onRecipesLoaded(recipes);
                                        } else {
                                            listener.onError("Failed to load recipe details");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<ApiRecipe>> call, Throwable t) {
                                        listener.onError(t.getMessage());
                                    }
                                });
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

    private List<Recipe> convertApiRecipesToLocalRecipes(List<ApiRecipe> apiRecipes) {
        List<Recipe> recipes = new ArrayList<>();
        for (ApiRecipe apiRecipe : apiRecipes) {
            try {
                Recipe recipe = new Recipe();
                recipe.setTitle(apiRecipe.getTitle());
                recipe.setInstructions(apiRecipe.getInstructions());
                recipe.setImageUrl(apiRecipe.getImageUrl());
                recipe.setCategory(apiRecipe.getDishTypes() != null && !apiRecipe.getDishTypes().isEmpty() 
                    ? apiRecipe.getDishTypes().get(0) 
                    : "Other");
                recipe.setImportedFromApi(true);
                recipe.setFavorite(false); // Initialize as not favorited
                recipe.setCreatedAt(System.currentTimeMillis());

                List<Ingredient> ingredients = new ArrayList<>();
                if (apiRecipe.getIngredients() != null) {
                    for (ApiIngredient apiIngredient : apiRecipe.getIngredients()) {
                        Ingredient ingredient = new Ingredient(
                            apiIngredient.getName(),
                            String.valueOf(apiIngredient.getAmount()),
                            apiIngredient.getUnit()
                        );
                        ingredients.add(ingredient);
                    }
                }
                recipe.setIngredients(ingredients);
                recipes.add(recipe);
                Log.d(TAG, "Successfully converted API recipe: " + recipe.getTitle());
            } catch (Exception e) {
                Log.e(TAG, "Error converting API recipe: " + apiRecipe.getTitle(), e);
            }
        }
        return recipes;
    }

    public interface OnRecipesLoadedListener {
        void onRecipesLoaded(List<Recipe> recipes);
        void onError(String error);
    }

    public Task<Void> addSampleRecipesIfNeeded() {
        String userId = getCurrentUserId();
        if (userId == null) return Tasks.forException(new Exception("User not logged in"));
        return db.collection(USERS_COLLECTION).document(userId).get().continueWithTask(task -> {
            if (!task.isSuccessful() || !task.getResult().exists()) {
                return Tasks.forException(new Exception("User not found"));
            }
            Boolean alreadyAdded = task.getResult().getBoolean("sampleRecipesAdded");
            if (alreadyAdded != null && alreadyAdded) {
                return Tasks.forResult(null);
            }
            // Add sample recipes
            return addSampleRecipes().addOnSuccessListener(aVoid -> {
                db.collection(USERS_COLLECTION).document(userId)
                  .update("sampleRecipesAdded", true);
            });
        });
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
} 