package com.sidephone.demogame

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sidephone.demogame.engine.Gamepad
import com.sidephone.demogame.engine.Gameplay
import com.sidephone.demogame.screens.MainMenuScreen
import com.sidephone.demogame.screens.SettingsScreen
import com.sidephone.demogame.screens.game.GameScreen
import com.sidephone.demogame.ui.theme.DemogameTheme


private enum class Screen {
    Menu, Game, Settings
}

/**
 * Main activity of the game. It displays all screens, coordinates communication between the game
 * components, and passes input to the game engine.
 */
class MainActivity : ComponentActivity() {
	private var gamepad = Gamepad()
	private var gameplay = Gameplay()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		switchToFullScreen()

		setContent {
			DemogameTheme {
				var currentScreen by remember { mutableStateOf(Screen.Menu) }
				var isGamePaused by remember { mutableStateOf(false) }

				// Back button/gesture returns to the menu from any sub-screen
				BackHandler(enabled = currentScreen != Screen.Menu) {
					if (currentScreen == Screen.Game) {
						gameplay.pause()
					} else {
						currentScreen = Screen.Menu
					}
				}

				Box(modifier = Modifier.fillMaxSize()) {
					GameScreen(gameplay) // Keep this in memory due to an Android bug. See below.

					when (currentScreen) {
						Screen.Menu -> MainMenuScreen(
							isGamePaused = isGamePaused,
							onSettings = { currentScreen = Screen.Settings },
							onExit = { finish() },
							onEndGame = {
								gameplay.stop()
								isGamePaused = gameplay.isPaused()
							},
							onNewGame = {
								currentScreen = Screen.Game

								gamepad.reset()

								if (!gameplay.isPaused()) gameplay.reset()
								gameplay
									.setOnPausedCallback {
										isGamePaused = gameplay.isPaused()
										currentScreen = Screen.Menu
									}
									.start()
							},
						)
						Screen.Game -> {
						// Due to an Android bug, we initialize the screen at the beginning and keep the
						// object alive all the time. Otherwise, we can't make it render after returning from
						// paused state, because its surfaceCreated() method is not called again.
						// See: https://slack-chats.kotlinlang.org/t/12312231/funky-issue-i-ve-got-i-m-using-androidview-with-a-surfacevie
						// See: https://issuetracker.google.com/issues/285718058
						}
						Screen.Settings -> SettingsScreen({ currentScreen = Screen.Menu })
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
		if (gameplay.isRunning() && gamepad.onKeyUp(keyCode)) {
			gameplay.onPressedKeys(gamepad.pressedKeys)
			return true
		}

		return super.onKeyUp(keyCode, event)
	}


	private fun switchToFullScreen() {
		WindowCompat.setDecorFitsSystemWindows(window, false)

		val controller = WindowInsetsControllerCompat(window, window.decorView)
		controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
		controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
	}
}
