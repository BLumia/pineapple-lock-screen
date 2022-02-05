package net.blumia.pineapple.lockscreen

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.service.quicksettings.TileService

class QuickSettingService: TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, LockActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        startActivityAndCollapse(intent)
    }
}