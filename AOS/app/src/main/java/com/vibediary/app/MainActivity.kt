package com.vibediary.app

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.vibediary.app.ui.LaunchScreen
import kotlinx.coroutines.delay
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vibediary.app.ui.VibeTheme
import com.vibediary.app.ui.components.CaptureMode
import com.vibediary.app.ui.detail.EntryDetailScreen
import com.vibediary.app.ui.home.HomeScreen
import com.vibediary.app.ui.map.RouteMapScreen
import com.vibediary.app.ui.newentry.NewEntryScreen
import com.vibediary.app.ui.settings.SettingsScreen
import com.vibediary.app.data.AppLock
import com.vibediary.app.ui.lock.LockScreen

class MainActivity : ComponentActivity() {
    /** PIN gate. Up on launch, and again after [AppLock.RELOCK_AFTER_MS] in the background. */
    private val locked = mutableStateOf(false)
    /** When the app left the screen, on [SystemClock.elapsedRealtime] so changing the clock can't skip the PIN. */
    private var stoppedAt = NOT_STOPPED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app is always light (paper ground), so keep dark status/nav icons even when
        // the phone is in dark mode; otherwise the clock and battery turn white-on-white.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        val coldStart = savedInstanceState == null
        locked.value = container.lock.isEnabled && (savedInstanceState?.getBoolean(KEY_LOCKED, true) ?: true)
        stoppedAt = savedInstanceState?.getLong(KEY_STOPPED_AT, NOT_STOPPED) ?: NOT_STOPPED
        setContent {
            VibeTheme {
                // Stop the app above the system navigation (back/home) bar so that strip stays
                // Android's own; screens below this no longer draw underneath it.
                Box(Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                    val nav = rememberNavController()
                    NavHost(navController = nav, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onCapture = { mode -> nav.navigate("new/${mode.name}") },
                                onOpenEntry = { id -> nav.navigate("entry/$id") },
                                onOpenMap = { nav.navigate("map") },
                                onOpenSettings = { nav.navigate("settings") },
                            )
                        }
                        composable("new/{mode}") { backStack ->
                            val mode = runCatching { CaptureMode.valueOf(backStack.arguments?.getString("mode").orEmpty()) }
                                .getOrDefault(CaptureMode.MEMO)
                            NewEntryScreen(start = mode, appLocked = locked.value, onClose = { nav.popBackStack() })
                        }
                        composable("map") {
                            RouteMapScreen(
                                onBack = { nav.popBackStack("home", inclusive = false) },
                                onOpenEntry = { id -> nav.navigate("entry/$id") },
                            )
                        }
                        composable("entry/{id}") { backStack ->
                            EntryDetailScreen(
                                entryId = backStack.arguments?.getString("id").orEmpty(),
                                onBack = { nav.popBackStack() },
                            )
                        }
                        composable("settings") {
                            SettingsScreen(onBack = { nav.popBackStack() })
                        }
                    }

                    // Tagline under the mark on a cold start, then fade into the timeline.
                    var showLaunch by rememberSaveable { mutableStateOf(coldStart) }
                    LaunchedEffect(Unit) {
                        delay(1400)
                        showLaunch = false
                    }
                    val launchOverlay = @Composable {
                        AnimatedVisibility(visible = showLaunch, enter = EnterTransition.None, exit = fadeOut(tween(400))) {
                            LaunchScreen()
                        }
                    }

                    // Drawn in both windows: the gate's window covers this one once it appears.
                    if (locked.value) {
                        LockScreen(onUnlocked = { locked.value = false }, overlay = launchOverlay)
                    }
                    launchOverlay()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (awayTooLong() && container.lock.isEnabled) locked.value = true
        // Cleared so a later rotation doesn't restore this old trip and lock mid-use.
        stoppedAt = NOT_STOPPED
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) stoppedAt = SystemClock.elapsedRealtime()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_LOCKED, locked.value)
        // Before API 28 this runs ahead of onStop, so stoppedAt isn't set yet when leaving.
        val leavingNow = stoppedAt == NOT_STOPPED && !isChangingConfigurations
        outState.putLong(KEY_STOPPED_AT, if (leavingNow) SystemClock.elapsedRealtime() else stoppedAt)
    }

    /** Short trips (camera, gallery picker, permission screens) stay under the limit and don't re-lock. */
    private fun awayTooLong(): Boolean =
        stoppedAt != NOT_STOPPED && SystemClock.elapsedRealtime() - stoppedAt >= AppLock.RELOCK_AFTER_MS

    private companion object {
        const val KEY_LOCKED = "locked"
        const val KEY_STOPPED_AT = "stoppedAt"
        const val NOT_STOPPED = 0L
    }
}
