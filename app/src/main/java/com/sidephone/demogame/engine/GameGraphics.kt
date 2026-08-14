package com.sidephone.demogame.engine

class GameGraphics {
	// @todo: Make these dynamic based on the actual screen size
	val screenWidth = 480f
	val screenHeight = 640f

	val backgroundColor = 0xFF000000.toInt() // Black background

	private val shipColor = 0xFFFFFF00.toInt()
	private val shipRadius = 15f

	private val shipCannonLength = shipRadius * 2f
	private val shipCannonColor = 0xFFFF9900.toInt()

	fun drawShip(x: Float, y: Float, direction: Int): List<DrawCommand> {
		val directionRad = direction * (Math.PI / 180).toFloat()
		val cannonCos = kotlin.math.cos(directionRad)
		val cannonSin = kotlin.math.sin(directionRad)

		return listOf(
			// cannon
			DrawCommand.Line(
				x,
				y,
				x + shipCannonLength * cannonCos,
				y + shipCannonLength * cannonSin,
				shipCannonColor
			),

			DrawCommand.Line(
				x + 1,
				y + 1,
				x + 1 + shipCannonLength * cannonCos,
				y + 1 + shipCannonLength * cannonSin,
				shipCannonColor
			),

			DrawCommand.Line(
				x - 1,
				y - 1,
				x - 1 + shipCannonLength * cannonCos,
				y - 1 + shipCannonLength * cannonSin,
				shipCannonColor
			),

			// fuselage
			DrawCommand.Circle(x, y, shipRadius, shipColor, filled = true),
		)
	}
}
