package org.deku.leoz.model

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import sx.junit.StandardTest

/**
 * Created by masc on 05.07.17.
 */
@Category(StandardTest::class)
class UnitNumberTests {
    val BACKSEAL_LABELS = listOf(
            "900202741551",
            "900202816839",
            "900202742190"
    )

    val MAINSEAL_LABELS = listOf(
            "900102602617",
            "900102600309",
            "900102602853"
    )

    val GLS_UNIT_LABELS = listOf(
            "458515041831",
            "458515041824",
            "458515041770"
    )

    val DEKU_GLS_UNIT_LABELS = listOf(
            "845515041830",
            "845515041823",
            "845515041779"
    )

    val DEKU_VARIOUS = listOf(
            "310500107933",
            "195500023533",
            "202500222054",
            "202500222061",
            "028500247053",
            "774500047819",
            "848580833102",
            "854556638411",
            "072804645619",
            "854558777811",
            "634071336630",
            "515200398708",
            "515200398913",
            "406500073924",
            "406500073917",
            "634071336852",
            "681723502516",
            "830593661575",
            "830593661582",
            "840541694210",
            "854511281805",
            "527061609756",
            "527061611889",
            "865560593016",
            "873502724367",
            "527130327888",
            "888505104689",
            "055040822860",
            "010605306612",
            "010605307190",
            "010605307244",
            "045800400087",
            "045800400094",
            "045800400100",
            "045800400117",
            "045800400124",
            "045800400131",
            "045800400148",
            "045800400155",
            "045800400162",
            "045800400179",
            "045800400186",
            "045800400193",
            "045800400209",
            "045800400216",
            "045800400223",
            "045800400230",
            "045800400247",
            "036801331623",
            "036801331630",
            "036801331647",
            "036801331654",
            "751807059509",
            "220006123071",
            "600243116003",
            "033921091086",
            "020810085752",
            "260200674979",
            "055040825175",
            "854558650794",
            "551500005779",
            "551500005786",
            "551500005762",
            "551500005755",
            "507501036861",
            "486500060186",
            "788500002761",
            "617500196982",
            "617500196999",
            "617500197002",
            "788500002778",
            "100713215545",
            "237500239542",
            "050502475349",
            "247500385934",
            "247500385941",
            "247500385958",
            "247500385965",
            "247500385972",
            "247500385989",
            "028500247183",
            "499500172414",
            "063504006237",
            "320500199609",
            "320500199616",
            "204500072370",
            "204500072387",
            "204500072394",
            "204500072400",
            "204500072417",
            "204500072424",
            "100713216733",
            "100713216740",
            "100713216757",
            "841553343806"
    )

    val DEKU_BAGS = listOf(
            "100713216733",
            "100713216740",
            "100713216757"
    )

    @Test
    fun testGlsUnitNumbers() {
        // Test GLS unit numbers
        GLS_UNIT_LABELS.forEach {
            // Test parsing
            val gun = GlsUnitNumber.parseLabel(it).value

            // Test conversion & gls flag
            Assert.assertEquals(true, gun.toUnitNumber().isGlsParcel)
        }
    }

    @Test
    fun testUnitNumbers() {
        DEKU_VARIOUS.forEach {
            val un = UnitNumber.parseLabel(it).value
        }
    }

    @Test
    fun testDekuGlsUnitNumbers() {
        DEKU_GLS_UNIT_LABELS.forEach {
            val un = UnitNumber.parseLabel(it).value

            Assert.assertEquals(UnitNumber.Type.Parcel, un.type)
            Assert.assertEquals(true, un.isGlsParcel)
        }
    }

    @Test
    fun testSealUnitNumbers() {
        // Test main seals
        MAINSEAL_LABELS.forEach {
            val un = UnitNumber.parseLabel(it).value

            Assert.assertEquals(UnitNumber.Type.MainSeal, un.type)
        }

        BACKSEAL_LABELS.forEach {
            val un = UnitNumber.parseLabel(it).value

            Assert.assertEquals(UnitNumber.Type.BackSeal, un.type)
        }
    }

    @Test
    fun testUnitNumberTypeFilter() {
        DEKU_VARIOUS.forEach {
            val un = UnitNumber.parseLabel(it)
            if (!un.assertAny(UnitNumber.Type.Parcel).hasError) {
                Assert.assertEquals(un.value.type, UnitNumber.Type.Parcel)
            }
        }
    }
}