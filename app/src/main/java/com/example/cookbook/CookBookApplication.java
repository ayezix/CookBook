package com.example.cookbook;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class CookBookApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
} 