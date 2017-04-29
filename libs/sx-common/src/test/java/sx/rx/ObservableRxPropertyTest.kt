package sx.rx

import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by masc on 28.04.17.
 */
class ObservableRxPropertyTest {
    val valueProperty = ObservableRxProperty<String?>("default")
    var value by valueProperty

    init {
        this.valueProperty.subscribe {
            println("[${Thread.currentThread().id.toString(16)}] Changed to ${it.value}")
        }
    }

    @Test
    fun testNulls() {
        this.value = null
    }

    @Test
    fun testThreaded() {
        task<Unit> {
            this@ObservableRxPropertyTest.value = "test"
        }
                .toHotReplay(scheduler = Schedulers.computation())
                .blockingSubscribe()
    }
}