package com.example.mastermind

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate

const val EASY_TIMER_LIMIT_MS = 120000L // 120 seconds
const val NORMAL_TIMER_LIMIT_MS = 90000L // 90 seconds
const val HARD_TIMER_LIMIT_MS = 60000L // 60 seconds

class MainActivity : AppCompatActivity() {
    private val difficultyLevels = listOf("Easy", "Normal", "Hard")
    private val difficultyLevelsColor = arrayOf(R.color.green, R.color.yellow, R.color.red)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        // Set window flags to make the app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        setContentView(R.layout.activity_main)

        val difficultyLevel = findViewById<TextView>(R.id.difficultyLevel)

        val btnLowerDifficulty = findViewById<ImageView>(R.id.btnDLowerDifficulty)
        val btnIncreaseDifficulty = findViewById<ImageView>(R.id.btnIncreaseDifficulty)

        var difficultyLevelIndex = 1 // Default to "Normal"
        difficultyLevel.text = difficultyLevels[difficultyLevelIndex]
        difficultyLevel.setTextColor(difficultyLevelsColor[difficultyLevelIndex])
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            difficultyLevel.setTextColor(getColor(difficultyLevelsColor[difficultyLevelIndex]))
        }


        btnLowerDifficulty.setOnClickListener {
            difficultyLevelIndex = (difficultyLevelIndex - 1 + difficultyLevels.size) % difficultyLevels.size
            difficultyLevel.text = difficultyLevels[difficultyLevelIndex]
            difficultyLevel.setTextColor(difficultyLevelsColor[difficultyLevelIndex])
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                difficultyLevel.setTextColor(getColor(difficultyLevelsColor[difficultyLevelIndex]))
            }
        }

        btnIncreaseDifficulty.setOnClickListener {
            difficultyLevelIndex = (difficultyLevelIndex + 1) % difficultyLevels.size
            difficultyLevel.text = difficultyLevels[difficultyLevelIndex]
            difficultyLevel.setTextColor(difficultyLevelsColor[difficultyLevelIndex])
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                difficultyLevel.setTextColor(getColor(difficultyLevelsColor[difficultyLevelIndex]))
            }
        }

        val playButton = findViewById<ImageView>(R.id.playButton)
        playButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            when (difficultyLevel.text) {
                "Easy" -> intent.putExtra("TIMER_LIMIT_MS", EASY_TIMER_LIMIT_MS)
                "Normal" -> intent.putExtra("TIMER_LIMIT_MS", NORMAL_TIMER_LIMIT_MS)
                "Hard" -> intent.putExtra("TIMER_LIMIT_MS", HARD_TIMER_LIMIT_MS)
            }
            startActivity(intent)
        }
    }
}