package net.blumia.pineapple.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Binder
import android.view.accessibility.AccessibilityEvent
import java.lang.ref.WeakReference

class A11yService : AccessibilityService() {
    private var info: AccessibilityServiceInfo = AccessibilityServiceInfo()

    override fun onServiceConnected() {
        mInstance = WeakReference(this)

        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED

            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
//            packageNames = arrayOf("com.example.android.myFirstApp", "com.example.android.mySecondApp")

            // Set the type of feedback your service will provide.
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            // Default services are invoked only if no package-specific ones are present
            // for the type of AccessibilityEvent generated. This service *is*
            // application-specific, so the flag isn't necessary. If this was a
            // general-purpose service, it would be worth considering setting the
            // DEFAULT flag.

            // flags = AccessibilityServiceInfo.DEFAULT;

            notificationTimeout = 100
        }

        this.serviceInfo = info
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mInstance = WeakReference(null)

        return super.onUnbind(intent)
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    fun lockScreen(): Boolean = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)

    fun powerDialog(): Boolean = performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)

    companion object {
        private var mInstance: WeakReference<A11yService> = WeakReference(null)
        fun instance(): A11yService? {
            return mInstance.get()
        }
    }
}