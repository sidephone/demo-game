package com.sidephone.demogame.settings

object EngineSettings {
	const val TARGET_FPS = 60 // Rendering frames per second. Adjust as needed.
	const val TARGET_IPS = TARGET_FPS / 2 // Advance game logic N iterations per second. Adjust as needed.

	// @todo: Make these dynamic based on the actual screen size
	const val SCREEN_WIDTH = 480f
	const val SCREEN_HEIGHT = 640f
}
