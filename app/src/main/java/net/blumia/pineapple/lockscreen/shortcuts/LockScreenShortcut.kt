package net.blumia.pineapple.lockscreen.shortcuts

import android.content.Intent
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.lockscreen.MainActivity
import net.blumia.pineapple.lockscreen.R

class LockScreenShortcut: ShortcutActivity() {
    override fun onOpened() {
        val a11yService = A11yService.instance()
        if (a11yService != null) {
            a11yService.lockScreen()
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