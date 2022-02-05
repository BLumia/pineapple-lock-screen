package net.blumia.pineapple.lockscreen.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.blumia.pineapple.lockscreen.R

@Composable
fun HomeScreen(
    onOpenA11ySettingsBtnClicked: () -> Unit = {},
    onLockScreenBtnClicked: () -> Unit = {},
    onCreateShortcutBtnClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onLockScreenBtnClicked,
                icon = {
                    Icon(Icons.Filled.Lock, contentDescription = null)
                },
                text = {
                    Text("Lock Screen")
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            AccessibilityPermissionCard(onOpenA11ySettingsBtnClicked)

            CreateShortcutIconCard(onCreateShortcutBtnClicked)
        }
    }
}

@Composable
fun AccessibilityPermissionCard(
    onOpenA11ySettingsBtnClicked: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                style = MaterialTheme.typography.body1,
                text = "This application require to enable its accessibility service in order to work."
            )
            TextButton(onClick = onOpenA11ySettingsBtnClicked) {
                Text(text = "Accessibility Settings")
            }
        }
    }
}

@Composable
fun CreateShortcutIconCard(
    onLockScreenBtnClicked: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                style = MaterialTheme.typography.body1,
                text = "You can create a shortcut icon on your launcher to preform the lock action."
            )
            TextButton(onClick = onLockScreenBtnClicked) {
                Text(text = "Create Shortcut Icon")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AccessibilityPermissionCardPreview() {
    AccessibilityPermissionCard()
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen()
}