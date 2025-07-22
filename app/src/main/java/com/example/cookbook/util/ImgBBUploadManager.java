package com.example.cookbook.util;

import android.util.Log;

import com.example.cookbook.BuildConfig;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * ImgBBUploadManager - Handles image upload to ImgBB service.
 * 
 * This class provides functionality to upload recipe images to the ImgBB
 * image hosting service. It handles the HTTP multipart form data request
 * and processes the JSON response to extract the image URL.
 * 
 * Key Features:
 * - Asynchronous image upload in background thread
 * - Multipart form data construction
 * - JSON response parsing
 * - Error handling and callback support
 * 
 * Usage:
 * - Recipe image upload in AddRecipeActivity
 * - Profile picture upload (future feature)
 * - Any image upload requirement in the app
 * 
 * Dependencies:
 * - ImgBB API key (configured in BuildConfig)
 * - OkHttp for HTTP requests
 * - JSON parsing for response handling
 */
public class ImgBBUploadManager {
    /** Tag for logging */
    private static final String TAG = "ImgBBUploadManager";
    
    /** ImgBB API endpoint URL */
    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";
    
    /** HTTP client for making requests */
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * Callback interface for upload results.
     * 
     * This interface provides methods to handle upload success and failure.
     * It's used to communicate the upload result back to the calling component.
     */
    public interface UploadCallback {
        /**
         * Called when image upload is successful.
         * 
         * @param imageUrl The URL of the uploaded image
         */
        void onSuccess(String imageUrl);
        
        /**
         * Called when image upload fails.
         * 
         * @param error Error message describing the failure
         */
        void onError(String error);
    }

    /**
     * Uploads an image file to ImgBB service.
     * 
     * This method performs the following steps:
     * 1. Creates a background thread for the upload
     * 2. Constructs multipart form data with the image and API key
     * 3. Makes HTTP POST request to ImgBB API
     * 4. Parses the JSON response to extract the image URL
     * 5. Calls the appropriate callback method with the result
     * 
     * The upload is performed asynchronously to avoid blocking the UI thread.
     * 
     * @param imageFile The image file to upload
     * @param callback Callback interface to handle upload result
     */
    public static void uploadImage(final File imageFile, final UploadCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create multipart form data with image and API key
                    RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", imageFile.getName(),
                            RequestBody.create(MediaType.parse("image/*"), imageFile))
                        .addFormDataPart("key", BuildConfig.IMGBB_API_KEY)
                        .build();

                    // Create HTTP request
                    Request request = new Request.Builder()
                        .url(IMGBB_API_URL)
                        .post(requestBody)
                        .build();

                    // Execute the request
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            callback.onError("Upload failed: " + response.code());
                            return;
                        }

                        // Parse JSON response
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        
                        if (json.getBoolean("success")) {
                            // Extract image URL from successful response
                            String imageUrl = json.getJSONObject("data")
                                .getString("url");
                            callback.onSuccess(imageUrl);
                        } else {
                            // Handle API error response
                            callback.onError("Upload failed: " + json.getString("error"));
                        }
                    }
                } catch (Exception e) {
                    // Log and handle any exceptions
                    Log.e(TAG, "Error uploading image", e);
                    callback.onError(e.getMessage());
                }
            }
        }).start();
    }
} 