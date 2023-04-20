package com.example.mastermind

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    private lateinit var answerBoxes : Array<ImageView>
    private lateinit var colorPicks : Array<ImageView>

    private var currentColorIndex: Int = 0

    private val colors = arrayOf(
        R.color.red,
        R.color.yellow,
        R.color.blue,
        R.color.green,
        R.color.orange,
        R.color.purple,
        R.color.brown,
        R.color.pink
    )

    override fun onCreate(savedInstanceState: Bundle?) {
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

        answerBoxes = arrayOf(
            findViewById(R.id.colorPick1),
            findViewById(R.id.colorPick2),
            findViewById(R.id.colorPick3),
            findViewById(R.id.colorPick4),
            findViewById(R.id.colorPick5),
            findViewById(R.id.colorPick6)
        )

        colorPicks = arrayOf(
            findViewById(R.id.colorLeftChoice1),
            findViewById(R.id.colorLeftChoice2),
            findViewById(R.id.colorLeftChoice3),
            findViewById(R.id.colorLeftChoice4),
            findViewById(R.id.colorRightChoice1),
            findViewById(R.id.colorRightChoice2),
            findViewById(R.id.colorRightChoice3),
            findViewById(R.id.colorRightChoice4)
        )


        for (i in colorPicks.indices) {
            val colorPickView = colorPicks[i]
            colorPickView.setOnClickListener{
                val color = colorPickView.imageTintList!!.defaultColor
                for (j in answerBoxes.indices) {
                    val answerView = answerBoxes[j]
                    if (answerView.backgroundTintList == null) {
                        answerView.backgroundTintList = ColorStateList.valueOf(color)
                        break
                    }
                }
            }
        }
    }

    private fun generateAnswerColorSeq() : List<Int> {
        val colorSeq = mutableListOf<Int>()
        for (i in 1..6) {
            val randomColor = colors.random()
            colorSeq.add(randomColor)
        }
        return colorSeq
    }

}