package com.sidephone.demogame.engine

import android.util.Log
import android.view.KeyEvent
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.cos
import kotlin.math.sin


/**
 * The main game engine class. It contains the game loop, input handling, and game state management.
 * It is designed to be simple and easy to understand, so you can modify it to create your own game.
 */
class Gameplay {
	private val LOG_TAG = Gameplay::class.java.simpleName

	// game loop
	private val TARGET_IPS = 60 // Advance game logic 60 times per second. Adjust as needed.
	private val TICK_INTERVAL = 1000L / TARGET_IPS
	private var executor = Executors.newSingleThreadScheduledExecutor()
	private var engineLooper: Future<*>? = null
	private var isPaused = false

	private var onPaused = {}
	private var onStarted = {}

	// input handling
	@Volatile private var pressedKeys = setOf<Int>()

	// graphics
	val TARGET_FPS = 100 // adjust the smoothness as needed. Not necessarily the same as TICK_INTERVAL, which is for game logic.
	@Volatile var screenOutput: DrawCommandList = DrawCommandList()
	private val graphics = GameGraphics()
	@Volatile private var firstIteration = true

	// game actions and state
	private var shipX = 0f
	private var shipY = 0f
	private var shipDirection = 0 // degrees


	private var movingForward = false
	private var movingBackward = false
	private var movingLeft = false
	private var movingRight = false


	init {
	    reset()
	}


	/**
	 * Set the initial state of the game. Call this whenever you need to restart the game.
	 */
	fun reset() {
		pressedKeys = setOf()
		shipX = graphics.screenWidth / 2f
		shipY = graphics.screenHeight / 2f
		shipDirection = -90 // degrees

		movingForward = false
		movingBackward = false
		movingLeft = false
		movingRight = false

		if (!isGameThreadAlive()) {
			executor = Executors.newSingleThreadScheduledExecutor()
		}
	}


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
		if (isGameThreadAlive()) {
			return
		}


		isPaused = false
		firstIteration = true

		engineLooper = executor.scheduleWithFixedDelay(
			{ advance() },
			0,
			TICK_INTERVAL,
			java.util.concurrent.TimeUnit.MILLISECONDS
		)

		onStarted()

		Log.d(LOG_TAG, "Gameplay loop started at $TARGET_IPS iterations per second")
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

		Log.d(LOG_TAG, "Gameplay loop paused")
	}


	/**
	 * Stop the game loop and release resources. After calling this, you can not resume the game
	 * anymore. You must create a new instance of Gameplay to start a new game.
	 */
	@MainThread
	fun stop() {
		isPaused = false
		executor.shutdownNow()
		Log.d(LOG_TAG, "Gameplay loop stopped")
	}


	/**
	 * A utility function that returns true if the game loop is currently running.
	 */
	@MainThread
	fun isRunning(): Boolean {
		return !isPaused && isGameThreadAlive()
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
	 * Set an optional callback to be invoked immediately before the game starts.
	 */
	@MainThread
	fun setOnStartedCallback(callback: () -> Unit): Gameplay {
		onStarted = callback
		return this
	}


	/**
	 * Returns true when the game thread executor is still working.
	 */
	@MainThread
	private fun isGameThreadAlive(): Boolean {
		return !executor.isShutdown && !executor.isTerminated && (engineLooper?.isDone == false)
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
	}


	/**
	 * This is the main method that draws to the screen. In this demo, we simply update a string that
	 * represents the current game state, but it could draw graphics, update a canvas, or perform other
	 * rendering tasks in a real game.
	 */
	@WorkerThread
	private fun render() {
		var isSceneChanged = false

		if (movingLeft) {
			isSceneChanged = true
			shipDirection -= 5
		}

		if (movingRight) {
			isSceneChanged = true
			shipDirection += 5
		}

		if (movingForward) {
			isSceneChanged = true
			shipX += (cos(Math.toRadians(shipDirection.toDouble())) * 5).toFloat()
			shipY += (sin(Math.toRadians(shipDirection.toDouble())) * 5).toFloat()

			if (shipX < 0) shipX = graphics.screenWidth
			if (shipY < 0) shipY = graphics.screenHeight
			if (shipX > graphics.screenWidth) shipX = 0f
			if (shipY > graphics.screenHeight) shipY = 0f
		}

		if (firstIteration) {
			firstIteration = false
			isSceneChanged = true
		}

		if (!isSceneChanged) {
			return
		}

		val screenObjects = mutableListOf<DrawCommand>()
		screenObjects.addAll(graphics.drawShip(shipX, shipY, shipDirection))

		screenOutput = DrawCommandList(graphics.backgroundColor, screenObjects)
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
}
