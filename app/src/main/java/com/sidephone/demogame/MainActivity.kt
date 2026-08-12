package com.sidephone.demogame

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sidephone.demogame.engine.Gamepad
import com.sidephone.demogame.engine.Gameplay
import com.sidephone.demogame.screens.GameScreen
import com.sidephone.demogame.screens.HighScoresScreen
import com.sidephone.demogame.screens.MainMenuScreen
import com.sidephone.demogame.ui.theme.DemogameTheme

private enum class Screen {
    Menu, Game, HighScores
}

class MainActivity : ComponentActivity() {
	private var gamepad = Gamepad()
	private var gameplay = Gameplay()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		setContent {
			DemogameTheme {
				var currentScreen by remember { mutableStateOf(Screen.Menu) }

				// Back button/gesture returns to the menu from any sub-screen
				BackHandler(enabled = currentScreen != Screen.Menu) {
					gameplay.stop()
					currentScreen = Screen.Menu
				}

				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					Box(modifier = Modifier.padding(innerPadding)) {
						when (currentScreen) {
							Screen.Menu -> MainMenuScreen(
								onNewGame = {
									currentScreen = Screen.Game
									gameplay = Gameplay()
									gameplay.start()
								},
								onHighScores = { currentScreen = Screen.HighScores },
								onExit = { finish() }
							)
							Screen.Game -> GameScreen(gameplay)
							Screen.HighScores -> HighScoresScreen()
						}
					}
				}
			}
		}
	}


	override fun onDestroy() {
		super.onDestroy()
		gameplay.stop()
	}


	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (gameplay.isRunning() && gamepad.onKeyDown(keyCode, event)) {
			gameplay.onPressedKeys(gamepad.pressedKeys)
			return true
		}

		return super.onKeyDown(keyCode, event)
	}


	override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
		if (gameplay.isRunning() && gamepad.onKeyUp(keyCode, event)) {
			gameplay.onPressedKeys(gamepad.pressedKeys)
			return true
		}

		return super.onKeyUp(keyCode, event)
	}
}
