package net.blumia.pineapple.lockscreen.shizuku

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Parcel
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rikka.shizuku.Shizuku

sealed class ShizukuLockScreenState {
    object Unavailable : ShizukuLockScreenState()
    object NotGranted : ShizukuLockScreenState()
    object Connecting : ShizukuLockScreenState()
    object Ready : ShizukuLockScreenState()
}

class ShizukuLockScreenManager private constructor(private val context: Context) {

    private val _state = MutableStateFlow<ShizukuLockScreenState>(ShizukuLockScreenState.Unavailable)
    val state: StateFlow<ShizukuLockScreenState> = _state

    private var serviceConnection: ServiceConnection? = null
    private var serviceBinder: IBinder? = null
    private var userServiceArgs: Shizuku.UserServiceArgs? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private var pendingRefresh: Runnable? = null

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        Log.d(TAG, "Binder received, scheduling delayed state update")
        scheduleRefresh(100)
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        Log.d(TAG, "Binder dead, scheduling delayed state update")
        scheduleRefresh(500)
    }

    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        Log.d(TAG, "Permission result: requestCode=$requestCode, grantResult=$grantResult")
        scheduleRefresh(300)
    }

    init {
        Log.d(TAG, "ShizukuLockScreenManager init")
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        updateState()
    }

    fun destroy() {
        Log.d(TAG, "ShizukuLockScreenManager destroy")
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        pendingRefresh?.let { mainHandler.removeCallbacks(it) }
        unbindUserService()
    }

    fun refreshState() {
        Log.d(TAG, "Manual refresh state")
        updateState()
    }

    fun lockScreen(): Boolean {
        val binder = serviceBinder ?: return false
        var data: Parcel? = null
        var reply: Parcel? = null
        return try {
            data = Parcel.obtain()
            reply = Parcel.obtain()
            data.writeInterfaceToken(LockScreenUserService.INTERFACE_DESCRIPTOR)
            binder.transact(
                LockScreenUserService.TRANSACTION_lock,
                data,
                reply,
                0
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "lockScreen failed", e)
            false
        } finally {
            data?.recycle()
            reply?.recycle()
        }
    }

    fun requestPermission(requestCode: Int) {
        Log.d(TAG, "Requesting permission: requestCode=$requestCode")
        Shizuku.requestPermission(requestCode)
    }

    fun isAvailable(): Boolean {
        return _state.value !is ShizukuLockScreenState.Unavailable
    }

    fun isReady(): Boolean {
        return _state.value is ShizukuLockScreenState.Ready
    }

    fun isShizukuInstalled(): Boolean {
        return try {
            context.packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api") != null
        } catch (e: Exception) {
            false
        }
    }

    private fun scheduleRefresh(delayMs: Long) {
        pendingRefresh?.let { mainHandler.removeCallbacks(it) }
        pendingRefresh = Runnable {
            updateState()
            pendingRefresh = null
        }.also {
            mainHandler.postDelayed(it, delayMs)
        }
    }

    private fun updateState() {
        Log.d(TAG, "updateState called")
        try {
            val binder = Shizuku.getBinder()
            Log.d(TAG, "Shizuku binder: ${binder != null}, ping: ${binder?.pingBinder()}")
            
            if (binder == null || !binder.pingBinder()) {
                Log.d(TAG, "Binder null or dead, setting Unavailable")
                _state.value = ShizukuLockScreenState.Unavailable
                unbindUserService()
                return
            }

            val permission = Shizuku.checkSelfPermission()
            Log.d(TAG, "Shizuku permission: $permission")
            
            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted, setting NotGranted")
                _state.value = ShizukuLockScreenState.NotGranted
                unbindUserService()
                return
            }

            Log.d(TAG, "Permission granted, binding user service")
            _state.value = ShizukuLockScreenState.Connecting
            bindUserService()
        } catch (e: Exception) {
            Log.e(TAG, "updateState error", e)
            _state.value = ShizukuLockScreenState.Unavailable
            unbindUserService()
        }
    }

    private fun bindUserService() {
        if (serviceBinder != null) {
            Log.d(TAG, "User service already bound")
            // Verify the binder is still alive
            if (serviceBinder?.pingBinder() == true) {
                return
            } else {
                Log.d(TAG, "Service binder dead, rebinding")
                serviceBinder = null
            }
        }

        Log.d(TAG, "Binding user service")
        val componentName = ComponentName(context, LockScreenUserService::class.java)
        val args = Shizuku.UserServiceArgs(componentName)
            .tag("lock-screen-v1")
            .processNameSuffix("lock_screen")
        userServiceArgs = args

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                Log.d(TAG, "User service connected: $name")
                serviceBinder = service
                service.linkToDeath(object : IBinder.DeathRecipient {
                    override fun binderDied() {
                        Log.d(TAG, "User service died")
                        serviceBinder = null
                        _state.value = ShizukuLockScreenState.NotGranted
                        scheduleRefresh(500)
                    }
                }, 0)
                _state.value = ShizukuLockScreenState.Ready
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Log.d(TAG, "User service disconnected: $name")
                serviceBinder = null
                _state.value = ShizukuLockScreenState.NotGranted
                scheduleRefresh(500)
            }
        }

        serviceConnection = connection
        try {
            Shizuku.bindUserService(args, connection)
        } catch (e: Exception) {
            Log.e(TAG, "bindUserService failed", e)
            _state.value = ShizukuLockScreenState.Unavailable
            serviceConnection = null
        }
    }

    private fun unbindUserService() {
        val args = userServiceArgs ?: return

        serviceBinder?.let { binder ->
            try {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                data.writeInterfaceToken(LockScreenUserService.INTERFACE_DESCRIPTOR)
                binder.transact(LockScreenUserService.TRANSACTION_destroy, data, reply, 0)
                data.recycle()
                reply.recycle()
            } catch (e: Exception) {
                // Ignore
            }
        }

        serviceConnection?.let {
            try {
                Shizuku.unbindUserService(args, it, false)
            } catch (e: Exception) {
                // Ignore
            }
        }
        serviceBinder = null
        serviceConnection = null
        userServiceArgs = null
    }

    companion object {
        private const val TAG = "ShizukuLockScreenMgr"

        @Volatile
        private var INSTANCE: ShizukuLockScreenManager? = null

        fun getInstance(context: Context): ShizukuLockScreenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ShizukuLockScreenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
