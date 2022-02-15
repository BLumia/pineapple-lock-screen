package net.blumia.pineapple.lockscreen

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.runBlocking


class ShortcutSetupWizardActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shortcutManager =
            ContextCompat.getSystemService(applicationContext, ShortcutManager::class.java)

        if (shortcutManager == null) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        runBlocking {
            val pinShortcutInfo = ShortcutInfo.Builder(applicationContext, "shortcut-lockscreen-default-2")
                .setShortLabel(resources.getString(R.string.shortcut_name_lock))
                .setIcon(android.graphics.drawable.Icon.createWithResource(applicationContext, R.mipmap.ic_launcher))
                .setIntent(Intent(Intent.ACTION_VIEW, null, applicationContext, LockActivity::class.java))
                .build()

            setResult(RESULT_OK, shortcutManager.createShortcutResultIntent(pinShortcutInfo))
        }
        finish()
    }
}