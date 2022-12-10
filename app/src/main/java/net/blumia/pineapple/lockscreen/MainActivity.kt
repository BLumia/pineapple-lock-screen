package net.blumia.pineapple.lockscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import net.blumia.pineapple.lockscreen.ui.NavGraph
import net.blumia.pineapple.lockscreen.ui.theme.PineappleLockScreenTheme

// For now, extend from AppCompatActivity.
// Otherwise, setApplicationLocales will do nothing.
class MainActivity : AppCompatActivity() {

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
