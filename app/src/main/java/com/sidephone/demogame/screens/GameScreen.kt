package com.sidephone.demogame.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sidephone.demogame.engine.Gameplay


@Composable
fun GameScreen(gameplay: Gameplay) {
	val gameScreen by gameplay.state.collectAsState()
	Text(text = gameScreen.screenOutput)
}
