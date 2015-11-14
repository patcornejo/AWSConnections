package com.patcornejo.awsconnections.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.patcornejo.awsconnections.utils.Globals;

public class PrefsManager {
    private static final String ID = "UserID";
    private static final String EMAIL = "UserEmail";

    private static SharedPreferences ds;
    private static PrefsManager prefs;

    public PrefsManager() {
        ds = AppManager.getInstance().getContext().getSharedPreferences("com.patcornejo.aws.PREFERENCES", Context.MODE_PRIVATE);
    }

    public static PrefsManager getInstance() {
        if (prefs == null)
            prefs = new PrefsManager();

        return prefs;
    }

    public void putRegistration() {
        ds.edit().putString(ID, Globals.USER.getUserID()).apply();
        ds.edit().putString(EMAIL, Globals.USER.getEmail()).apply();
    }

    public String getEmail() {
        return ds.getString(EMAIL, null);
    }

    public String getUserID() {
        return ds.getString(ID, null);
    }

    public boolean isRegistered() {
        return (ds.getString(EMAIL, null) != null);
    }

    public void clear() {
        ds.edit().clear().apply();
    }
}
