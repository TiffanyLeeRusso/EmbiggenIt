package com.boxcatgames.embiggenit

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textview.MaterialTextView

class FullscreenActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TEXT = "extra_text"
    }

    private lateinit var textView: MaterialTextView
    private lateinit var seekBar: SeekBar
    private lateinit var sliderBar: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        // Full brightness + no cutout overlap
        val params = window.attributes
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        window.attributes = params

        hideSystemUi()

        textView = findViewById(R.id.fullscreenText)
        seekBar = findViewById(R.id.textSizeSeekBar)
        sliderBar = findViewById(R.id.sliderBar)

        // Load saved text size and apply
        val prefs = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val savedSp = NightModePreference.loadTextSize(prefs)
        textView.textSize = savedSp
        seekBar.progress = NightModePreference.spToProgress(savedSp)

        textView.text = intent.getStringExtra(EXTRA_TEXT) ?: ""

        // Tap text area or scroll view to close — slider bar is excluded
        textView.setOnClickListener { finish() }
        findViewById<View>(R.id.fullscreenScroll).setOnClickListener { finish() }

        // Size and rotate the SeekBar to be vertical once the layout is known
        sliderBar.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    sliderBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    sizeAndRotateSeekBar()
                }
            }
        )

        // Move slider bar to correct side
        val root = findViewById<LinearLayout>(R.id.fullscreenRoot)
        if (NightModePreference.loadSliderSide(prefs) == NightModePreference.SliderSide.LEFT) {
            root.removeView(sliderBar)
            root.addView(sliderBar, 0)
        }

        // SeekBar changes: update text size for this session only (don't save)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                textView.textSize = NightModePreference.progressToSp(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

        // Safe area insets: pad the root so content clears the cutout in all orientations
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fullscreenRoot)) { view, insets ->
            val cutout = insets.getInsets(
                WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars()
            )
            view.setPadding(cutout.left, cutout.top, cutout.right, cutout.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onPause() {
        super.onPause()
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

    private fun sizeAndRotateSeekBar() {
        // When rotated -90°, the SeekBar's width becomes its visual height.
        // Set width = container height so it fills the full bar length.
        // Height = container width (56dp) so the thumb sits centred in the bar.
        val barHeight = sliderBar.height
        val barWidth = sliderBar.width
        seekBar.layoutParams = FrameLayout.LayoutParams(barHeight, barWidth).apply {
            gravity = android.view.Gravity.CENTER
        }
        // -90°: progress=0 → bottom (smaller), progress=max → top (bigger)
        seekBar.rotation = -90f
    }
}
