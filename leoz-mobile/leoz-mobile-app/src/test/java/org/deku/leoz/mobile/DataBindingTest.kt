package org.deku.leoz.mobile

import android.databinding.Observable
import android.databinding.ObservableField
import org.deku.leoz.mobile.model.entity.Address
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.Stopwatch

/**
 * Created by masc on 11.07.17.
 */
class DataBindingTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testObservableField() {
        val field = ObservableField("hello")

        field.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                log.info("CHANGED")
            }
        })

        field.set("test")
    }

    @Test
    fun testObservableFieldWithEntity() {
        val a = Address()

        a.line1Field.subscribe {
            log.info("CHANGED! [${it.value}]")
        }

        val sw = Stopwatch.createStarted()
        for (i in 0..100000) {
            a.line1 = "TEST${i}"
        }
        log.info("${sw}")
    }

    @Test
    fun testEntityInstantiationTime() {
        val sw = Stopwatch.createStarted()
        for (i in 0..10000000) {
            Address()
        }
        log.info("${sw}")
    }
}