package org.deku.leoz.mobile

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.requery.Persistable
import io.requery.reactivex.ReactiveEntityStore
import org.deku.leoz.mobile.config.DatabaseConfiguration
import org.deku.leoz.mobile.data.requery.Models
import org.junit.Test
import org.deku.leoz.mobile.data.requery.AddressEntity
import org.deku.leoz.mobile.model.Address
import org.junit.Assert

/**
 * Some generic requery tests using leoz mobile model/entity classes
 * Created by n3 on 09/12/2016.
 */
class RequeryTest {
    companion object {
        init {
            Kodein.global.addImport(DatabaseConfiguration.module)
        }
    }

    val store: ReactiveEntityStore<Persistable> by Kodein.global.lazy.instance()

    private fun newAddress(): Address {
        val a = Address()
        a.line1 = "1234"
        store.insert(a.entity).blockingGet()
        return a
    }

    @Test
    fun testSelect() {
        val r = Models.REQUERY

        var observedRecords: Int = 0

        store.delete(AddressEntity::class.java)

        newAddress()

        store
                .select(AddressEntity::class.java)
                .get()
                .observableResult()
                .subscribe {
                    println("next ${it.count()}")
                    observedRecords = it.count()
                }

        newAddress()

        Assert.assertEquals(2, observedRecords)
    }

    @Test
    fun testInsert() {
        store.runInTransaction {
            store.delete(AddressEntity::class.java)

            newAddress()

            Assert.assertEquals(1, store.count(AddressEntity::class.java))

            it.transaction().rollback()
        }.blockingGet()
    }
}