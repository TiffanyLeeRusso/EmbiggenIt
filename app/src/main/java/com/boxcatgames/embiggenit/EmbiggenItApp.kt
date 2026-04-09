package com.boxcatgames.embiggenit

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class EmbiggenItApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply saved night mode before any Activity starts to avoid theme flash
        val prefs = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val mode = prefs.getInt(MainActivity.KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
