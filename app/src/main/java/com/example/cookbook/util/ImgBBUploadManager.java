package com.example.cookbook.util;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.example.cookbook.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class ImgBBUploadManager {
    private static final String TAG = "ImgBBUploadManager";
    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";
    private static ImgBBUploadManager instance;
    private final Context context;
    private final OkHttpClient client;

    private ImgBBUploadManager(Context context) {
        this.context = context.getApplicationContext();
        this.client = new OkHttpClient();
    }

    public static synchronized ImgBBUploadManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImgBBUploadManager(context);
        }
        return instance;
    }

    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public void uploadImage(Uri imageUri, ImageUploadCallback callback) {
        try {
            Log.d(TAG, "Starting image upload to ImgBB...");
            Log.d(TAG, "Image URI: " + imageUri);
            
            // Convert image to base64
            String base64Image = convertImageToBase64(imageUri);
            Log.d(TAG, "Image converted to base64, length: " + base64Image.length());
            
            // Create request body with the correct format
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", BuildConfig.IMGBB_API_KEY)
                    .addFormDataPart("image", base64Image)
                    .build();

            Log.d(TAG, "Request body created, sending to ImgBB...");

            // Create request
            Request request = new Request.Builder()
                    .url(IMGBB_API_URL)
                    .post(requestBody)
                    .build();

            // Execute request
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Upload failed", e);
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "Received response from ImgBB. Code: " + response.code());
                    String responseBody = response.body().string();
                    Log.d(TAG, "ImgBB response: " + responseBody);
                    
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Upload failed with response: " + responseBody);
                        callback.onError("Upload failed: " + response.code() + " - " + responseBody);
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONObject data = json.getJSONObject("data");
                        String imageUrl = data.getString("url");
                        Log.d(TAG, "Successfully extracted image URL: " + imageUrl);
                        callback.onSuccess(imageUrl);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse response", e);
                        callback.onError("Failed to parse response: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing upload", e);
            callback.onError(e.getMessage());
        }
    }

    private String convertImageToBase64(Uri imageUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }
} 