package com.documentorworldke.android;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class Documentor extends Application {

    private static Documentor mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mInstance = this;
    }

    public static synchronized Documentor getInstance() {
        return mInstance;
    }



}


