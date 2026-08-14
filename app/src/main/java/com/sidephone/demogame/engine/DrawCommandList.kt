package com.sidephone.demogame.engine

import android.graphics.Color

data class DrawCommandList(
	val backgroundColor: Int = Color.BLACK,
	val commands: List<DrawCommand> = emptyList()
)
