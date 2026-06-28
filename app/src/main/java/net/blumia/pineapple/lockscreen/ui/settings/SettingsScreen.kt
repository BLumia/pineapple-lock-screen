package net.blumia.pineapple.lockscreen.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.intl.Locale as IntlLocale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import net.blumia.pineapple.lockscreen.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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
    onAppIconBtnClicked: () -> Unit = {},
    lockScreenMethod: String = "accessibility",
    onLockScreenMethodChanged: (String) -> Unit = {},
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
            // ==================== Section: Lock Method ====================
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_lock_method))

            var methodExpanded by remember { mutableStateOf(false) }
            val lockMethods = remember {
                listOf(
                    Triple("accessibility", R.string.method_accessibility, R.string.card_a11y_description),
                    Triple("shizuku", R.string.method_shizuku, R.string.card_shizuku_description)
                )
            }
            val selectedMethodLabel = lockMethods.find { it.first == lockScreenMethod }?.second ?: lockMethods[0].second

            ExposedDropdownMenuBox(
                expanded = methodExpanded,
                onExpandedChange = { methodExpanded = it }
            ) {
                ListItem(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    headlineContent = { Text(stringResource(id = R.string.option_lock_screen_method)) },
                    supportingContent = { Text(stringResource(id = R.string.option_lock_screen_method_desc)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(id = selectedMethodLabel),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded)
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = methodExpanded,
                    onDismissRequest = { methodExpanded = false }
                ) {
                    lockMethods.forEach { (key, labelRes, _) ->
                        DropdownMenuItem(
                            onClick = {
                                methodExpanded = false
                                onLockScreenMethodChanged(key)
                            },
                            text = {
                                Text(
                                    stringResource(id = labelRes),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            HorizontalDivider()

            // ==================== Section: General ====================
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_general))

            var langExpanded by remember { mutableStateOf(false) }
            val locales = arrayOf(
                "en", "zh-CN", "zh-HK", "zh-TW",
                "de", "ja", "nl", "pl", "pt-BR", "sv", "tr"
            )
            val currentLocale = AppCompatDelegate.getApplicationLocales()[0]
                ?: Locale.forLanguageTag(IntlLocale.current.toLanguageTag())

            ExposedDropdownMenuBox(
                expanded = langExpanded,
                onExpandedChange = { langExpanded = it }
            ) {
                ListItem(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    headlineContent = { Text(stringResource(id = R.string.language)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = currentLocale.getDisplayName(currentLocale),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded)
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = langExpanded,
                    onDismissRequest = { langExpanded = false }
                ) {
                    locales.forEach { languageTag ->
                        DropdownMenuItem(
                            onClick = {
                                langExpanded = false
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(languageTag)
                                )
                            },
                            text = {
                                val locale = Locale.forLanguageTag(languageTag)
                                Text(
                                    "${locale.getDisplayName(locale)} · ${locale.getDisplayName(currentLocale)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            ListItem(
                modifier = Modifier.clickable { onAppIconBtnClicked() },
                headlineContent = { Text(stringResource(id = R.string.app_icon)) },
                supportingContent = { Text(stringResource(id = R.string.app_icon_desc)) },
                trailingContent = {
                    IconButton(onClick = onAppIconBtnClicked) {
                        Icon(Icons.Filled.Settings, stringResource(id = R.string.app_icon))
                    }
                }
            )

            HorizontalDivider()

            // ==================== Section: Advanced ====================
            SettingsSectionHeader(text = stringResource(id = R.string.settings_section_advanced))

            SwitchSettingItem(
                title = stringResource(id = R.string.option_use_compat_method),
                subtitle = stringResource(id = R.string.option_use_compat_method_short_desc),
                checked = deprecatedShortcutMethodEnabled,
                onCheckedChange = onDeprecatedShortcutSwitchClicked,
                onInfoClicked = onDeprecatedShortcutInfoBtnClicked,
            )

            SwitchSettingItem(
                title = stringResource(id = R.string.option_use_launcher_icon_to_lock),
                subtitle = stringResource(id = R.string.option_use_launcher_icon_to_lock_desc),
                checked = useLauncherIconToLock,
                onCheckedChange = onUseLauncherIconToLockSwitchClicked,
                onInfoClicked = onUseLauncherIconToLockInfoButtonClicked,
            )

            if (useLauncherIconToLock) {
                SwitchSettingItem(
                    title = stringResource(id = R.string.option_exclude_from_recents_screen),
                    subtitle = stringResource(id = R.string.option_exclude_from_recents_screen_desc),
                    checked = excludeFromRecents,
                    onCheckedChange = onExcludeFromRecentsSwitchClicked,
                    onInfoClicked = onExcludeFromRecentsInfoBtnClicked,
                )
            }

            ListItem(
                modifier = Modifier.clickable { onBatteryOptimizationBtnClicked() },
                headlineContent = { Text(stringResource(id = R.string.option_battery_optimization)) },
                supportingContent = { Text(stringResource(id = R.string.option_battery_optimization_short_desc)) },
                trailingContent = {
                    IconButton(onClick = onBatteryOptimizationInfoBtnClicked) {
                        Icon(Icons.Filled.Info, stringResource(id = R.string.details))
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun SwitchSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onInfoClicked: () -> Unit,
) {
    ListItem(
        modifier = Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange
        ),
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        trailingContent = {
            Row {
                IconButton(onClick = onInfoClicked) {
                    Icon(Icons.Filled.Info, stringResource(id = R.string.details))
                }
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    SettingsScreen()
}
