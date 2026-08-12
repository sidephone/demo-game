package com.sidephone.demogame.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import android.view.KeyEvent as AndroidKeyEvent

fun Modifier.clickableWithGamepadStart(onClick: () -> Unit): Modifier =
	this.onKeyEvent { event: KeyEvent ->
		if (
			event.type == KeyEventType.KeyDown
			&& (
				event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_BUTTON_START
				|| event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_BUTTON_A
				|| event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_BUTTON_B
			)
		) {
			onClick()
			true // consume the event
		} else {
			false
		}
	}
