package net.blumia.pineapple.lockscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.accessibility.openSystemA11ySettings
import net.blumia.pineapple.lockscreen.ui.theme.PineappleLockScreenTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PineappleLockScreenTheme {
                Scaffold() {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = MaterialTheme.colors.background
                    ) {
                        AccessibilityPermissionCard(
                            onOpenA11ySettingsBtnClicked = {
                                openSystemA11ySettings(applicationContext)
                            },
                            onLockScreenBtnClicked = {
                                A11yService.instance()?.lockScreen()
                            }
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun AccessibilityPermissionCard(
    onOpenA11ySettingsBtnClicked: () -> Unit,
    onLockScreenBtnClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column() {
                Text(text = "This application require to enable its accessibility service in order to work.")
                Button(onClick = onOpenA11ySettingsBtnClicked) {
                    Text(text = "Require")
                }
            }
        }

        Button(onClick = onLockScreenBtnClicked) {
            Text(text = "Lock Screen")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AccessibilityPermissionCardPreview() {
    AccessibilityPermissionCard({},{})
}