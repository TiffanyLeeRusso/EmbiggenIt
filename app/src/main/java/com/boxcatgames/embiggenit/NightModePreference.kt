package com.boxcatgames.embiggenit

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Encapsulates night mode preference read/write and the mapping between
 * radio button selection labels and AppCompatDelegate constants.
 *
 * Kept free of Activity/Context references so it can be unit tested on the JVM.
 */
object NightModePreference {

    const val KEY_NIGHT_MODE = "night_mode"

    enum class Selection { SYSTEM, LIGHT, DARK }

    /** Map a [Selection] to the corresponding [AppCompatDelegate] night mode constant. */
    fun toDelegate(selection: Selection): Int = when (selection) {
        Selection.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        Selection.LIGHT  -> AppCompatDelegate.MODE_NIGHT_NO
        Selection.DARK   -> AppCompatDelegate.MODE_NIGHT_YES
    }

    /** Map an [AppCompatDelegate] night mode constant back to a [Selection]. */
    fun fromDelegate(mode: Int): Selection = when (mode) {
        AppCompatDelegate.MODE_NIGHT_NO  -> Selection.LIGHT
        AppCompatDelegate.MODE_NIGHT_YES -> Selection.DARK
        else                             -> Selection.SYSTEM
    }

    /** Read the saved mode from [prefs], defaulting to [Selection.SYSTEM]. */
    fun load(prefs: SharedPreferences): Selection =
        fromDelegate(prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))

    /** Persist [selection] to [prefs]. */
    fun save(prefs: SharedPreferences, selection: Selection) {
        prefs.edit().putInt(KEY_NIGHT_MODE, toDelegate(selection)).apply()
    }

    /** Format the version string shown in the settings dialog. */
    fun versionLabel(versionName: String): String = "Embiggen It v$versionName"
}
