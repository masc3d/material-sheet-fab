package sx

import org.junit.Assert
import org.junit.Test

/**
 * Created by masc on 20/08/16.
 */
class LazyInstanceJavaTest {
    internal inner class TestClass

    @Test
    fun testInitNull() {
        val li = LazyInstance({ null })

        Assert.assertNull(li.get())
    }
}
