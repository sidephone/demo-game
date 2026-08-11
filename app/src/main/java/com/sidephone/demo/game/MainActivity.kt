package com.sidephone.demo.game

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sidephone.demo.game.ui.theme.DemogameTheme
import com.sidephone.demo.game.util.Gamepad
import com.sidephone.demo.game.util.Gameplay

// TODO: add .editorconfig
// TODO: add comments
// TODO: add menu screen
// TODO: add highscore screen
// TODO: make it possible to start/stop/pause the game
// TODO: add readme

class MainActivity : ComponentActivity() {
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
                    SetContent(
                        txt = gameScreen.screenOutput,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
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

@Composable
fun SetContent(txt: String, modifier: Modifier = Modifier) {
    Text(text = txt, modifier = modifier)
}
