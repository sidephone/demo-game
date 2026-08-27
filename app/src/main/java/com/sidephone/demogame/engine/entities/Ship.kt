package com.sidephone.demogame.engine.entities

import com.sidephone.demogame.engine.graphics.DrawCommand
import com.sidephone.demogame.engine.graphics.DrawCommandGroup


/**
 * An example on how to use the DrawCommand class to draw a spaceship. You can use this as a reference
 * to create your own game objects.
 */
class Ship {
	companion object {
		const val RADIUS: Float = 30f
		const val MOVE_SPEED = 3f // px per iteration
		const val TURN_SPEED = 2.8f // degrees per iteration

		object Cannon {
			const val WIDTH = RADIUS * 0.1f
			const val HEIGHT = RADIUS * 0.8f
			const val COLOR: Int = 0xffddecee.toInt()
		}

		object Fuselage {
			const val WIDTH: Float = RADIUS * 0.75f
			const val HEIGHT: Float = RADIUS * 0.75f
			const val COLOR: Int = 0Xffbed9dd.toInt() // navy blue
		}

		object Wing {
			const val WIDTH: Float = RADIUS * 0.5f
			const val HEIGHT: Float = RADIUS * 0.5f
			const val COLOR: Int = 0xffeeeeee.toInt()
		}
	}


	private var drawCommands: List<DrawCommand> = listOf()


	/**
	 * Returns the full set of commands to draw a ship and position it on the screen at the given
	 * coordinates and direction.
	 */
	fun draw(x: Float, y: Float, direction: Float): DrawCommandGroup {
		drawCommands = drawCommands.ifEmpty { getDrawCommands() }

		// for convenience, we draw the ship upwards, but the zero direction is to the right, so we need
		// to rotate it permanently by 90 degrees to appear straight up.
		return DrawCommandGroup(x, y, direction + 90f, drawCommands)
	}


	/**
	 * Draw this ship in a local coordinate system, with the origin at the center of the ship. The ship
	 * is drawn facing upwards. Put the commands in DrawCommandGroup to move and rotate it on the
	 * desired position of the screen.
	 */
	private fun getDrawCommands(): List<DrawCommand> {
		// Order matters - last objects will be drawn on top of the previous ones.
		return listOf(
//			 outline - uncomment for debugging purposes
//			DrawCommand.Circle(0f, 0f, RADIUS,0xffffffff.toInt(), filled = false),

			// cannon
			DrawCommand.Rect(
				-Cannon.WIDTH / 2,
				-Cannon.HEIGHT,
				Cannon.WIDTH / 2,
				0f,
				0f,
				Cannon.COLOR,
				filled = true
			),

			// left wing
			DrawCommand.Polygon(
				listOf(
					Pair(Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2 + Wing.WIDTH, Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2 - Wing.HEIGHT)
				),
				0f,
				Wing.COLOR,
				filled = true
			),

			// right wing
			DrawCommand.Polygon(
				listOf(
					Pair(-Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2),
					Pair(-Fuselage.WIDTH / 2 - Wing.WIDTH, Fuselage.HEIGHT / 2),
					Pair(-Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2 - Wing.HEIGHT)
				),
				0f,
				Wing.COLOR,
				filled = true
			),

			// fuselage
			DrawCommand.Polygon(
				listOf(
					Pair(-Fuselage.WIDTH / 2, -Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2, -Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2),
					Pair(-Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2)
				),
				0f,
				Fuselage.COLOR,
				filled = true
			),
		)
	}
}
