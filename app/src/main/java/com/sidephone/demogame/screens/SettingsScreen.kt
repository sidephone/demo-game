package com.sidephone.demogame.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sidephone.demogame.R
import com.sidephone.demogame.ui.theme.Dimens
import com.sidephone.demogame.util.MenuButton
import com.sidephone.demogame.util.clickableWithGamepadStart

@Composable
fun SettingsScreen(onBack: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(Dimens.MainMenuButtonContainerPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		Text(
			text = stringResource(R.string.menu_settings),
			style = MaterialTheme.typography.headlineMedium,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(
				top = Dimens.MainMenuTitlePaddingTop,
				bottom = Dimens.MainMenuTitlePaddingBottom
			),
			color = MaterialTheme.colorScheme.onBackground
		)

		Text(
			text = "No settings available yet.",
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onBackground,
			modifier = Modifier.padding(bottom = Dimens.MainMenuButtonPaddingBottom)
		)

		MenuButton(
			onClick = onBack,
			modifier = Modifier.fillMaxWidth()
			.padding(
				bottom = Dimens.MainMenuButtonPaddingBottom,
				start = Dimens.MainMenuButtonPaddingHorizontal,
				end = Dimens.MainMenuButtonPaddingHorizontal
			)
			.clickableWithGamepadStart(onBack)
		) {
			Text(stringResource(R.string.menu_back))
		}
	}
}
