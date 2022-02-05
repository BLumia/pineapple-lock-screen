package net.blumia.pineapple.lockscreen

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.accessibility.openSystemA11ySettings
import net.blumia.pineapple.lockscreen.ui.home.HomeScreen
import net.blumia.pineapple.lockscreen.ui.theme.PineappleLockScreenTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PineappleLockScreenTheme {
                HomeScreen(
                    onOpenA11ySettingsBtnClicked = {
                        openSystemA11ySettings(applicationContext)
                    },
                    onLockScreenBtnClicked = {
                        val a11yService = A11yService.instance()
                        if (a11yService != null) {
                            a11yService.lockScreen()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Please enable its accessibility service first.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onCreateShortcutBtnClicked = {
                        val shortcutManager = getSystemService(ShortcutManager::class.java)

                        if (shortcutManager!!.isRequestPinShortcutSupported) {
                            // Assumes there's already a shortcut with the ID "my-shortcut".
                            // The shortcut must be enabled.
                            val pinShortcutInfo = ShortcutInfo.Builder(this, "my-shortcut")
                                .setShortLabel("ddd")
                                .setIcon(android.graphics.drawable.Icon.createWithResource(this, R.mipmap.ic_launcher))
                                .setIntent(Intent(Intent.ACTION_VIEW, null, this, LockActivity::class.java))
                                .build()

                            // Create the PendingIntent object only if your app needs to be notified
                            // that the user allowed the shortcut to be pinned. Note that, if the
                            // pinning operation fails, your app isn't notified. We assume here that the
                            // app has implemented a method called createShortcutResultIntent() that
                            // returns a broadcast intent.
                            val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)

                            // Configure the intent so that your app's broadcast receiver gets
                            // the callback successfully.For details, see PendingIntent.getBroadcast().
                            val successCallback = PendingIntent.getBroadcast(this, /* request code */ 0,
                                pinnedShortcutCallbackIntent, /* flags */ PendingIntent.FLAG_IMMUTABLE)

                            shortcutManager.requestPinShortcut(pinShortcutInfo,
                                successCallback.intentSender)
                        }
                    }
                )
            }
        }
    }
}
