# Sidephone Demo Game
This is a minimal game example with 2D vector graphics drawn on a Canvas, and handling of Sidephone's gamepad tile. It is intended to help you get started with your own game development on Sidephone.

Feel free to restructure the project to your needs, add more screens, custom controls, a different 2D or 3D engine, add sounds and other resources, write tests, and so on. This project is intended to help you get started quicker, rather than being hard development requirement.

If you would like to see a more advanced example, check out the [Snake game](https://github.com/sidephone/snake).

## Project Structure
The code is located in `app/src/main/java/com/sidephone/demogame/`. See each file for more details.

- **engine/**
    - **entities/** Contains examples how to draw a spaceship and a background color using DrawCommands (below).
    - **graphics/**
        - **_DrawCommand._** Contains utility functions for drawing Canvas primitives, such as circles, rectangles, lines, and dots.
        - **_GameFrame._** Contains a background color and a list of DrawCommands. The gameplay engine generates a frame on every tick and passes it to the GameSurfaceView for rendering.
    - **_Gamepad._** Handles the gamepad input and provides it to the game engine, `Gameplay`. You shouldn't need to modify this.
    - **_Gameplay._** The game engine. Contains the main game logic. Define your game rules and mechanics, graphics rendering, and audio playback here.
- **screens/**
    - **game/**
        - **_GameScreen._** This is the screen where the game is played. If needed, add extra buttons or UI elements here.
        - **_GameSurfaceView._** The Canvas wrapper that handles the rendering of the game graphics.
    - **_MainMenuScreen._** The main menu displayed on app launch. You can add extra buttons or connect it to other screens.
    - **_SettingsScreen._** An empty screen template. You can add the game settings or rework it to your needs.
- **settings/**
    - ... (Contains constants used through the project. Also, a good place to put the SharedPreferences, if needed)
- **ui**/
    - **components/** ... (custom UI components, such as buttons, titles, etc.)
    - **modifiers/** ... (custom UI modifiers for Jetpack Compose)
    - **theme/** ... (standard Android UI theme files: Color, Dimens, Type, etc.)

## Setup
The project is intended to be developed in Android Studio. The current Android Studio version, as of the time of writing this document, is Android Studio Quail 3 | 2026.1.3 Patch 1. Compatibility with older versions is not guaranteed.

If you have not configured Android Studio yet, follow [the official manual](https://developer.android.com/training/basics/firstapp), then just import the project into Android Studio. It should run without any additional configuration.

You are now ready to start developing your game!

_Please, note that there may be significant visual discrepancies between the emulator and a real device. Always test your game on a real device before publishing it._