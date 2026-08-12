package com.sidephone.demogame

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sidephone.demogame.engine.Gamepad
import com.sidephone.demogame.engine.Gameplay
import com.sidephone.demogame.ui.theme.DemogameTheme

class GameplayActivity : ComponentActivity() {
	private var gamepad = Gamepad()
	private var gameplay = Gameplay()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		gamepad.resetKeyPress()
		gameplay.start()

		enableEdgeToEdge()
		setContent {
			val gameScreen by gameplay.state.collectAsState()

			DemogameTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					Text(
						text = gameScreen.screenOutput,
						modifier = Modifier.padding(innerPadding)
					)
				}
			}
		}
	}


	override fun onDestroy() {
		super.onDestroy()
		gameplay.stop()
	}


	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (gamepad.onKeyDown(keyCode, event)) {
			gameplay.onPressedKeys(gamepad.pressedKeys)
			return true
		}

		return super.onKeyDown(keyCode, event)
	}


	override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
		if (gamepad.onKeyUp(keyCode, event)) {
			gameplay.onPressedKeys(gamepad.pressedKeys)
			return true
		}

		return super.onKeyUp(keyCode, event)
	}
}
