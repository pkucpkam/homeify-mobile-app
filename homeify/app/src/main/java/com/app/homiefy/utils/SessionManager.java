package com.app.homiefy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "HomifySession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Create a login session
    public void createLoginSession(String userId, String userRole) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_ROLE, userRole);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    // Save additional user details
    public void saveUserDetails(String userName, String userEmail) {
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.commit();
    }

    // Update specific user info
    public void updateUserInfo(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    // Get User ID
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    // Get User Role
    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, null);
    }

    // Get User Name
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "Anonymous");
    }

    // Get User Email
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "No Email");
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Clear all session data
    public void logout() {
        editor.clear();
        editor.commit();
    }

    // Check if a specific key exists
    public boolean hasKey(String key) {
        return pref.contains(key);
    }

    // Get a custom value
    public String getCustomValue(String key, String defaultValue) {
        return pref.getString(key, defaultValue);
    }
}
