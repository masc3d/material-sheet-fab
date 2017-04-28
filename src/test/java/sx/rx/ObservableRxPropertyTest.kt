package sx.rx

import org.junit.Test
import java.util.*

/**
 * Created by masc on 28.04.17.
 */
class ObservableRxPropertyTest {
    val valueProperty = ObservableRxProperty<String?>("default")
    var value by valueProperty

    @Test
    fun testNulls() {
        this.valueProperty.subscribe {
            println("Changed to ${it.value}")
        }

        this.value = null
    }
}