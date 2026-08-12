package com.sidephone.demogame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sidephone.demogame.ui.theme.DemogameTheme
import com.sidephone.demogame.ui.theme.Dimens
import com.sidephone.demogame.util.clickableWithGamepadStart

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		setContent {
			DemogameTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					MainMenu(
						modifier = Modifier.padding(innerPadding),
						onNewGame = {
							startActivity(Intent(this, GameplayActivity::class.java))
						},
						onHighScores = {
							startActivity(Intent(this, HighScoresActivity::class.java))
						},
						onExit = {
							finish()
						}
					)
				}
			}
		}
	}
}

@Composable
fun MainMenu(
    modifier: Modifier = Modifier,
    onNewGame: () -> Unit,
    onHighScores: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.MainMenuButtonContainerPadding),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

		Text(
			text = stringResource(R.string.app_name),
			style = MaterialTheme.typography.headlineMedium,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(
				top = Dimens.MainMenuTitlePaddingTop,
				bottom = Dimens.MainMenuTitlePaddingBottom
			)
		)

		val buttonModifiers = Modifier
			.fillMaxWidth()
			.padding(
				bottom = Dimens.MainMenuButtonPaddingBottom,
				start = Dimens.MainMenuButtonPaddingHorizontal,
				end = Dimens.MainMenuButtonPaddingHorizontal
			)

			Button(
				onClick = onNewGame,
				modifier = buttonModifiers.clickableWithGamepadStart(onNewGame)
			) {
				Text(text = stringResource(R.string.menu_new_game))
			}
			Button(
				onClick = onHighScores,
				modifier = buttonModifiers.clickableWithGamepadStart(onHighScores)
			) {
				Text(text = stringResource(R.string.menu_high_scores))
			}
			Button(
				onClick = onExit,
				modifier = buttonModifiers.clickableWithGamepadStart(onExit)
			) {
				Text(text = stringResource(R.string.menu_exit))
			}
		}

}
