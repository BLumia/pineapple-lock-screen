package net.blumia.pineapple.lockscreen.glancewidget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.shortcuts.LockScreenShortcut

class LockScreenWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = Color.Transparent)
//                .appWidgetBackground()
                .padding(8.dp)
                .clickable(actionStartActivity<LockScreenShortcut>()),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Text(
                text = LocalContext.current.getString(R.string.shortcut_name_lock),
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}

class FirstGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LockScreenWidget()
}