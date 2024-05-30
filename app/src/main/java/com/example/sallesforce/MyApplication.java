package com.example.sallesforce;

import android.app.Application;
import com.evergage.android.ClientConfiguration;
import com.evergage.android.Evergage;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Evergage:
        Evergage.initialize(this);
        Evergage evergage = Evergage.getInstance();

        // Recommended to set the authenticated user's ID as soon as known:
        //evergage.setUserId("34522599862");

        // Start Evergage with your Evergage Configuration:
        evergage.start(new ClientConfiguration.Builder()
                .account("e55685555563t3jh3j3j3n3n6655564349")
                .dataset("firstdataset")
                .usePushNotifications(false)
                .build());

        // ... existing code from your app starts here
    }
}
