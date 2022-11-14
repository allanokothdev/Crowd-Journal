package com.documentorworldke.android.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GetUser extends Application {

    public static User getUser(Context context, String currentUserID) {
        User user = new User();
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(Constants.USERS, masterKeyAlias, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            user = new User(currentUserID, sharedPreferences.getString(Constants.PIC, Constants.PIC),sharedPreferences.getString(Constants.NAME, Constants.NAME),sharedPreferences.getString(Constants.USERNAME, Constants.USERNAME), sharedPreferences.getString(Constants.EMAIL, Constants.EMAIL), sharedPreferences.getString(Constants.PHONE_NUMBER, Constants.PHONE_NUMBER), sharedPreferences.getString(Constants.COUNTRY,Constants.COUNTRY),sharedPreferences.getString(Constants.TOKEN,Constants.TOKEN), sharedPreferences.getBoolean(Constants.VERIFICATION,false),sharedPreferences.getBoolean(Constants.VERIFIED,false));
        } catch (GeneralSecurityException | IOException exception){
            exception.printStackTrace();
        }
        return user;
    }

    public static void saveUser(Context context, User user) {

        try{
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(Constants.USERS, masterKeyAlias, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.PIC, user.getPic());
            editor.putString(Constants.USERNAME, user.getUsername());
            editor.putString(Constants.NAME, user.getName());
            editor.putString(Constants.EMAIL, user.getEmail());
            editor.putString(Constants.PHONE_NUMBER, user.getPhone());
            editor.putString(Constants.TOKEN, user.getToken());
            editor.putString(Constants.COUNTRY, user.getCountry());
            editor.apply();
        } catch (GeneralSecurityException | IOException exception){
            exception.printStackTrace();
        }
    }

    public static void saveObject(Context context, String key, String value) {
        try{
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(Constants.USERS, masterKeyAlias, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (GeneralSecurityException | IOException exception){
            exception.printStackTrace();
        }
    }


    public static String fetchObject(Context context, String key) {
        String balance = "0";
        try{
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(Constants.USERS, masterKeyAlias, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            balance = sharedPreferences.getString(key, "0");
        } catch (GeneralSecurityException | IOException exception){
            exception.printStackTrace();
        }
        return balance;
    }

}
