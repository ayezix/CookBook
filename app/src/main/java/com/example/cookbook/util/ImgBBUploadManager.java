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

public class ImgBBUploadManager {
    private static final String TAG = "ImgBBUploadManager";
    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";
    private static final OkHttpClient client = new OkHttpClient();

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public static void uploadImage(File imageFile, UploadCallback callback) {
        new Thread(() -> {
            try {
                RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), imageFile))
                    .addFormDataPart("key", BuildConfig.IMGBB_API_KEY)
                    .build();

                Request request = new Request.Builder()
                    .url(IMGBB_API_URL)
                    .post(requestBody)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Upload failed: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    
                    if (json.getBoolean("success")) {
                        String imageUrl = json.getJSONObject("data")
                            .getString("url");
                        callback.onSuccess(imageUrl);
                    } else {
                        callback.onError("Upload failed: " + json.getString("error"));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error uploading image", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }
} 