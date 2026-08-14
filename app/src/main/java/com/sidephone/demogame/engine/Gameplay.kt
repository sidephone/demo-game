package com.sidephone.demogame.engine

import android.util.Log
import android.view.KeyEvent
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors
import java.util.concurrent.Future


/**
 * The main game engine class. It contains the game loop, input handling, and game state management.
 * It is designed to be simple and easy to understand, so you can modify it to create your own game.
 */
class Gameplay {
	private val LOG_TAG = Gameplay::class.java.simpleName

	// game loop
	private val TICK_INTERVAL = 1000L / 60L // Advance game logic about 60 times per second. Adjust as needed.
	private val executor = Executors.newSingleThreadScheduledExecutor()
	private var engineLooper: Future<*>? = null
	private var isPaused = false
	private var onPaused = {}

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
	@MainThread
	fun onPressedKeys(keys: Set<Int>) {
		pressedKeys = keys.toSet() // make a copy for thread safety
		preprocessInput()
	}


	/**
	 * Start or resume the game loop, or if already running, do nothing.
	 */
	@MainThread
fun start() {
		if (isRunning()) {
			return
		}

		isPaused = false
		engineLooper = executor.scheduleWithFixedDelay(
			{ advance() },
			0,
			TICK_INTERVAL,
			java.util.concurrent.TimeUnit.MILLISECONDS
		)

		Log.d(LOG_TAG, "Started the game loop with tick interval: $TICK_INTERVAL ms")
	}


	/**
	 * Pause the game loop, or if already paused, do nothing.
	 */
	@MainThread
	fun pause() {
		if (isPaused) {
			return
		}

		engineLooper?.cancel(true)
		isPaused = true
		onPaused()

		Log.d(LOG_TAG, "Paused the game loop")
	}


	/**
	 * Stop the game loop and release resources. After calling this, you can not resume the game
	 * anymore. You must create a new instance of Gameplay to start a new game.
	 */
	@MainThread
	fun stop() {
		isPaused = false
		executor.shutdownNow()
		Log.d(LOG_TAG, "Stopped the game loop")
	}


	/**
	 * A utility function that returns true if the game loop is currently running.
	 */
	@MainThread
	fun isRunning(): Boolean {
		return !isPaused && !executor.isShutdown && !executor.isTerminated
	}


	/**
	 * A utility function that returns true if the game loop is currently paused.
	 */
	@MainThread
	fun isPaused(): Boolean {
		return isPaused
	}


	/**
	 * Set an optional callback to be invoked when the game is paused. This can be used to navigate
	 * back to the main menu or perform other actions.
	 */
	@MainThread
	fun setOnPausedCallback(callback: () -> Unit): Gameplay {
		onPaused = callback
		return this
	}


	/**
	 * The main game loop function. This is equivalent to a single step or "frame" in the game. It
	 * is called repeatedly at a fixed interval (TICK_INTERVAL) to read the input, update state and
	 * perform other game logic. Finally, the "render()" method draws the current state to the screen.
	 */
	@WorkerThread
	private fun advance() {
		processGameInput()

		// optionally, do these at even longer intervals to save resources, e.g., every 100ms or 500ms
		validateMovement()
		updateScore()
		render()
	}


	/**
	 * Perform any non-game related actions, immediately after receiving the pressed keys. For example,
	 * pause the game, when "KeyEvent.KEYCODE_BUTTON_START" is pressed.
	 */
	@MainThread
	private fun preprocessInput() {
		if (KeyEvent.KEYCODE_BUTTON_START in pressedKeys) {
			pause()
		}
	}


	/**
	 * Set any game state variables based on the currently pressed keys. This is the first step in
	 * the game loop. All following steps will use these variables to calculate actions or draw objects.
	 * on the screen.
	 */
	@WorkerThread
	private fun processGameInput() {
		val keys = pressedKeys // make a copy for thread safety

		movingForward = KeyEvent.KEYCODE_DPAD_UP in keys
		movingBackward = KeyEvent.KEYCODE_DPAD_DOWN in keys
		movingLeft = KeyEvent.KEYCODE_DPAD_LEFT in keys
		movingRight = KeyEvent.KEYCODE_DPAD_RIGHT in keys
		jumping = KeyEvent.KEYCODE_BUTTON_A in keys
		shooting = KeyEvent.KEYCODE_BUTTON_B in keys
	}


	/**
	 * This is the main method that draws to the screen. In this demo, we simply update a string that
	 * represents the current game state, but it could draw graphics, update a canvas, or perform other
	 * rendering tasks in a real game.
	 */
	@WorkerThread
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


	/**
	 * An example of input validation. In this demo, we simply ensure that the player cannot move forward and backward
	 * at the same time, or left and right at the same time.
	 */
	@WorkerThread
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


	/**
	 * Update the score based on the current actions.
	 */
	@WorkerThread
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
}
