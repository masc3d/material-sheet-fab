package sx.time

import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by masc on 17/03/2017.
 */
class DateTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testReplaceDate() {
        val calDate = GregorianCalendar()
        calDate.clear()
        calDate.set(2017, Calendar.FEBRUARY, 2)

        val calTime = GregorianCalendar()
        calTime.clear()
        calTime.set(1932, Calendar.JANUARY, 1, 13, 30, 30)

        val time = calTime.time
        val replaced = time.replaceDate(calDate.time)

        val calReplaced = GregorianCalendar()
        calReplaced.clear()
        calReplaced.time = replaced

        val calExpected = GregorianCalendar()
        calExpected.clear()
        calExpected.set(2017, Calendar.FEBRUARY, 2, 13, 30, 30)
        Assert.assertEquals(calReplaced, calExpected)
    }

    @Test
    fun testReplaceTime() {
        val calTime= GregorianCalendar()
        calTime.clear()
        calTime.set(0, 0, 0, 13, 30, 30)

        val calDate= GregorianCalendar()
        calDate.clear()
        calDate.set(2017, Calendar.FEBRUARY, 2, 0, 0, 0)

        val date = calDate.time
        val replaced = date.replaceTime(calTime.time)

        val calReplaced = GregorianCalendar()
        calReplaced.clear()
        calReplaced.time = replaced

        val calExpected = GregorianCalendar()
        calExpected.clear()
        calExpected.set(2017, Calendar.FEBRUARY, 2, 13, 30, 30)
        Assert.assertEquals(calReplaced, calExpected)
    }
}