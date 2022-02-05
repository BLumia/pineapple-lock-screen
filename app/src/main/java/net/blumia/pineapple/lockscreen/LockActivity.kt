package net.blumia.pineapple.lockscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import net.blumia.pineapple.accessibility.A11yService

class LockActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val a11yService = A11yService.instance()
        if (a11yService != null) {
            a11yService.lockScreen()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}