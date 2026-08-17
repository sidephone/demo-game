package com.sidephone.demogame.engine.graphics

import kotlin.math.cos
import kotlin.math.sin

/**
 * An example on how to use the DrawCommand class to draw a spaceship. You can use this as a reference
 * to create your own game objects.
 */
class Ship {
	companion object {
		const val RADIUS: Float = 15f
		const val CANNON_LENGTH = RADIUS * 2.2f
		const val MOVE_SPEED = 3f // px per iteration
		const val TURN_SPEED = 2.8f // degrees per iteration

		object Colors {
			const val FUSELAGE: Int = 0xFFFFFF00.toInt()
			const val CANNON: Int = 0xFFFFAA00.toInt()
		}
	}


	/**
	 * Produces a list of DrawCommand objects that represent the spaceship at the given position and
	 * direction. Add these commands to the GameFrame along with any other game entities you want to draw.
	 */
	fun draw(x: Float, y: Float, direction: Float): List<DrawCommand> {
		val directionRad = (direction * Math.PI / 180).toFloat()
		val cannonCos = cos(directionRad)
		val cannonSin = sin(directionRad)

		// Orders matters - last objects will be drawn on top of the previous ones.
		return listOf(
			// cannon
			DrawCommand.Line(
				x,
				y,
				x + CANNON_LENGTH * cannonCos,
				y + CANNON_LENGTH * cannonSin,
				Colors.CANNON
			),

			DrawCommand.Line(
				x + 1,
				y + 1,
				x + 1 + CANNON_LENGTH * cannonCos,
				y + 1 + CANNON_LENGTH * cannonSin,
				Colors.CANNON
			),

			DrawCommand.Line(
				x - 1,
				y - 1,
				x - 1 + CANNON_LENGTH * cannonCos,
				y - 1 + CANNON_LENGTH * cannonSin,
				Colors.CANNON
			),

			// fuselage
			DrawCommand.Circle(x, y, RADIUS, Colors.FUSELAGE, filled = true),
		)
	}
}
