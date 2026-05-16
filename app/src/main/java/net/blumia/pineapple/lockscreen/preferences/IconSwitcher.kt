package net.blumia.pineapple.lockscreen.preferences

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import net.blumia.pineapple.lockscreen.R

enum class IconColor(val key: String, val labelResId: Int, val backgroundResId: Int) {
    GREEN("green", R.string.icon_color_green, R.drawable.ic_launcher_background_green),
    BLUE("blue", R.string.icon_color_blue, R.drawable.ic_launcher_background_blue),
    RED("red", R.string.icon_color_red, R.drawable.ic_launcher_background_red),
    ORANGE("orange", R.string.icon_color_orange, R.drawable.ic_launcher_background_orange),
    PURPLE("purple", R.string.icon_color_purple, R.drawable.ic_launcher_background_purple),
    PINK("pink", R.string.icon_color_pink, R.drawable.ic_launcher_background_pink),
    CYAN("cyan", R.string.icon_color_cyan, R.drawable.ic_launcher_background_cyan),
    INDIGO("indigo", R.string.icon_color_indigo, R.drawable.ic_launcher_background_indigo),
    YELLOW("yellow", R.string.icon_color_yellow, R.drawable.ic_launcher_background_yellow);

    companion object {
        fun fromKey(key: String?): IconColor {
            return entries.find { it.key == key } ?: GREEN
        }
    }
}

enum class IconStyle(val key: String, val labelResId: Int, val foregroundResId: Int, val displayIconResId: Int) {
    LOCK("lock", R.string.icon_style_lock, R.drawable.ic_launcher_foreground_lock, R.drawable.ic_baseline_lock_24),
    POWER("power", R.string.icon_style_power, R.drawable.ic_launcher_foreground_power, R.drawable.ic_baseline_power_settings_new_24);

    companion object {
        fun fromKey(key: String?): IconStyle {
            return entries.find { it.key == key } ?: LOCK
        }
    }
}

object IconSwitcher {
    private fun getAliasName(color: IconColor, style: IconStyle): String {
        return ".MainActivityAlias${color.name.lowercase().replaceFirstChar { it.uppercase() }}${style.name.lowercase().replaceFirstChar { it.uppercase() }}"
    }

    fun switchIcon(context: Context, color: IconColor, style: IconStyle) {
        val pm = context.packageManager
        val packageName = context.packageName

        for (c in IconColor.entries) {
            for (s in IconStyle.entries) {
                val aliasName = getAliasName(c, s)
                val component = ComponentName(packageName, "$packageName$aliasName")
                val newState = if (c == color && s == style) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }
                pm.setComponentEnabledSetting(
                    component,
                    newState,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }

    fun getBackgroundResId(color: IconColor): Int = color.backgroundResId

    fun getForegroundResId(style: IconStyle): Int = style.foregroundResId
}
