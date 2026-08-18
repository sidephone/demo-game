package com.sidephone.demogame.screens.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.sidephone.demogame.engine.Gameplay
import com.sidephone.demogame.engine.graphics.DrawCommand
import com.sidephone.demogame.engine.graphics.GameFrame
import com.sidephone.demogame.settings.GameplaySettings
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * A Canvas wrapper that accepts a list of draw commands from the Gameplay engine and renders it to
 * the screen.Runs on a separate thread to avoid blocking other game logic.
 */
class GameSurfaceView(context: Context, private var gameplay: Gameplay, private val menuBackground: Int) : SurfaceView(context), SurfaceHolder.Callback {
	private val LOG_TAG = GameSurfaceView::class.java.simpleName

	private var executor = Executors.newSingleThreadScheduledExecutor()
	private var renderFuture: ScheduledFuture<*>? = null
	private val paint = Paint()
	private var isCanvasCleared = false


	init {
		holder.addCallback(this)
	}


	override fun surfaceCreated(holder: SurfaceHolder) {
		run(holder) // run at least once to hide the Canvas on the first start
		gameplay.setOnStartedCallback { run(holder) }
	}


	override fun surfaceDestroyed(holder: SurfaceHolder) {
		stop()
	}


	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
		gameplay.setViewportSize(width, height)
	}


	/**
	 * Starts the rendering loop on a separate thread. The loop runs at a fixed rate defined by the
	 * TARGET_FPS constant. Does nothing if the loop is already running.
	 */
	private fun run(holder: SurfaceHolder) {
		if (renderFuture != null) {
			return
		}

		val exec = Executors.newSingleThreadScheduledExecutor()
		executor = exec
		renderFuture = exec.scheduleWithFixedDelay(
			{ render(holder) }, 0, 1_000_000_000L / GameplaySettings.TARGET_FPS, TimeUnit.NANOSECONDS
		)

		Log.d(LOG_TAG, "Rendering loop started at ${GameplaySettings.TARGET_FPS} FPS")
	}


	/**
	 * Stops the rendering loop and shuts down the executor. Does nothing if the loop is not running.
	 */
	private fun stop() {
		if (renderFuture == null) {
			return
		}

		renderFuture?.cancel(false)
		renderFuture = null
		executor.shutdown()
		Log.d(LOG_TAG, "Rendering loop stopped")
	}


	/**
	 * The main function that accepts and validates the rendering commands, and renders them to the canvas,
	 * and clears the canvas when the game is paused or stopped.
	 */
	private fun render(holder: SurfaceHolder) {
		if (!holder.surface.isValid) {
			return
		}

		var drawCommands: GameFrame?

		if (gameplay.isRunning()) {
			// when running, accept the draw command list from the gameplay engine
			drawCommands = gameplay.currentFrame
			isCanvasCleared = false
		} else if (!isCanvasCleared) {
			// when paused or stopped, run once to clear the canvas with the menu background color
			drawCommands = GameFrame(backgroundColor = menuBackground)
			isCanvasCleared = true
		} else {
			// after clearing the canvas above, stop looping and drawing to save CPU cycles and battery
			stop()
			return
		}

		val canvas = holder.lockCanvas() ?: return
		try {
			drawToCanvas(canvas, drawCommands)
		} finally {
			holder.unlockCanvasAndPost(canvas)
		}
	}


	/**
	 * Draws the provided command list to the given Canvas.
	 */
	private fun drawToCanvas(canvas: Canvas, commands: GameFrame?) {
		if (commands == null) return

		canvas.drawColor(commands.backgroundColor)

		for (command in commands.commands) {
			when (command) {
				is DrawCommand.Dot -> {
					paint.color = command.color
					canvas.drawPoint(command.x, command.y, paint)
				}

				is DrawCommand.Line -> {
					paint.color = command.color
					canvas.drawLine(command.x1, command.y1, command.x2, command.y2, paint)
				}

				is DrawCommand.Circle -> {
					paint.color = command.color
					paint.style = if (command.filled) Paint.Style.FILL else Paint.Style.STROKE
					canvas.drawCircle(command.cx, command.cy, command.radius, paint)
				}

				is DrawCommand.Rect -> {
					paint.color = command.color
					paint.style = if (command.filled) Paint.Style.FILL else Paint.Style.STROKE
					canvas.drawRect(command.left, command.top, command.right, command.bottom, paint)
				}
			}
		}
	}
}
