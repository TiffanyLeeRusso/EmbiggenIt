package com.boxcatgames.embiggenit

import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView

class FullscreenActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TEXT = "extra_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        // Pad content away from cutout and system bars in fullscreen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fullscreenRoot)) { view, insets ->
            val cutout = insets.getInsets(
                WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars()
            )
            view.setPadding(cutout.left, cutout.top, cutout.right, cutout.bottom)
            insets
        }

        // Full brightness + no cutout overlap
        val params = window.attributes
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        window.attributes = params

        // True fullscreen: hide status bar and nav bar
        hideSystemUi()

        val textView = findViewById<MaterialTextView>(R.id.fullscreenText)
        textView.text = intent.getStringExtra(EXTRA_TEXT) ?: ""

        // Tap anywhere to go back
        textView.setOnClickListener { finish() }
        findViewById<View>(R.id.fullscreenRoot).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onPause() {
        super.onPause()
        // Restore normal brightness when leaving fullscreen
        val params = window.attributes
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = params
    }

    private fun hideSystemUi() {
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
