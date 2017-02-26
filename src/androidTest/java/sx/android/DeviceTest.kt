package sx.android

import android.Manifest
import android.content.Context
import android.support.test.runner.AndroidJUnit4
import org.junit.runner.RunWith
import android.hardware.usb.UsbDevice.getDeviceId
import android.content.Context.TELEPHONY_SERVICE
import android.provider.Telephony
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.telephony.TelephonyManager
import org.junit.Test
import android.Manifest.permission
import android.Manifest.permission.READ_PHONE_STATE
import android.app.Instrumentation
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.util.Log
import org.junit.Rule
import android.app.Instrumentation.REPORT_KEY_STREAMRESULT
import android.os.Bundle


/**
 * Created by n3 on 25/02/2017.
 */
@RunWith(AndroidJUnit4::class)
class DeviceTest {
    @get:Rule
    val permissionRule = PermissionRule(Manifest.permission.READ_PHONE_STATE)

    val context by lazy { InstrumentationRegistry.getContext() }
    val device by lazy { this.context }

    @Test
    fun testDevice() {
        println(device)
    }
}