package com.sidephone.demogame.engine

import android.view.KeyEvent

class Gamepad {
	val pressedKeys = mutableSetOf<Int>()


	fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (hasKey(keyCode) && event?.repeatCount == 0) {
			pressedKeys.add(keyCode)
			return true
		}

		return false
	}


	fun onKeyUp(keyCode: Int): Boolean {
		if (hasKey(keyCode)) {
			pressedKeys.remove(keyCode)
			return true
		}

		return false
	}


	fun reset() {
		pressedKeys.clear()
	}


	private fun hasKey(keyCode: Int): Boolean {
		return when (keyCode) {
			KeyEvent.KEYCODE_BUTTON_A,
			KeyEvent.KEYCODE_BUTTON_B,
			KeyEvent.KEYCODE_BUTTON_X,
			KeyEvent.KEYCODE_BUTTON_Y,
			KeyEvent.KEYCODE_BUTTON_SELECT,
			KeyEvent.KEYCODE_BUTTON_START,
			KeyEvent.KEYCODE_DPAD_UP,
			KeyEvent.KEYCODE_DPAD_DOWN,
			KeyEvent.KEYCODE_DPAD_LEFT,
			KeyEvent.KEYCODE_DPAD_RIGHT -> true
			else -> false
		}
	}
}
