package net.blumia.pineapple.lockscreen.shortcuts

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.preferences.PreferencesKeys
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
            useDeprecatedMethod = applicationContext.booleanPreference(PreferencesKeys.DEPRECATED_SHORTCUT_METHOD).firstOrNull()!!
        }
        return if (useDeprecatedMethod || shortcutManager == null) {
            // Seems no longer works with Android 13. Anyway...
            val shortcutIntent = Intent(this, this::class.java)
            // val shortcutIntent: Intent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent(this, this::class.java)
            Intent().putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, shortLabel)
                .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, iconResource));
        } else {
            val classRef = this::class.java
            runBlocking {
                val pinShortcutInfo = ShortcutInfo.Builder(applicationContext, shortcutId)
                    .setShortLabel(shortLabel)
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