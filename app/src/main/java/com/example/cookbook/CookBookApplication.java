package com.example.cookbook;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class CookBookApplication extends Application {
    private static CookBookApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);
    }

    public static CookBookApplication getInstance() {
        return instance;
    }
} 