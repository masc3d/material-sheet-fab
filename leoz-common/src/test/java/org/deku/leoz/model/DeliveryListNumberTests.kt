package org.deku.leoz.model

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest
import sx.log.slf4j.trace

/**
 * Created by phpr on 02.08.2017.
 */
@Category(StandardTest::class)
class DeliveryListNumberTests {
    private val log = LoggerFactory.getLogger(this.javaClass)

    val DEKU_DELIVERYLIST_IDS = listOf(
            "10768797",
            "10768792",
            "10769796",
            "10769988",
            "10769828",
            "10769820",
            "10769813",
            "10769811",
            "10769799",
            "10769797",
            "367750221",
            "367754101",
            "367754095",
            "367752849",
            "367752845",
            "367752816",
            "367752762",
            "367752757",
            "367752552",
            "367752536",
            "367752519",
            "367751694",
            "367751693"
    )

    val DEKU_DELIVERYLIST_LABELS = listOf(
            "0107687890",
            "0107701497",
            "0107701480",
            "0107699909",
            "0107699886",
            "0107699695",
            "0107699626",
            "0107699619",
            "0107699596",
            "0107699572",
            "0107699305",
            "0107698285",
            "0107698209",
            "0107698117",
            "0107691446",
            "0107687920",
            "0107687906",
            "0107687890",
            "0107692139",
            "0107692085",
            "0107691545",
            "0107691484",
            "0107691477",
            "0107691446",
            "0107691385",
            "0107691316",
            "0107691224",
            "0107691149",
            "0107690968",
            "0107690630",
            "0107688033",
            "0107687982",
            "0107687975",
            "0107687920",
            "0107697967",
            "0107699886",
            "0107698285",
            "0107698209",
            "0107698131",
            "0107698117",
            "0107697998",
            "0107697974",
            "3677502211",
            "3677541012",
            "3677540954",
            "3677528495",
            "3677528457",
            "3677528167",
            "3677527627",
            "3677527573",
            "3677525524",
            "3677525364",
            "3677525197",
            "3677516942",
            "3677516935",
            "3677516928",
            "3677516911",
            "3677516904",
            "3677516881",
            "3677516874",
            "3677516867",
            "3677516850",
            "3677516843",
            "3677516829",
            "3677516775",
            "3677516737",
            "3677516720",
            "3677516706",
            "3677516690",
            "3677516270",
            "3677516249",
            "3677516225",
            "3677516218",
            "3677516201",
            "3677516171",
            "3677516164",
            "3677516133",
            "3677514993",
            "3677514863",
            "3677514054",
            "3677514047",
            "3677514016",
            "3677513989",
            "3677513972",
            "3677513903",
            "3677513781",
            "3677513200",
            "3677513156",
            "3677513125",
            "3677513088"
    )

    @Test
    fun testParseLabel() {
        DEKU_DELIVERYLIST_LABELS.forEach { label ->
            val result = DekuDeliveryListNumber.parseLabel(label)

            Assert.assertFalse(result.hasError)
            Assert.assertEquals(result.value.label, label)
        }
    }

    @Test
    fun testParse() {
        DEKU_DELIVERYLIST_IDS.forEach {
            val result = DekuDeliveryListNumber.parse(it)

            Assert.assertFalse(result.hasError)
        }
    }

    @Test
    fun testCreate() {
        Assert.assertEquals("0000123457", DekuDeliveryListNumber.create(12345).label)
    }
}