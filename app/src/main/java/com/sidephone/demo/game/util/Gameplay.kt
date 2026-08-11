package com.sidephone.demo.game.util

import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors

class Gameplay {
    // game loop
    private val TICK_INTERVAL = 1000L / 60L // Advance game logic about 60 times per second. Adjust as needed.
    private val executor = Executors.newSingleThreadScheduledExecutor()

    // input handling
    @Volatile private var pressedKeys = setOf<Int>()

    // game actions and state
    private var movingForward = false
    private var movingBackward = false
    private var movingLeft = false
    private var movingRight = false
    private var shooting = false
    private var jumping = false

    private var score = 0
    private val SCORE_UPDATE_INTERVAL = 350L
    private var lastScoreUpdateTime = 0L

    // output
    data class GameState(val screenOutput: String)

    private val _state = MutableStateFlow(GameState(screenOutput = ""))
    val state: StateFlow<GameState> = _state


    /**
     * Handle the pressed keys for your game logic.
     * For each key you can call appropriate handler. E.g. if KeyEvent.KEYCODE_DPAD_UP, call
     * "moveUp()" function, or if KeyEvent.KEYCODE_BUTTON_A, call "jump()" function. You can also
     * choose to ignore some keys if you don't need them for your game.
     * When a key is released, you will receive a new list of pressed keys without that key.
     *
     * @param keys The set of currently pressed keys represented by their KeyEvent key codes.
     */
    fun onPressedKeys(keys: Set<Int>) {
        pressedKeys = keys.toSet() // make a copy for thread safety
    }


    fun start() {
        executor.scheduleWithFixedDelay(
            { advance() },
            0,
            TICK_INTERVAL,
            java.util.concurrent.TimeUnit.MILLISECONDS
        )
    }


    fun stop() {
        executor.shutdownNow()
    }


    private fun advance() {
        processInput()

        // optionally, do these at even longer intervals to save resources, e.g., every 100ms or 500ms
        validateMovement()
        updateScore()
        render()
    }


    private fun processInput() {
        val keys = pressedKeys // make a copy for thread safety

        movingForward = KeyEvent.KEYCODE_DPAD_UP in keys
        movingBackward = KeyEvent.KEYCODE_DPAD_DOWN in keys
        movingLeft = KeyEvent.KEYCODE_DPAD_LEFT in keys
        movingRight = KeyEvent.KEYCODE_DPAD_RIGHT in keys
        jumping = KeyEvent.KEYCODE_BUTTON_A in keys
        shooting = KeyEvent.KEYCODE_BUTTON_B in keys
    }


    private fun validateMovement() {
        if (movingForward && movingBackward) {
            movingForward = false
            movingBackward = false
        }

        if (movingLeft && movingRight) {
            movingLeft = false
            movingRight = false
        }
    }


    private fun updateScore() {
        val now = System.currentTimeMillis()
        if (now - lastScoreUpdateTime < SCORE_UPDATE_INTERVAL) {
            return
        }
        lastScoreUpdateTime = now

        if (movingForward || movingLeft || movingRight) score += 1
        if (movingBackward) score -= 1
        if (shooting) score += 5
        if (jumping) score += 3
    }


    private fun render() {
        var actions = "Moving: "

        actions += if (movingForward)
            "Forward"
        else if (movingBackward)
            "Backward"
        else
            "No"

        actions += "\nTurning: " + if (movingLeft)
            "Left"
        else if (movingRight)
            "Right"
        else
            "No"

        actions += "\nJumping: " + if (jumping) "Yes" else "No"
        actions += "\nShooting: " + if (shooting) "Yes" else "No"

        val newScreenOutput = "-=== GAME STATE ===-\n\n$actions\n\nScore: $score"
        if (newScreenOutput != _state.value.screenOutput) {
            _state.value = GameState(newScreenOutput)
        }
    }
}
