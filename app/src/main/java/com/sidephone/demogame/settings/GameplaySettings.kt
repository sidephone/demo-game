package com.sidephone.demogame.settings

object GameplaySettings {
	const val TARGET_FPS = 120 // Rendering frames per second. Use multiples of the device screen refresh rate for better performance
	const val TARGET_IPS = 50 // Advance game logic N iterations per second. Indirectly affects the FPS
	const val GAME_SPEED = 100 // %

	const val SHIP_INITIAL_DIRECTION = -90.0 // degrees
	val getShipInitialPosition: (Float) -> Float = { viewportSize: Float -> viewportSize / 2f }
}
