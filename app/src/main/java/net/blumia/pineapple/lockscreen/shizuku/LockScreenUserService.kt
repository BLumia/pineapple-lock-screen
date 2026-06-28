package net.blumia.pineapple.lockscreen.shizuku

import android.os.Binder
import android.os.Parcel
import android.os.SystemClock
import android.view.KeyEvent

class LockScreenUserService : Binder() {

    companion object {
        const val INTERFACE_DESCRIPTOR = "net.blumia.pineapple.shizuku.LockScreenUserService"
        const val TRANSACTION_lock = FIRST_CALL_TRANSACTION
        const val TRANSACTION_destroy = 16777115
    }

    override fun onTransact(code: Int, data: android.os.Parcel, reply: android.os.Parcel?, flags: Int): Boolean {
        when (code) {
            INTERFACE_TRANSACTION -> {
                reply?.writeString(INTERFACE_DESCRIPTOR)
                return true
            }
            TRANSACTION_lock -> {
                data.enforceInterface(INTERFACE_DESCRIPTOR)
                lockScreen()
                reply?.writeNoException()
                return true
            }
            TRANSACTION_destroy -> {
                destroy()
                reply?.writeNoException()
                return true
            }
            else -> return super.onTransact(code, data, reply, flags)
        }
    }

    private fun lockScreen() {
        try {
            lockScreenViaPowerManager()
        } catch (e: Exception) {
            lockScreenViaInputInjection()
        }
    }

    private fun lockScreenViaPowerManager() {
        val serviceManagerClass = Class.forName("android.os.ServiceManager")
        val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
        val powerManagerBinder = getServiceMethod.invoke(null, "power")
        val powerManagerStubClass = Class.forName("android.os.IPowerManager\$Stub")
        val asInterfaceMethod = powerManagerStubClass.getMethod("asInterface", Binder::class.java)
        val powerManager = asInterfaceMethod.invoke(null, powerManagerBinder)
        val lockMethod = powerManager.javaClass.getMethod("lock", String::class.java)
        lockMethod.invoke(powerManager, null)
    }

    private fun lockScreenViaInputInjection() {
        try {
            val inputManagerClass = Class.forName("android.hardware.input.InputManager")
            val getInstanceMethod = inputManagerClass.getMethod("getInstance")
            val inputManager = getInstanceMethod.invoke(null)
            val event = KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_SLEEP,
                0
            )
            val injectInputEventMethod = inputManagerClass.getMethod(
                "injectInputEvent",
                KeyEvent::class.java,
                Int::class.javaPrimitiveType!!
            )
            injectInputEventMethod.invoke(inputManager, event, 0)
        } catch (e: Exception) {
            lockScreenViaShellCommand()
        }
    }

    private fun lockScreenViaShellCommand() {
        Runtime.getRuntime().exec(arrayOf("input", "keyevent", "223"))
    }

    private fun destroy() {
        System.exit(0)
    }
}
