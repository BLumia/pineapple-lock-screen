package net.blumia.pineapple.lockscreen.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.text.intl.Locale as IntlLocale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import net.blumia.pineapple.lockscreen.R
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    onBackBtnClicked: () -> Unit = {},
    deprecatedShortcutMethodEnabled: Boolean = false,
    onDeprecatedShortcutSwitchClicked: (Boolean) -> Unit = {},
    onDeprecatedShortcutInfoBtnClicked: () -> Unit = {},
    useLauncherIconToLock: Boolean = false,
    onUseLauncherIconToLockSwitchClicked: (Boolean) -> Unit = {},
    onUseLauncherIconToLockInfoButtonClicked: () -> Unit = {},
    excludeFromRecents: Boolean = false,
    onExcludeFromRecentsSwitchClicked: (Boolean) -> Unit = {},
    onExcludeFromRecentsInfoBtnClicked: () -> Unit = {},
    onBatteryOptimizationBtnClicked: () -> Unit = {},
    onBatteryOptimizationInfoBtnClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackBtnClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(stringResource(id = R.string.settings)) }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
        ) {
            var expanded by remember { mutableStateOf(false) }
            val locales = arrayOf( // should aligns to locale_config.xml
                "en",
                "zh-CN",
                "de",
                "ja",
                "nl",
                "pl",
                "pt-BR",
                "tr"
            )
            val currentLocale = AppCompatDelegate.getApplicationLocales()[0]
                ?: Locale.forLanguageTag(IntlLocale.current.toLanguageTag())

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                ListItem(
                    text = { Text(stringResource(id = R.string.language)) },
                    trailing = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    locales.forEach { languageTag ->
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                // set app locale given the user's selected locale
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(languageTag)
                                )
                            },
                            content = {
                                val locale = Locale.forLanguageTag(languageTag)
                                Text(
                                    "${locale.getDisplayLanguage(locale)} (${
                                        locale.getDisplayLanguage(
                                            currentLocale
                                        )
                                    })"
                                )
                            }
                        )
                    }
                }
            }

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
                        Switch(
                            checked = deprecatedShortcutMethodEnabled,
                            onCheckedChange = onDeprecatedShortcutSwitchClicked
                        )
                    }
                }
            )

            ListItem(
                modifier = Modifier.toggleable(
                    value = useLauncherIconToLock,
                    onValueChange = onUseLauncherIconToLockSwitchClicked
                ),
                text = { Text(stringResource(id = R.string.option_use_launcher_icon_to_lock)) },
                secondaryText = { Text(stringResource(id = R.string.option_use_launcher_icon_to_lock_desc)) },
                trailing = {
                    Row {
                        IconButton(onClick = onUseLauncherIconToLockInfoButtonClicked) {
                            Icon(Icons.Filled.Info, stringResource(id = R.string.details))
                        }
                        Switch(
                            checked = useLauncherIconToLock,
                            onCheckedChange = onUseLauncherIconToLockSwitchClicked
                        )
                    }
                }
            )

            if (useLauncherIconToLock) {
                ListItem(
                    modifier = Modifier.toggleable(
                        value = excludeFromRecents,
                        onValueChange = onExcludeFromRecentsSwitchClicked
                    ),
                    text = { Text(stringResource(id = R.string.option_exclude_from_recents_screen)) },
                    secondaryText = { Text(stringResource(id = R.string.option_exclude_from_recents_screen_desc)) },
                    trailing = {
                        Row {
                            IconButton(onClick = onExcludeFromRecentsInfoBtnClicked) {
                                Icon(Icons.Filled.Info, stringResource(id = R.string.details))
                            }
                            Switch(
                                checked = excludeFromRecents,
                                onCheckedChange = onExcludeFromRecentsSwitchClicked
                            )
                        }
                    }
                )
            }

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