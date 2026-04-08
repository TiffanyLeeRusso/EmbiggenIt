package com.boxcatgames.embiggenit

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var embiggenButton: Button
    private lateinit var clearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        embiggenButton = findViewById(R.id.embiggenButton)
        clearButton = findViewById(R.id.clearButton)

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
}
