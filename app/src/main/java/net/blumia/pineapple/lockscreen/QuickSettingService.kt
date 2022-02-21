package net.blumia.pineapple.lockscreen

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.service.quicksettings.TileService
import net.blumia.pineapple.lockscreen.shortcuts.LockScreenShortcut

class QuickSettingService: TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, LockScreenShortcut::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        startActivityAndCollapse(intent)
    }
}