package com.cronberry;

import android.content.Context;
import android.content.SharedPreferences;

public class CronberryPref {

    private Context context;
    private String prefFileName = "com.cronberry";
    private String userEmail = "useremail";


    private SharedPreferences prefs;

    CronberryPref(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(prefFileName, 0);
    }

    public String getUserEmail() {
        return prefs.getString(userEmail, "");
    }

    public void setUserEmail(String userEmailValue) {
        prefs.edit().putString(this.userEmail, userEmailValue).apply();
    }
}
