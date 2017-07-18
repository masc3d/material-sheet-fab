package org.deku.leoz.mobile

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.config.DatabaseTestConfiguration
import org.deku.leoz.mobile.model.entity.AddressEntity
import org.junit.Test
import org.junit.Assert

/**
 * Some generic requery tests using leoz mobile model/entity classes
 * Created by n3 on 09/12/2016.
 */
class RequeryTest {
    companion object {
        init {
            Kodein.global.addImport(DatabaseTestConfiguration.module)
        }
    }

    val store: KotlinReactiveEntityStore<Persistable> by Kodein.global.lazy.instance()

    private fun newAddress(): AddressEntity {
        val a = AddressEntity()
        a.line1 = "1234"
        store.insert(a).blockingGet()
        return a
    }

    @Test
    fun testSelect() {
        var observedRecords: Int = 0

        store.delete(AddressEntity::class)

        newAddress()

        store
                .select(AddressEntity::class)
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

        store.withTransaction {
            store.delete(AddressEntity::class).get().call()

            newAddress()
//            newAddress()

//            Assert.assertEquals(1, store.count(Address2::class).get().call())

            //(store.toBlocking() as KotlinEntityDataStore).data.transaction().rollback()
        }.blockingGet()
    }

    @Test
    fun testDelete() {
        store.delete(AddressEntity::class)
    }
}