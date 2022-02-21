package net.blumia.pineapple.lockscreen.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.net.Uri
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.accessibility.openSystemA11ySettings
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.shortcuts.LockScreenShortcut
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
            val msgString = stringResource(id = R.string.msg_pls_enable_a11y_service_first)
            val msgActionString = stringResource(id = R.string.msg_action_open_a11y_settings)
            val shortcutString = stringResource(id = R.string.shortcut_name_lock)
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()
            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                    title = {
                        Text(stringResource(id = R.string.accessibility_service_prominent_disclosure_dlg_title))
                    },
                    text = {
                        Text(stringResource(id = R.string.accessibility_service_prominent_disclosure_dlg_desc))
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            openSystemA11ySettings(applicationContext)
                        }) {
                            Text(stringResource(id = R.string.accept))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDialog = false
                        }) {
                            Text(stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            val prominentDisclosureDlg = {
                showDialog = true
                // openSystemA11ySettings(applicationContext)
            }

            HomeScreen(
                showDialog = showDialog,
                scaffoldState = scaffoldState,
                onOpenA11ySettingsBtnClicked = {
                    prominentDisclosureDlg()
                },
                onLockScreenBtnClicked = {
                    val a11yService = A11yService.instance()
                    if (a11yService != null) {
                        a11yService.lockScreen()
                    } else {
                        coroutineScope.launch {
                            when (scaffoldState.snackbarHostState.showSnackbar(msgString, msgActionString)) {
                                SnackbarResult.ActionPerformed -> prominentDisclosureDlg()
                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    }
                },
                onLockScreenBtnLongPressed = {
                    val a11yService = A11yService.instance()
                    if (a11yService != null) {
                        a11yService.powerDialog()
                    } else {
                        coroutineScope.launch {
                            when (scaffoldState.snackbarHostState.showSnackbar(msgString, msgActionString)) {
                                SnackbarResult.ActionPerformed -> prominentDisclosureDlg()
                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    }
                },
                onCreateShortcutBtnClicked = {
                    val shortcutManager = getSystemService(applicationContext, ShortcutManager::class.java)

                    if (shortcutManager!!.isRequestPinShortcutSupported) {
                        // Assumes there's already a shortcut with the ID "my-shortcut".
                        // The shortcut must be enabled.
                        val pinShortcutInfo = ShortcutInfo.Builder(applicationContext, "shortcut-lockscreen-default")
                            .setShortLabel(shortcutString)
                            .setIcon(android.graphics.drawable.Icon.createWithResource(applicationContext, R.mipmap.ic_launcher_green_lock))
                            .setIntent(Intent(Intent.ACTION_VIEW, null, applicationContext, LockScreenShortcut::class.java))
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
            val applicationContext = LocalContext.current
            AboutScreen(
                onBackBtnClicked = {
                    navController.navigateUp()
                },
                onPrivacyPolicyBtnClicked = {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pineapplelockscreen-privacy/"))
                    startActivity(applicationContext, browserIntent, null)
                }
            )
        }
    }
}