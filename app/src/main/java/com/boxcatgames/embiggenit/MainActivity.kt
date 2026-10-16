package com.boxcatgames.embiggenit

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "embiggen_prefs"
        const val KEY_NIGHT_MODE = "night_mode"
    }

    private lateinit var editText: EditText
    private lateinit var embiggenButton: Button
    private lateinit var clearButton: Button
    private lateinit var settingsButton: Button
    private lateinit var pasteButton: Button
    private var isSettingsDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        embiggenButton = findViewById(R.id.embiggenButton)
        clearButton = findViewById(R.id.clearButton)
        settingsButton = findViewById(R.id.settingsButton)
        pasteButton = findViewById(R.id.pasteButton)

        embiggenButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotBlank()) {
                val intent = Intent(this, FullscreenActivity::class.java)
                intent.putExtra(FullscreenActivity.EXTRA_TEXT, text)
                startActivity(intent)
            }
        }

        clearButton.setOnClickListener {
            editText.text.clear()
            editText.requestFocus()
        }

        settingsButton.setOnClickListener {
            showSettingsDialog()
        }

         pasteButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).coerceToText(this).toString()
                if (text.isNotBlank()) {
                    // Insert at cursor position rather than replacing all text
                    val start = editText.selectionStart.coerceAtLeast(0)
                    val end = editText.selectionEnd.coerceAtLeast(0)
                    editText.text.replace(minOf(start, end), maxOf(start, end), text)
                }
            }
        }
        
        if (savedInstanceState?.getBoolean("settings_open") == true) {
            showSettingsDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        editText.requestFocus()
        editText.postDelayed({
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("settings_open", isSettingsDialogOpen)
    }

    private fun showSettingsDialog() {
        isSettingsDialogOpen = true
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null, false)
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // ---- Color mode ----
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.colorModeGroup)
        val currentMode = NightModePreference.load(prefs)
        when (currentMode) {
            NightModePreference.Selection.SYSTEM -> radioGroup.check(R.id.radioSystem)
            NightModePreference.Selection.LIGHT  -> radioGroup.check(R.id.radioLight)
            NightModePreference.Selection.DARK   -> radioGroup.check(R.id.radioDark)
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selection = when (checkedId) {
                R.id.radioLight -> NightModePreference.Selection.LIGHT
                R.id.radioDark  -> NightModePreference.Selection.DARK
                else            -> NightModePreference.Selection.SYSTEM
            }
            NightModePreference.save(prefs, selection)
            AppCompatDelegate.setDefaultNightMode(NightModePreference.toDelegate(selection))
            // Activity recreates here — onSaveInstanceState saves the flag,
            // onCreate restores it and reopens the dialog automatically
        }

        // ---- Brightness ----
        val brightnessGroup = dialogView.findViewById<RadioGroup>(R.id.brightnessGroup)
        when (NightModePreference.loadBrightness(prefs)) {
            NightModePreference.Brightness.FULL      -> brightnessGroup.check(R.id.radioBrightnessFull)
            NightModePreference.Brightness.MEDIUM    -> brightnessGroup.check(R.id.radioBrightnessMedium)
            NightModePreference.Brightness.NO_CHANGE -> brightnessGroup.check(R.id.radioBrightnessNoChange)
        }
        brightnessGroup.setOnCheckedChangeListener { _, checkedId ->
            val brightness = when (checkedId) {
                R.id.radioBrightnessMedium   -> NightModePreference.Brightness.MEDIUM
                R.id.radioBrightnessNoChange -> NightModePreference.Brightness.NO_CHANGE
                else                         -> NightModePreference.Brightness.FULL
            }
            NightModePreference.saveBrightness(prefs, brightness)
        }
        
        // ---- Text size ----
        val textSizePreview = dialogView.findViewById<TextView>(R.id.textSizePreview)
        val textSizeSeekBar = dialogView.findViewById<SeekBar>(R.id.textSizeSeekBarSettings)

        val savedSp = NightModePreference.loadTextSize(prefs)
        textSizeSeekBar.progress = NightModePreference.spToProgress(savedSp)
        textSizePreview.textSize = savedSp

        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                val sp = NightModePreference.progressToSp(progress)
                textSizePreview.textSize = sp
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {
                // Save when the user lifts their finger
                val sp = NightModePreference.progressToSp(sb.progress)
                NightModePreference.saveTextSize(prefs, sp)
            }
        })

        // ---- Slider side ----
        val sliderSideGroup = dialogView.findViewById<RadioGroup>(R.id.sliderSideGroup)
        val currentSide = NightModePreference.loadSliderSide(prefs)
        when (currentSide) {
            NightModePreference.SliderSide.RIGHT -> sliderSideGroup.check(R.id.radioSliderRight)
            NightModePreference.SliderSide.LEFT  -> sliderSideGroup.check(R.id.radioSliderLeft)
        }
        sliderSideGroup.setOnCheckedChangeListener { _, checkedId ->
            val side = if (checkedId == R.id.radioSliderLeft) NightModePreference.SliderSide.LEFT
                       else NightModePreference.SliderSide.RIGHT
            NightModePreference.saveSliderSide(prefs, side)
        }

        // ---- Center text ----
        val centerTextSwitch = dialogView.findViewById<SwitchCompat>(R.id.centerTextSwitch)
        centerTextSwitch.isChecked = NightModePreference.loadCenterText(prefs)
        centerTextSwitch.setOnCheckedChangeListener { _, isChecked ->
            NightModePreference.saveCenterText(prefs, isChecked)
        }

        // ---- Show fullscreen sizer ----
        val showSizerSwitch = dialogView.findViewById<SwitchCompat>(R.id.showSizerSwitch)
        showSizerSwitch.isChecked = NightModePreference.loadShowSizer(prefs)
        showSizerSwitch.setOnCheckedChangeListener { _, isChecked ->
            NightModePreference.saveShowSizer(prefs, isChecked)
        }

        // ---- Version ----
        val versionText = dialogView.findViewById<TextView>(R.id.versionText)
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName ?: "?"
        versionText.text = NightModePreference.versionLabel(versionName)

        // ---- Show —---
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.settings_close)) { _, _ ->
                isSettingsDialogOpen = false
            }
            .setOnCancelListener {
                isSettingsDialogOpen = false
            }
            .show()
    }
}
