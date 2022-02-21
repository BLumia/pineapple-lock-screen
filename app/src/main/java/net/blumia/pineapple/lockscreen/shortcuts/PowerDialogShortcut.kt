package net.blumia.pineapple.lockscreen.shortcuts

import android.content.Intent
import net.blumia.pineapple.accessibility.A11yService
import net.blumia.pineapple.lockscreen.MainActivity
import net.blumia.pineapple.lockscreen.R

class PowerDialogShortcut: ShortcutActivity() {
    override fun onOpened() {
        val a11yService = A11yService.instance()
        if (a11yService != null) {
            a11yService.powerDialog()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun setupShortcut() {
        val intent = createShortcutResultIntent(
            "shortcut-powerdialog-default-2",
            resources.getString(R.string.shortcut_name_powerdialog),
            R.mipmap.ic_launcher_red_power
        )

        setResult(
            RESULT_OK,
            intent
        )
    }
}