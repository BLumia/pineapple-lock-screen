package net.blumia.pineapple.lockscreen

import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.service.quicksettings.TileService
import net.blumia.pineapple.lockscreen.shortcuts.LockScreenShortcut

class QuickSettingService: TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, LockScreenShortcut::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE))
        } else {
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}