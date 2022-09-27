package com.documentorworldke.android.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.User;

public class GetUser extends Application {

    public static User getUser(Context context, String currentUserID) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.USERS, Context.MODE_PRIVATE);
        return new User(currentUserID, prefs.getString(Constants.PIC, Constants.PIC), prefs.getString(Constants.NAME, Constants.NAME), prefs.getString(Constants.USERNAME, Constants.USERNAME), prefs.getString(Constants.EMAIL, Constants.EMAIL),prefs.getString(Constants.PHONE_NUMBER, Constants.PHONE_NUMBER),prefs.getString(Constants.COUNTRY, Constants.COUNTRY), prefs.getString(Constants.TOKEN,Constants.TOKEN),prefs.getBoolean(Constants.VERIFICATION,false)  ,prefs.getBoolean(Constants.REPORTED,false));
    }
}
