package com.example.mastermind

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mastermind.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    // Define initialTimeMs as a class variable
    private val initialTimeMs: Long = 100000 //Time given per round in ms

    private lateinit var btnNewGame : Button
    private lateinit var btnQuitGame : Button
    private lateinit var roundStatus : TextView

    private lateinit var answerSlots : Array<ImageView>
    private lateinit var previousColorSlots : Array<ImageView>
    private lateinit var colorPicks : Array<ImageView>
    private lateinit var answerColors : List<Int>

    private lateinit var playerChoice:Array<Int>

    private val colors = arrayOf(
        R.color.red,
        R.color.yellow,
        R.color.blue,
        R.color.green,
        R.color.orange,
        R.color.purple,
        R.color.aqua,
        R.color.pink
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
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

        setContentView(binding.root)

        btnNewGame = binding.newGameButton
        btnQuitGame = binding.quitGameButton
        roundStatus = binding.roundStatusText

        btnNewGame.setOnClickListener{
            resetGame()
        }

        btnQuitGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        answerSlots = arrayOf(
            binding.colorSlot1,
            binding.colorSlot2,
            binding.colorSlot3,
            binding.colorSlot4,
            binding.colorSlot5,
            binding.colorSlot6
        )

        previousColorSlots = arrayOf(
            binding.previousColorSlot1,
            binding.previousColorSlot2,
            binding.previousColorSlot3,
            binding.previousColorSlot4,
            binding.previousColorSlot5,
            binding.previousColorSlot6,
        )
        playerChoice = Array(answerSlots.size) { -1 }

        colorPicks = arrayOf(
            binding.colorLeftChoice1,
            binding.colorLeftChoice2,
            binding.colorLeftChoice3,
            binding.colorLeftChoice4,
            binding.colorRightChoice1,
            binding.colorRightChoice2,
            binding.colorRightChoice3,
            binding.colorRightChoice4
        )

        answerColors = generateAnswer()

        timerTextView = binding.timerTextView

        val timerLimitMs = intent.getLongExtra("TIMER_LIMIT_MS", 95000L)
        //Initialize countdown timer
        countDownTimer = object : CountDownTimer(timerLimitMs, 1000) {
            override fun onTick(msUntilFinished: Long) {
                //Update timer textView
                val secondsRemaining = msUntilFinished / 1000 + 1
                timerTextView.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                timerTextView.text = "0"
                for (i in colorPicks.indices) {
                    val colorPickView = colorPicks[i]
                    colorPickView.isClickable = false
                }
                answerSlots.forEachIndexed { index, answerView ->
                    answerView.backgroundTintList = ColorStateList.valueOf(answerColors[index])
                }

                roundStatus.text = "You Lost"
                roundStatus.visibility = View.VISIBLE
                btnNewGame.visibility = View.VISIBLE
                btnQuitGame.visibility = View.VISIBLE
            }
        }

        countDownTimer.start()


        colorPicks.forEachIndexed { _, colorPickView ->
            colorPickView.setOnClickListener {
                val anim = AnimationUtils.loadAnimation(this, R.anim.button_pressed)
                colorPickView.startAnimation(anim)
                val color = colorPickView.backgroundTintList!!.defaultColor
                answerSlots.find { it.backgroundTintList == null }?.let { answerView ->
                    answerView.backgroundTintList = ColorStateList.valueOf(color)
                    playerChoice[answerSlots.indexOf(answerView)] = color
                    if (answerSlots.last() == answerView) {
                        //Calling checkPlayerAnswer so that player has time to see the complete guess.
                        Handler().postDelayed( {
                            checkPlayerAnswer()
                        }, 300)
                    }
                }
            }
        }
    }

    private fun generateAnswer() : List<Int> {
        val colorList = colors.toMutableList()
        val answer = mutableListOf<Int>()
        for (i in 0 until 6) {
            val colorIndex = (0 until colorList.size).random()
            answer.add(colorList.removeAt(colorIndex))
        }
        return answer.map { ContextCompat.getColor(this, it) }
    }

    private fun checkPlayerAnswer() {
        var correctColor = 0
        var correctPosition = 0

        val answerColorsCopy = answerColors.toList().toTypedArray()
        val playerChoiceCopy = playerChoice.toList().toTypedArray()

        for (i in answerColorsCopy.indices) {
            if (playerChoiceCopy[i] == answerColorsCopy[i]) {
                correctPosition++
                answerColorsCopy[i] = -1
                playerChoiceCopy[i] = -2
            }
        }

        for (i in answerColorsCopy.indices) {
            if (playerChoiceCopy.contains(answerColorsCopy[i])) {
                correctColor++
                playerChoiceCopy[playerChoiceCopy.indexOf(answerColorsCopy[i])] = -2
            }
        }

        binding.correctPosition.text = correctPosition.toString()
        binding.correctColor.text = correctColor.toString()

        if (correctPosition == answerSlots.size) {
            countDownTimer.cancel()
            roundStatus.text = "You Won"
            roundStatus.visibility = View.VISIBLE
            btnNewGame.visibility = View.VISIBLE
            btnQuitGame.visibility = View.VISIBLE
        } else {
            resetGuess()
        }
    }

    private fun resetGuess() {
        for (i in previousColorSlots.indices) {
            previousColorSlots[i].backgroundTintList = answerSlots[i].backgroundTintList
        }
        //Reset players answer
        playerChoice = Array(answerSlots.size) { -1 }

        //Reset color of answer boxes
        for (answerBox in answerSlots) {
            answerBox.backgroundTintList = null
        }
    }

    private fun resetGame() {
        btnNewGame.visibility = View.GONE
        btnQuitGame.visibility = View.GONE

        for (i in colorPicks.indices) {
            val colorPickView = colorPicks[i]
            colorPickView.isClickable = true
        }

        //Generate new answer
        roundStatus.visibility = View.GONE
        answerColors = generateAnswer()
        //Reset player answer and answer boxes
        resetGuess()
        for (i in previousColorSlots.indices) {
            previousColorSlots[i].backgroundTintList = null
        }
        //Reset text views for correct colors and positions
        findViewById<TextView>(R.id.correctPosition).text = "0"
        findViewById<TextView>(R.id.correctColor).text = "0"

        timerTextView.text = (initialTimeMs / 1000).toString()
        countDownTimer.start()
    }
}