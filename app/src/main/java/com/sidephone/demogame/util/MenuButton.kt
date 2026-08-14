package com.sidephone.demogame.util

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * The default Material3 Button highlighting when focused is too subtle. This defines more obvious
 * colors for the focused state.
 */
@Composable
fun MenuButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable RowScope.() -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }
	val isFocused by interactionSource.collectIsFocusedAsState()

	Button(
		onClick = onClick,
		interactionSource = interactionSource,
		colors = ButtonDefaults.buttonColors(
			// background color
			containerColor = if (isFocused) MaterialTheme.colorScheme.secondaryContainer
			else MaterialTheme.colorScheme.secondary,

			// text color
			contentColor = if (isFocused) MaterialTheme.colorScheme.onSecondaryContainer
			else MaterialTheme.colorScheme.onSecondary
		),
		modifier = modifier,
		content = content
	)
}
