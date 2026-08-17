package com.sidephone.demogame.screens.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.sidephone.demogame.engine.DrawCommand
import com.sidephone.demogame.engine.DrawCommandList
import com.sidephone.demogame.engine.Gameplay
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class GameSurfaceView(context: Context, private var gameplay: Gameplay, private val menuBackground: Int) : SurfaceView(context), SurfaceHolder.Callback {
	private var executor = Executors.newSingleThreadScheduledExecutor()
	private var renderFuture: ScheduledFuture<*>? = null
	private val paint = Paint()

	private var isCanvasCleared = false


	init {
		holder.addCallback(this)
	}


	override fun surfaceCreated(holder: SurfaceHolder) {
		val exec = Executors.newSingleThreadScheduledExecutor()
		executor = exec
		renderFuture = exec.scheduleWithFixedDelay(
			{ renderFrame(holder) }, 0, 1000L / gameplay.TARGET_FPS, TimeUnit.MILLISECONDS
		)
	}


	override fun surfaceDestroyed(holder: SurfaceHolder) {
		renderFuture?.cancel(false)
		renderFuture = null
		executor?.shutdown()
		executor = null
	}


	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
		// handle resize if your coordinate system needs it
	}


	private fun renderFrame(holder: SurfaceHolder) {
		var drawCommands: DrawCommandList?

		if (gameplay.isRunning() && !gameplay.isPaused()) {
			drawCommands = gameplay.screenOutput
			isCanvasCleared = false
		} else if (!isCanvasCleared) {
			drawCommands = DrawCommandList(backgroundColor = menuBackground)
			isCanvasCleared = true
		} else {
			return
		}

		val canvas = holder.lockCanvas() ?: return
		try {
			drawByCommands(canvas, drawCommands)
		} finally {
			holder.unlockCanvasAndPost(canvas)
		}
	}


	private fun drawByCommands(canvas: Canvas, commands: DrawCommandList?) {
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
