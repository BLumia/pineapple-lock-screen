package net.blumia.pineapple.accessibility

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

fun openSystemA11ySettings(applicationContext: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putComponent(applicationContext.packageName, A11yService::class.java)
    applicationContext.startActivity(intent)
}

private fun Intent.putComponent(pkg: String, cls: Class<*>) {
    val cs = ComponentName(pkg, cls.name).flattenToString()
    val bundle = Bundle()
    bundle.putString(":settings:fragment_args_key", cs)
    putExtra(":settings:fragment_args_key", cs)
    putExtra(":settings:show_fragment_args", bundle)
}