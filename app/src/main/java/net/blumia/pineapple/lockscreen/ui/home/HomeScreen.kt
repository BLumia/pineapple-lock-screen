package net.blumia.pineapple.lockscreen.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.blumia.pineapple.lockscreen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    showDialog: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onOpenA11ySettingsBtnClicked: () -> Unit = {},
    onLockScreenBtnClicked: () -> Unit = {},
    onOpenQuickSettingsBtnClicked: () -> Unit = {},
    onLockScreenBtnLongPressed: () -> Unit = {},
    onCreateShortcutBtnClicked: () -> Unit = {},
    onActionSettingsClicked: () -> Unit = {},
    onActionAboutClicked: () -> Unit = {},
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    var expanded by remember { mutableStateOf(false)}
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.menu))
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.settings), style = MaterialTheme.typography.bodyLarge) },
                            onClick = onActionSettingsClicked
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.about), style = MaterialTheme.typography.bodyLarge) },
                            onClick = onActionAboutClicked
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onLockScreenBtnClicked,
                icon = {
                    Icon(Icons.Filled.Lock, contentDescription = null)
                },
                text = {
                    Text(stringResource(id = R.string.fab_lock_screen))
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MainScreenCard(
                descriptionText = stringResource(id = R.string.card_a11y_description),
                actionText = stringResource(id = R.string.card_a11y_action_go_to_setting),
                onActionClicked = onOpenA11ySettingsBtnClicked
            )

            MainScreenCard(
                descriptionText = stringResource(id = R.string.card_shortcut_description),
                actionText = stringResource(id = R.string.card_shortcut_action_create_shortcut),
                onActionClicked = onCreateShortcutBtnClicked
            )

            if (LocalContext.current.resources.getBoolean(R.bool.supportQuickSettingsTile)) {
                MainScreenCard(
                    descriptionText = stringResource(id = R.string.card_qstile_description),
                    actionText = stringResource(id = R.string.card_qstile_action_open_qs),
                    onActionClicked = onOpenQuickSettingsBtnClicked
                )
            }
        }
    }
}

@Composable
fun MainScreenCard(
    descriptionText: String,
    actionText: String,
    onActionClicked: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge,
                text = descriptionText
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(onClick = onActionClicked) {
                    Text(text = actionText)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainScreenCardPreview() {
    MainScreenCard(
        descriptionText = stringResource(id = R.string.card_a11y_description),
        actionText = stringResource(id = R.string.card_a11y_action_go_to_setting),
    )
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen()
}