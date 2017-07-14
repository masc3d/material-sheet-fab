package org.deku.leoz.mobile

import android.databinding.Observable
import android.databinding.ObservableField
import org.junit.Test
import org.slf4j.LoggerFactory

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
}