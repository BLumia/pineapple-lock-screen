package net.blumia.pineapple.lockscreen.ui

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.net.Uri
import android.provider.Settings
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
import net.blumia.pineapple.lockscreen.BuildConfig
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.preferences.*
import net.blumia.pineapple.lockscreen.shortcuts.LockScreenShortcut
import net.blumia.pineapple.lockscreen.ui.about.AboutScreen
import net.blumia.pineapple.lockscreen.ui.home.HomeScreen
import net.blumia.pineapple.lockscreen.ui.settings.SettingsScreen


object MainDestinations {
    const val MAIN_ROUTE = "main"
    const val ABOUT_ROUTE = "about"
    const val SETTINGS_ROUTE = "settings"
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

            val prominentDisclosureAccepted by applicationContext.booleanPreference(
                PreferencesKeys.PROMINENT_DISCLOSURE_ACCEPTED).collectAsState(
                initial = false
            )

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
                            coroutineScope.launch {
                                applicationContext.setBooleanPreference(PreferencesKeys.PROMINENT_DISCLOSURE_ACCEPTED, true)
                            }
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
                if (!prominentDisclosureAccepted) {
                    showDialog = true
                } else {
                    openSystemA11ySettings(applicationContext)
                }
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
                onOpenQuickSettingsBtnClicked = {
                    val a11yService = A11yService.instance()
                    if (a11yService != null) {
                        a11yService.quickSettings()
                    } else {
                        coroutineScope.launch {
                            when (scaffoldState.snackbarHostState.showSnackbar(msgString, msgActionString)) {
                                SnackbarResult.ActionPerformed -> prominentDisclosureDlg()
                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    }
                },
                onActionSettingsClicked = {
                    navController.navigate(MainDestinations.SETTINGS_ROUTE)
                },
                onActionAboutClicked = {
                    navController.navigate(MainDestinations.ABOUT_ROUTE)
                }
            )
        }

        composable(MainDestinations.SETTINGS_ROUTE) {
            val applicationContext = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val deprecatedShortcutMethodEnabled by applicationContext.booleanPreference(
                PreferencesKeys.DEPRECATED_SHORTCUT_METHOD).collectAsState(
                initial = false
            )
            val msgCompatMethodDescString = stringResource(id = R.string.option_use_compat_method_long_desc)
            val msgBatteryOptimizationDescString = stringResource(id = R.string.option_battery_optimization_long_desc)
            var dialogText by remember { mutableStateOf("") }
            if (dialogText.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = {
                        dialogText = ""
                    },
                    text = { Text(dialogText) },
                    confirmButton = {
                        TextButton(onClick = {
                            dialogText = ""
                        }) {
                            Text(stringResource(id = R.string.ok))
                        }
                    },
                )
            }

            SettingsScreen(
                onBackBtnClicked = {
                    navController.navigateUp()
                },
                deprecatedShortcutMethodEnabled = deprecatedShortcutMethodEnabled,
                onDeprecatedShortcutSwitchClicked = { enabled ->
                    coroutineScope.launch {
                        applicationContext.setBooleanPreference(PreferencesKeys.DEPRECATED_SHORTCUT_METHOD, enabled)
                    }
                },
                onDeprecatedShortcutInfoBtnClicked = {
                    dialogText = msgCompatMethodDescString
                },
                onBatteryOptimizationBtnClicked = {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    applicationContext.startActivity(intent)
                },
                onBatteryOptimizationInfoBtnClicked = {
                    dialogText = msgBatteryOptimizationDescString
                },
            )
        }

        composable(MainDestinations.ABOUT_ROUTE) {
            val applicationContext = LocalContext.current
            val appPackageName = applicationContext.packageName
            AboutScreen(
                onBackBtnClicked = {
                    navController.navigateUp()
                },
                onShareBtnClicked = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, BuildConfig.STORE_LINK.format(appPackageName))
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(applicationContext, shareIntent, null)
                },
                onGetPlusVersionBtnClicked = {
                    startActivity(
                        applicationContext,
                        Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=net.blumia.pineapple.lockscreen.plus")),
                        null
                    )
                },
                onPrivacyPolicyBtnClicked = {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pineapplelockscreen-privacy/"))
                    startActivity(applicationContext, browserIntent, null)
                },
                onRateUsBtnClicked = {
                    try {
                        startActivity(
                            applicationContext,
                            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")),
                            null
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            applicationContext,
                            Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")),
                            null
                        )
                    }
                },
                onSourceCodeBtnClicked = {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BLumia/pineapple-lock-screen"))
                    startActivity(applicationContext, browserIntent, null)
                }
            )
        }
    }
}