package com.boxcatgames.embiggenit

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Encapsulates all app preferences: night mode, text size, slider side,
 * fullscreen brightness, and center text.
 * Kept free of Activity/Context references so it can be unit tested on the JVM.
 */
object NightModePreference {

    // ---- Night mode ----

    const val KEY_NIGHT_MODE = "night_mode"

    enum class Selection { SYSTEM, LIGHT, DARK }

    fun toDelegate(selection: Selection): Int = when (selection) {
        Selection.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        Selection.LIGHT  -> AppCompatDelegate.MODE_NIGHT_NO
        Selection.DARK   -> AppCompatDelegate.MODE_NIGHT_YES
    }

    fun fromDelegate(mode: Int): Selection = when (mode) {
        AppCompatDelegate.MODE_NIGHT_NO  -> Selection.LIGHT
        AppCompatDelegate.MODE_NIGHT_YES -> Selection.DARK
        else                             -> Selection.SYSTEM
    }

    fun load(prefs: SharedPreferences): Selection =
        fromDelegate(prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))

    fun save(prefs: SharedPreferences, selection: Selection) {
        prefs.edit().putInt(KEY_NIGHT_MODE, toDelegate(selection)).apply()
    }

    // ---- Text size ----

    const val KEY_TEXT_SIZE_SP = "text_size_sp"
    const val MIN_TEXT_SIZE_SP = 32f
    const val MAX_TEXT_SIZE_SP = 96f
    const val DEFAULT_TEXT_SIZE_SP = 48f
    const val SEEKBAR_MAX = 64 // 1 step = 1sp (96 - 32 = 64)

    /** Map SeekBar progress (0..64) to sp value (32..96). */
    fun progressToSp(progress: Int): Float = MIN_TEXT_SIZE_SP + progress

    /** Map sp value to SeekBar progress. */
    fun spToProgress(sp: Float): Int = (sp - MIN_TEXT_SIZE_SP).toInt().coerceIn(0, SEEKBAR_MAX)

    fun loadTextSize(prefs: SharedPreferences): Float =
        prefs.getFloat(KEY_TEXT_SIZE_SP, DEFAULT_TEXT_SIZE_SP)

    fun saveTextSize(prefs: SharedPreferences, sp: Float) {
        prefs.edit().putFloat(KEY_TEXT_SIZE_SP, sp).apply()
    }

    // ---- Slider side ----

    const val KEY_SLIDER_SIDE = "slider_side"

    enum class SliderSide { RIGHT, LEFT }

    fun loadSliderSide(prefs: SharedPreferences): SliderSide =
        if (prefs.getString(KEY_SLIDER_SIDE, "RIGHT") == "LEFT") SliderSide.LEFT
        else SliderSide.RIGHT

    fun saveSliderSide(prefs: SharedPreferences, side: SliderSide) {
        prefs.edit().putString(KEY_SLIDER_SIDE, side.name).apply()
    }

    // ---- Fullscreen brightness ----

    const val KEY_BRIGHTNESS = "brightness"

    enum class Brightness { FULL, MEDIUM, NO_CHANGE }

    fun loadBrightness(prefs: SharedPreferences): Brightness =
        when (prefs.getString(KEY_BRIGHTNESS, "FULL")) {
            "MEDIUM"    -> Brightness.MEDIUM
            "NO_CHANGE" -> Brightness.NO_CHANGE
            else        -> Brightness.FULL
        }

    fun saveBrightness(prefs: SharedPreferences, brightness: Brightness) {
        prefs.edit().putString(KEY_BRIGHTNESS, brightness.name).apply()
    }

    // ---- Center text ----

    const val KEY_CENTER_TEXT = "center_text"

    fun loadCenterText(prefs: SharedPreferences): Boolean =
        prefs.getBoolean(KEY_CENTER_TEXT, false)

    fun saveCenterText(prefs: SharedPreferences, center: Boolean) {
        prefs.edit().putBoolean(KEY_CENTER_TEXT, center).apply()
    }

    // ---- Shared ----

    fun versionLabel(versionName: String): String = "Embiggen It! v$versionName"
}
