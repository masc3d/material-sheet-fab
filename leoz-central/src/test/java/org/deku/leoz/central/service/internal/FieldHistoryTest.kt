package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.data.repository.JooqFieldHistoryRepository
import org.deku.leoz.central.data.repository.JooqParcelRepository
import org.deku.leoz.central.data.repository.storeWithHistory
import org.deku.leoz.central.data.repository.storeWithHistoryExportservice
import org.deku.leoz.model.VehicleType
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.StandardTest
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject

@Category(StandardTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [
    DataTestConfiguration::class,
JooqFieldHistoryRepository::class,
JooqParcelRepository::class
    ]
)
class FieldHistoryTest {


    @Inject
    private lateinit var parcelRepository: JooqParcelRepository

    @Test
fun writeWithHistory(){
        val unitRecord = parcelRepository.findParcelByUnitNumber(2041018753)
        unitRecord ?: return

        unitRecord.tournr2+=1
        unitRecord.storeWithHistory(unitRecord.colliebelegnr.toLong(),"WEBB","EXX")
    }

    @Test
    fun writeWithHistoryExportservice(){
        val unitRecord = parcelRepository.findParcelByUnitNumber(2041018753)
        unitRecord ?: return

        unitRecord.tournr2+=1
        unitRecord.bemerkung=if(unitRecord.bemerkung==null)unitRecord.tournr2.toString() else unitRecord.bemerkung+unitRecord.tournr2.toString()
        unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
    }
}

