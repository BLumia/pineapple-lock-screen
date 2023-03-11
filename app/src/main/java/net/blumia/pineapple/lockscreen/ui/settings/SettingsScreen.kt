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
import androidx.compose.material.icons.filled.ArrowBack
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
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
        ) {
            var expanded by remember { mutableStateOf(false) }
            val locales = arrayOf( // should aligns to locale_config.xml
                "en",
                "zh-CN",
                "de",
                "nl",
                "pt-BR"
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