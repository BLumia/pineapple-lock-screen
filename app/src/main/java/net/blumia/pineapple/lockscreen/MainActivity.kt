package net.blumia.pineapple.lockscreen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.lockscreen.preferences.PreferencesKeys.EXCLUDE_FROM_RECENTS
import net.blumia.pineapple.lockscreen.preferences.PreferencesKeys.USE_LAUNCHER_ICON_TO_LOCK
import net.blumia.pineapple.lockscreen.preferences.booleanPreference
import net.blumia.pineapple.lockscreen.ui.NavGraph
import net.blumia.pineapple.lockscreen.ui.theme.PineappleLockScreenTheme

// For now, extend from AppCompatActivity.
// Otherwise, setApplicationLocales will do nothing.
class MainActivity : AppCompatActivity() {

    // Return if the code should continue to run.
    private fun checkAndLockScreen(): Boolean {
        var shouldLock: Boolean
        runBlocking {
            shouldLock = applicationContext.booleanPreference(USE_LAUNCHER_ICON_TO_LOCK).first()
        }
        if (!shouldLock) return false

        val a11yService = A11yService.instance()
        if (a11yService != null) {
            var deepLinked = false
            intent?.data?.let { deepLinked = true }
            if (!deepLinked) {
                var removeTask: Boolean
                runBlocking {
                    removeTask = applicationContext.booleanPreference(EXCLUDE_FROM_RECENTS).first()
                }
                a11yService.lockScreen()
                if (removeTask) finishAndRemoveTask() else finishAffinity()
                return true
            }
        }
        return false
    }
    override fun onStart() {
        super.onStart()
        checkAndLockScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PineappleLockScreenTheme {
                Surface(color = MaterialTheme.colors.background) {
                    NavGraph()
                }
            }
        }
    }
}
