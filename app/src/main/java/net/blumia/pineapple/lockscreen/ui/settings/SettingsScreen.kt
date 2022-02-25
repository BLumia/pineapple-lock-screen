package net.blumia.pineapple.lockscreen.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.preferences.P_DEPRECATED_SHORTCUT_METHOD
import net.blumia.pineapple.lockscreen.preferences.P_PROMINENT_DISCLOSURE_ACCEPTED
import net.blumia.pineapple.lockscreen.preferences.booleanPreference

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    onBackBtnClicked: () -> Unit = {},
    deprecatedShortcutMethodEnabled: Boolean = false,
    onDeprecatedShortcutSwitchClicked: (Boolean) -> Unit = {},
    onDeprecatedShortcutInfoBtnClicked: () -> Unit = {},
    onBatteryOptimizationBtnClicked: () -> Unit = {},
    onBatteryOptimizationInfoBtnClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackBtnClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(stringResource(id = R.string.settings)) }
            )
        },
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
//            val switched by remember { mutableStateOf(true) }
            ListItem(
                modifier = Modifier.toggleable(
                    value = deprecatedShortcutMethodEnabled,
                    onValueChange = onDeprecatedShortcutSwitchClicked
                ),
                text = { Text(stringResource(id = R.string.option_use_compat_method)) },
                secondaryText = { Text(stringResource(id = R.string.option_use_compat_method_short_desc)) },
                trailing = {
                    Row {
                        IconButton(onClick = onDeprecatedShortcutInfoBtnClicked) {
                            Icon(Icons.Filled.Info, stringResource(id = R.string.details))
                        }
                        Switch(checked = deprecatedShortcutMethodEnabled, onCheckedChange = onDeprecatedShortcutSwitchClicked)
                    }
                }
            )

            ListItem(
                modifier = Modifier.clickable { onBatteryOptimizationBtnClicked() },
                text = { Text(stringResource(id = R.string.option_battery_optimization)) },
                secondaryText = { Text(stringResource(id = R.string.option_battery_optimization_short_desc)) },
                trailing = {
                    Row {
                        IconButton(onClick = onBatteryOptimizationInfoBtnClicked) {
                            Icon(Icons.Filled.Info, stringResource(id = R.string.details))
                        }
                        IconButton(onClick = onBatteryOptimizationBtnClicked) {
                            Icon(Icons.Filled.Settings, stringResource(id = R.string.details))
                        }
                    }
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    SettingsScreen()
}