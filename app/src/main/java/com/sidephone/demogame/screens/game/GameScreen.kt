package com.sidephone.demogame.screens.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.sidephone.demogame.engine.Gameplay


@Composable
fun GameScreen(gameplay: Gameplay) {
	AndroidView(
		modifier = Modifier.fillMaxSize(),
		factory = { context -> GameSurfaceView(context, gameplay) }
	)
}
