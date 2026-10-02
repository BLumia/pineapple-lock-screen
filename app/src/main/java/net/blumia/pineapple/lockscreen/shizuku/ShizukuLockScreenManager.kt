package net.blumia.pineapple.lockscreen.shizuku

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Parcel
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.blumia.pineapple.lockscreen.BuildConfig
import rikka.shizuku.Shizuku

sealed class ShizukuLockScreenState {
    object Unavailable : ShizukuLockScreenState()
    object NotGranted : ShizukuLockScreenState()
    object Connecting : ShizukuLockScreenState()
    object Ready : ShizukuLockScreenState()
}

/**
 * Owns the Shizuku-based lock screen backend (binder listeners and the binding
 * to the privileged user service) for the whole app process.
 *
 * Lifecycle: the backend is created as soon as the Shizuku lock screen method
 * is selected and deliberately outlives MainActivity — locking via the
 * launcher icon finishes the activity, and the binding must survive that so
 * the next icon tap still locks instantly. Explicit teardown happens only when
 * the user switches back to the accessibility method (see NavGraph). If this
 * process dies for any other reason, the Shizuku server reaps the user service
 * on its own because the service runs in non-daemon mode.
 */
class ShizukuLockScreenManager private constructor(private val context: Context) {

    private val _state = MutableStateFlow<ShizukuLockScreenState>(ShizukuLockScreenState.Unavailable)
    val state: StateFlow<ShizukuLockScreenState> = _state

    private var serviceConnection: ServiceConnection? = null
    private var serviceBinder: IBinder? = null
    private var userServiceArgs: Shizuku.UserServiceArgs? = null

    /** Non-zero while a bindUserService call is still waiting for onServiceConnected. */
    private var bindingSince = 0L

    private val mainHandler = Handler(Looper.getMainLooper())
    private var pendingRefresh: Runnable? = null

    /**
     * Set when [destroy] is called. Once destroyed, this instance no longer
     * reacts to any callback, so a late binder/death notification can never
     * re-bind (and thus re-spawn) the privileged user service process.
     */
    @Volatile
    private var destroyed = false

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
        destroyed = true
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        pendingRefresh?.let { mainHandler.removeCallbacks(it) }
        pendingRefresh = null
        unbindUserService()
        _state.value = ShizukuLockScreenState.Unavailable
        releaseInstance(this)
    }

    fun refreshState() {
        Log.d(TAG, "Manual refresh state")
        updateState()
    }

    fun lockScreen(): Boolean {
        if (destroyed) return false
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
        if (destroyed) return
        pendingRefresh?.let { mainHandler.removeCallbacks(it) }
        pendingRefresh = Runnable {
            updateState()
            pendingRefresh = null
        }.also {
            mainHandler.postDelayed(it, delayMs)
        }
    }

    private fun updateState() {
        if (destroyed) return
        Log.d(TAG, "updateState called")
        try {
            val binder = Shizuku.getBinder()

            if (binder == null || !binder.pingBinder()) {
                Log.d(TAG, "Binder null or dead, setting Unavailable")
                _state.value = ShizukuLockScreenState.Unavailable
                unbindUserService()
                return
            }

            val permission = Shizuku.checkSelfPermission()

            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted, setting NotGranted")
                _state.value = ShizukuLockScreenState.NotGranted
                unbindUserService()
                return
            }

            bindUserService()
        } catch (e: Exception) {
            Log.e(TAG, "updateState error", e)
            _state.value = ShizukuLockScreenState.Unavailable
            unbindUserService()
        }
    }

    private fun bindUserService() {
        if (destroyed) return

        if (serviceBinder != null) {
            if (serviceBinder!!.pingBinder()) {
                Log.d(TAG, "User service already bound and alive")
                _state.value = ShizukuLockScreenState.Ready
                return
            }
            Log.d(TAG, "Service binder dead, rebinding")
            serviceBinder = null
        }

        // Skip if a bind request is still pending, unless it has been pending for
        // so long that the server-side start timeout (30 seconds) must have fired
        // without onServiceConnected/onServiceDisconnected ever being called.
        val now = SystemClock.elapsedRealtime()
        if (bindingSince != 0L && now - bindingSince < BIND_TIMEOUT_MS) {
            Log.d(TAG, "User service binding already in progress")
            _state.value = ShizukuLockScreenState.Connecting
            return
        }

        val args = userServiceArgs ?: Shizuku.UserServiceArgs(
            ComponentName(context, LockScreenUserService::class.java)
        )
            .tag("lock-screen-v1")
            .processNameSuffix("lock_screen")
            // The user service only needs to outlive this app's process while
            // it is actually bound; non-daemon mode also makes the Shizuku
            // server clean it up if this process dies unexpectedly.
            .daemon(false)
            // Let the server re-create the service process when its
            // implementation changes between app updates.
            .version(BuildConfig.VERSION_CODE)
            .debuggable(BuildConfig.DEBUG)
            .also { userServiceArgs = it }

        // Reuse a single ServiceConnection instance across (re-)binds, so that
        // no connection can leak and only this one needs to be unbound.
        val connection = serviceConnection ?: object : ServiceConnection {
            // Note: Shizuku dispatches onServiceDisconnected when the user
            // service process dies (it links to death internally), so there is
            // no need for a manual IBinder.DeathRecipient here.
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                Log.d(TAG, "User service connected: $name")
                bindingSince = 0L
                if (destroyed) return
                serviceBinder = service
                _state.value = ShizukuLockScreenState.Ready
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Log.d(TAG, "User service disconnected: $name")
                bindingSince = 0L
                if (destroyed) return
                serviceBinder = null
                // A re-bind is scheduled below; if Shizuku itself is dead,
                // updateState will switch the state to Unavailable instead.
                _state.value = ShizukuLockScreenState.Connecting
                scheduleRefresh(500)
            }
        }.also { serviceConnection = it }

        Log.d(TAG, "Binding user service")
        bindingSince = now
        _state.value = ShizukuLockScreenState.Connecting
        try {
            Shizuku.bindUserService(args, connection)
        } catch (e: Exception) {
            Log.e(TAG, "bindUserService failed", e)
            bindingSince = 0L
            _state.value = ShizukuLockScreenState.Unavailable
        }
    }

    private fun unbindUserService() {
        val args = userServiceArgs
        val connection = serviceConnection

        userServiceArgs = null
        serviceBinder = null
        serviceConnection = null
        bindingSince = 0L

        if (args == null || connection == null) return

        try {
            // remove=true asks the Shizuku server to invoke the reserved destroy
            // transaction (LockScreenUserService.TRANSACTION_destroy) on the user
            // service so the privileged process exits itself, and to drop its
            // server-side service record.
            Shizuku.unbindUserService(args, connection, true)
        } catch (e: Exception) {
            // Ignore (e.g. the Shizuku server itself is already dead)
        }
    }

    companion object {
        private const val TAG = "ShizukuLockScreenMgr"

        /** Matches the Shizuku server-side user service start timeout (30s), plus margin. */
        private const val BIND_TIMEOUT_MS = 35_000L

        @Volatile
        private var INSTANCE: ShizukuLockScreenManager? = null

        fun getInstance(context: Context): ShizukuLockScreenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ShizukuLockScreenManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        /** Returns the current instance, if any, without creating one. */
        fun peekInstance(): ShizukuLockScreenManager? = INSTANCE

        private fun releaseInstance(instance: ShizukuLockScreenManager) {
            synchronized(this) {
                if (INSTANCE === instance) {
                    INSTANCE = null
                }
            }
        }
    }
}
