package org.deku.leoz.mobile.update

import org.junit.Assert
import org.junit.Test

/**
 * Created by n3 on 19/02/2017.
 */
class ApplicationPackageNameTest {
    @Test
    fun testToString() {
        val bundleName = "leoz-mobile"
        val version = "0.1-SNAPSHOT"

        val apkName = UpdateService.ApplicationPackageName(bundleName = bundleName, version = version)

        Assert.assertEquals(apkName.toString(), "leoz-mobile-0.1-SNAPSHOT.apk")
    }

    @Test
    fun testParse() {
        val filename = "leoz-mobile-0.1-SNAPSHOT.apk"

        val apkName = UpdateService.ApplicationPackageName.parse(filename)
        Assert.assertEquals(apkName.bundleName, "leoz-mobile")
        Assert.assertEquals(apkName.version, "0.1-SNAPSHOT")
    }
}