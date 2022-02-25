package net.blumia.pineapple.lockscreen.shortcuts

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.preferences.P_DEPRECATED_SHORTCUT_METHOD
import net.blumia.pineapple.lockscreen.preferences.booleanPreference

abstract class ShortcutActivity: Activity() {
    abstract fun onOpened()
    open fun setupShortcut() {
        setResult(RESULT_CANCELED)
    }

    protected fun createShortcutResultIntent(
        shortcutId: String,
        shortLabel: String,
        iconResource: Int,
    ): Intent {
        val shortcutManager = ContextCompat.getSystemService(applicationContext, ShortcutManager::class.java)
        val useDeprecatedMethod: Boolean
        runBlocking {
            useDeprecatedMethod = applicationContext.booleanPreference(P_DEPRECATED_SHORTCUT_METHOD, false).firstOrNull()!!
        }
        return if (useDeprecatedMethod || shortcutManager == null) {
            Intent().putExtra(Intent.EXTRA_SHORTCUT_INTENT, Intent(this, this::class.java))
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, shortLabel)
                .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, iconResource));
        } else {
            val classRef = this::class.java
            runBlocking {
                val pinShortcutInfo = ShortcutInfo.Builder(applicationContext, shortcutId)
                    .setShortLabel(resources.getString(R.string.shortcut_name_lock))
                    .setIcon(Icon.createWithResource(applicationContext, iconResource))
                    .setIntent(Intent(Intent.ACTION_VIEW, null, applicationContext, classRef))
                    .build()

                shortcutManager.createShortcutResultIntent(pinShortcutInfo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.action == Intent.ACTION_CREATE_SHORTCUT) {
            setupShortcut()
        } else {
            onOpened()
        }
        finish()
    }
}