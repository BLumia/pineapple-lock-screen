package net.blumia.pineapple.lockscreen.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.accessibility.openSystemA11ySettings
import net.blumia.pineapple.lockscreen.LockActivity
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.ui.about.AboutScreen
import net.blumia.pineapple.lockscreen.ui.home.HomeScreen

object MainDestinations {
    const val MAIN_ROUTE = "main"
    const val ABOUT_ROUTE = "about"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.MAIN_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.MAIN_ROUTE) {
            val applicationContext = LocalContext.current
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
                    val shortcutManager = getSystemService(applicationContext, ShortcutManager::class.java)

                    if (shortcutManager!!.isRequestPinShortcutSupported) {
                        // Assumes there's already a shortcut with the ID "my-shortcut".
                        // The shortcut must be enabled.
                        val pinShortcutInfo = ShortcutInfo.Builder(applicationContext, "my-shortcut")
                            .setShortLabel("ddd")
                            .setIcon(android.graphics.drawable.Icon.createWithResource(applicationContext, R.mipmap.ic_launcher))
                            .setIntent(Intent(Intent.ACTION_VIEW, null, applicationContext, LockActivity::class.java))
                            .build()

                        // Create the PendingIntent object only if your app needs to be notified
                        // that the user allowed the shortcut to be pinned. Note that, if the
                        // pinning operation fails, your app isn't notified. We assume here that the
                        // app has implemented a method called createShortcutResultIntent() that
                        // returns a broadcast intent.
                        val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)

                        // Configure the intent so that your app's broadcast receiver gets
                        // the callback successfully.For details, see PendingIntent.getBroadcast().
                        val successCallback = PendingIntent.getBroadcast(applicationContext, /* request code */ 0,
                            pinnedShortcutCallbackIntent, /* flags */ PendingIntent.FLAG_IMMUTABLE)

                        shortcutManager.requestPinShortcut(pinShortcutInfo,
                            successCallback.intentSender)
                    }
                },
                onActionAboutClicked = {
                    navController.navigate(MainDestinations.ABOUT_ROUTE)
                }
            )
        }
        composable(MainDestinations.ABOUT_ROUTE) {
            AboutScreen(
                onBackBtnClicked = {
                    navController.navigateUp()
                }
            )
        }
    }
}