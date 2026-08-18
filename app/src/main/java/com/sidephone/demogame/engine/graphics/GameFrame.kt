package com.sidephone.demogame.engine.graphics

import android.graphics.Color

data class GameFrame(
	val backgroundColor: Int = Color.BLACK,
	val commands: List<DrawCommand> = emptyList()
)
