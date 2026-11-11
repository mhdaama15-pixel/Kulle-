package com.mhd.kulle

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private var locked = true
    private lateinit var err: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0F172A"))
            setPadding(60, 60, 60, 60)
        }
        val title = TextView(this).apply {
            text = "Rubuta sunanka"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        val input = EditText(this).apply {
            hint = "Suna..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        }
        err = TextView(this).apply {
            setTextColor(Color.parseColor("#F87171"))
            gravity = Gravity.CENTER
        }
        val btn = Button(this).apply { text = "Ci gaba" }

        root.addView(title)
        root.addView(input)
        root.addView(err)
        root.addView(btn)
        setContentView(root)

        btn.setOnClickListener {
            val name = input.text.toString().trim()
            if (name.length < 2) {
                err.text = "Saka suna mai inganci."
            } else {
                locked = false
                getSharedPreferences("kulle", MODE_PRIVATE).edit().putString("name", name).apply()
                try { stopLockTask() } catch (e: Exception) {}
                Toast.makeText(this, "Barka, $name ✅", Toast.LENGTH_LONG).show()
                finishAndRemoveTask()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (locked) {
            try { startLockTask() } catch (e: Exception) {}   // yana toshe Back/Home/Recents
            hideBars()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (locked && hasFocus) hideBars()
    }

    @Suppress("DEPRECATION")
    private fun hideBars() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    // Toshe Back
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (locked) err.text = "Sai ka saka suna tukuna!" else super.onBackPressed()
    }

    // Toshe maɓallan Volume/Menu/Back na zahiri
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (locked && keyCode != KeyEvent.KEYCODE_ENTER &&
            (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU ||
             keyCode == KeyEvent.KEYCODE_APP_SWITCH || keyCode == KeyEvent.KEYCODE_HOME)) return true
        return super.onKeyDown(keyCode, event)
    }

    // Idan wani abu ya fitar da app, a dawo da shi gaba
    override fun onPause() {
        super.onPause()
        if (locked) {
            val am = getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
            am.moveTaskToFront(taskId, 0)
        }
    }
}
