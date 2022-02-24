package net.blumia.pineapple.lockscreen.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.blumia.pineapple.lockscreen.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    onBackBtnClicked: () -> Unit = {},
    onDeprecatedShortcutInfoBtnClicked: () -> Unit = {},
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
        Column() {
            val switched by remember { mutableStateOf(true) }
            ListItem(
                modifier = Modifier.toggleable(
                    value = switched,
                    onValueChange = {}
                ),
                text = { Text(stringResource(id = R.string.option_use_compat_method)) },
                secondaryText = { Text(stringResource(id = R.string.option_use_compat_method_short_desc)) },
                trailing = {
                    Row {
                        IconButton(onClick = onDeprecatedShortcutInfoBtnClicked) {
                            Icon(Icons.Filled.Info, stringResource(id = R.string.details))
                        }
                        Switch(checked = switched, onCheckedChange = {})
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