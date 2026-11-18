package com.powerhold.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.powerhold.app.ui.screens.MainDashboardScreen
import com.powerhold.app.ui.theme.PowerholdTheme

/**
 * Main Activity opened when clicking the Powerhold app icon from the launcher or app drawer.
 *
 * Displays the Material 3 Expressive configuration dashboard allowing the user to:
 * 1. View and test current trigger action.
 * 2. Select from alphabetical Quick Toggles (Flashlight, DND, Auto-rotate, etc.).
 * 3. Select an installed App to launch.
 * 4. Select an App Activity to launch directly.
 * 5. Open Android Default Assistant Settings with one click.
 * 6. View ADB commands for advanced non-root system toggles.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            PowerholdTheme {
                MainDashboardScreen(viewModel = viewModel)
            }
        }
    }
}
