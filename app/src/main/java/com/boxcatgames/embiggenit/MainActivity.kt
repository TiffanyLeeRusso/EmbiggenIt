package com.boxcatgames.embiggenit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "embiggen_prefs"
        const val KEY_NIGHT_MODE = "night_mode"
    }

    private lateinit var editText: EditText
    private lateinit var embiggenButton: Button
    private lateinit var clearButton: Button
    private lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        embiggenButton = findViewById(R.id.embiggenButton)
        clearButton = findViewById(R.id.clearButton)
        settingsButton = findViewById(R.id.settingsButton)

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
    }

    override fun onResume() {
        super.onResume()
        // Auto-focus and show keyboard every time we return to this screen
        editText.requestFocus()
        editText.postDelayed({
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.colorModeGroup)
        val versionText = dialogView.findViewById<TextView>(R.id.versionText)

        // Set version string
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        versionText.text = NightModePreference.versionLabel(versionName)

        // Reflect current saved mode
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentMode = NightModePreference.load(prefs)
        when (currentMode) {
            NightModePreference.Selection.SYSTEM -> radioGroup.check(R.id.radioSystem)
            NightModePreference.Selection.LIGHT  -> radioGroup.check(R.id.radioLight)
            NightModePreference.Selection.DARK   -> radioGroup.check(R.id.radioDark)
        }

        // Apply immediately when selection changes; activity recreates automatically
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selection = when (checkedId) {
                R.id.radioLight -> NightModePreference.Selection.LIGHT
                R.id.radioDark  -> NightModePreference.Selection.DARK
                else            -> NightModePreference.Selection.SYSTEM
            }
            NightModePreference.save(prefs, selection)
            AppCompatDelegate.setDefaultNightMode(NightModePreference.toDelegate(selection))
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.settings_close), null)
            .show()
    }
}
