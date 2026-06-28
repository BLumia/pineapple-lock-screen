package net.blumia.pineapple.lockscreen.shortcuts

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.edit
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.lockscreen.MainActivity
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.preferences.PreferencesKeys
import net.blumia.pineapple.lockscreen.shizuku.ShizukuLockScreenManager

class LockScreenShortcut: ShortcutActivity() {
    override fun onOpened() {
        val method = getLockScreenMethod()
        when (method) {
            "shizuku" -> lockScreenWithShizuku()
            else -> lockScreenWithAccessibility()
        }
    }

    private fun getLockScreenMethod(): String {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        return prefs.getString(PreferencesKeys.LOCK_SCREEN_METHOD.name, "accessibility") ?: "accessibility"
    }

    private fun lockScreenWithAccessibility() {
        val a11yService = A11yService.instance()
        if (a11yService != null) {
            a11yService.lockScreen()
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun lockScreenWithShizuku() {
        val manager = ShizukuLockScreenManager.getInstance(this)
        if (manager.isReady()) {
            manager.lockScreen()
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun setupShortcut() {
        val intent = createShortcutResultIntent(
            "shortcut-lockscreen-default-2",
            resources.getString(R.string.shortcut_name_lock),
            R.mipmap.ic_launcher_green_lock
        )

        setResult(
            RESULT_OK,
            intent
        )
    }
}