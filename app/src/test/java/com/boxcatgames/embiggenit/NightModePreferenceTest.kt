package com.boxcatgames.embiggenit

import androidx.appcompat.app.AppCompatDelegate
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [NightModePreference].
 *
 * Run with: ./gradlew test
 * These run on the JVM — no device or emulator required.
 */
class NightModePreferenceTest {

    // -------------------------------------------------------------------------
    // toDelegate: Selection -> AppCompatDelegate constant
    // -------------------------------------------------------------------------

    @Test
    fun `toDelegate maps SYSTEM to MODE_NIGHT_FOLLOW_SYSTEM`() {
        assertEquals(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            NightModePreference.toDelegate(NightModePreference.Selection.SYSTEM)
        )
    }

    @Test
    fun `toDelegate maps LIGHT to MODE_NIGHT_NO`() {
        assertEquals(
            AppCompatDelegate.MODE_NIGHT_NO,
            NightModePreference.toDelegate(NightModePreference.Selection.LIGHT)
        )
    }

    @Test
    fun `toDelegate maps DARK to MODE_NIGHT_YES`() {
        assertEquals(
            AppCompatDelegate.MODE_NIGHT_YES,
            NightModePreference.toDelegate(NightModePreference.Selection.DARK)
        )
    }

    // -------------------------------------------------------------------------
    // fromDelegate: AppCompatDelegate constant -> Selection
    // -------------------------------------------------------------------------

    @Test
    fun `fromDelegate maps MODE_NIGHT_FOLLOW_SYSTEM to SYSTEM`() {
        assertEquals(
            NightModePreference.Selection.SYSTEM,
            NightModePreference.fromDelegate(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        )
    }

    @Test
    fun `fromDelegate maps MODE_NIGHT_NO to LIGHT`() {
        assertEquals(
            NightModePreference.Selection.LIGHT,
            NightModePreference.fromDelegate(AppCompatDelegate.MODE_NIGHT_NO)
        )
    }

    @Test
    fun `fromDelegate maps MODE_NIGHT_YES to DARK`() {
        assertEquals(
            NightModePreference.Selection.DARK,
            NightModePreference.fromDelegate(AppCompatDelegate.MODE_NIGHT_YES)
        )
    }

    @Test
    fun `fromDelegate treats unknown value as SYSTEM`() {
        // Defensive: any unrecognised constant should fall back to SYSTEM
        assertEquals(
            NightModePreference.Selection.SYSTEM,
            NightModePreference.fromDelegate(999)
        )
    }

    // -------------------------------------------------------------------------
    // Round-trip: toDelegate(fromDelegate(x)) == x for all valid constants
    // -------------------------------------------------------------------------

    @Test
    fun `round-trip FOLLOW_SYSTEM`() {
        val original = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        assertEquals(original, NightModePreference.toDelegate(NightModePreference.fromDelegate(original)))
    }

    @Test
    fun `round-trip MODE_NIGHT_NO`() {
        val original = AppCompatDelegate.MODE_NIGHT_NO
        assertEquals(original, NightModePreference.toDelegate(NightModePreference.fromDelegate(original)))
    }

    @Test
    fun `round-trip MODE_NIGHT_YES`() {
        val original = AppCompatDelegate.MODE_NIGHT_YES
        assertEquals(original, NightModePreference.toDelegate(NightModePreference.fromDelegate(original)))
    }

    // -------------------------------------------------------------------------
    // versionLabel
    // -------------------------------------------------------------------------

    @Test
    fun `versionLabel formats correctly`() {
        assertEquals("Embiggen It v1.0", NightModePreference.versionLabel("1.0"))
    }

    @Test
    fun `versionLabel handles multi-part version string`() {
        assertEquals("Embiggen It v1.2.3", NightModePreference.versionLabel("1.2.3"))
    }

    @Test
    fun `versionLabel handles empty string without crashing`() {
        assertEquals("Embiggen It v", NightModePreference.versionLabel(""))
    }
}
