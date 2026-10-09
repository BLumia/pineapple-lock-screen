package net.blumia.pineapple.lockscreen.shortcuts

import android.content.Intent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.lockscreen.MainActivity
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.preferences.PreferencesKeys
import net.blumia.pineapple.lockscreen.preferences.stringPreference
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
        // Read the same DataStore-backed preference that the settings UI
        // writes to. A blocking read is acceptable here: this is a short-lived
        // no-display activity, and MainActivity does the same for icon locking.
        return runBlocking {
            applicationContext.stringPreference(
                PreferencesKeys.LOCK_SCREEN_METHOD, "accessibility"
            ).first()
        }
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